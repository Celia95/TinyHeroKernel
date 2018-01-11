package com.raincoatmoon.Core;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.raincoatmoon.EnvVariables;
import com.raincoatmoon.Keyboards.KeyboardData;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CommandManager implements ActionSubscriber {
    private PublishSubject<CommandMessage> commandStream;
    private PublishSubject<ExtendedFile> fileStream;
    private PublishSubject<ExtendedFile> processedFileStream;
    private PublishSubject<Message> stringStream;
    private final Map<String, BotAction> actions = new TreeMap<>();
    private final Map<String, BotAction> privateActions = new HashMap<>();
    private final Map<Integer, BotActionGenerator> volatileActions = new HashMap<>();
    private final Map<Long, BotActionFromString> volatileKeyboardActions = new HashMap<>();
    private final Map<Long, BotActionGenerator> volatileInlineKeyboardActions = new HashMap<>();

    private Retrofit retrofit;

    public CommandManager() {
        commandStream = PublishSubject.create();
        commandStream.subscribeOn(Schedulers.trampoline())
                .subscribe(cmd -> {
                    Observable.just(cmd).subscribeOn(Schedulers.newThread())
                            .subscribe(this::dispatch, (Throwable e) -> {
                                e.printStackTrace();
                            });
                });
        stringStream = PublishSubject.create();
        stringStream.subscribeOn(Schedulers.trampoline())
                .subscribe(s -> {
                    Observable.just(s).subscribeOn(Schedulers.newThread())
                            .subscribe(this::dispatch, (Throwable e) -> {
                                e.printStackTrace();
                            });
                });
        processedFileStream = PublishSubject.create();
        fileStream = PublishSubject.create();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        FileService fileService = retrofit.create(FileService.class);
        fileStream.subscribeOn(Schedulers.newThread()) //TODO: Refactor
                .subscribe(extendedFile -> {
                    Call<ResponseBody> call = fileService.getFile(extendedFile.getFullPath());
                    ResponseBody responseBody = call.execute().body();
                    String internalPath = EnvVariables.INTERNAL_STORAGE_PATH + "/" + extendedFile.getFile().fileId() + "__" + extendedFile.getFileName();
                    // Saving file
                    File file = new File(internalPath);
                    OutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(responseBody.bytes());
                    outputStream.close();
                    extendedFile.setInternalPath(internalPath);
                    processedFileStream.onNext(extendedFile);
                }, e -> {
                    e.printStackTrace();
                    //TODO: Notify user in case of fail
                });
    }

    @Override
    public void emitCommand(Command cmd, KeyboardData keyboardData) {
        commandStream.onNext(new CommandMessage(cmd, keyboardData));
    }

    @Override
    public void emitFile(ExtendedFile file) {
        fileStream.onNext(file);
    }

    @Override
    public void emitStringMessage(Message message) {
        if (message.text() != null) stringStream.onNext(message);
    }

    @Override
    public synchronized void subscribe(BotAction action) {
        actions.put(action.getCommandName(), action);
    }

    @Override
    public synchronized void subscribePrivateAction(BotAction action) {
        privateActions.put(action.getCommandName(), action);
    }

    @Override
    public void subscribeFileObserver(Consumer<ExtendedFile> fileConsumer) {
        processedFileStream.subscribe(fileConsumer);
    }

    @Override
    public synchronized void subscribeVolatileActions(User user, BotActionGenerator botActionGenerator) {
        volatileActions.put(user.id(), botActionGenerator);
    }

    @Override
    public synchronized void subscribeKeyboardActions(Chat chat, BotActionGenerator botActionGenerator) {
        volatileInlineKeyboardActions.put(chat.id(), botActionGenerator);
    }

    @Override
    public synchronized void subscribeKeyboardActions(Chat chat, BotActionFromString botActionGenerator) {
        volatileKeyboardActions.put(chat.id(), botActionGenerator);
    }

    private void dispatch(CommandMessage commandMessage) {
        Command command = commandMessage.getCommand();
        KeyboardData keyboardData = commandMessage.getKeyboardData();
        if (command.isVolatile()) {
            if (command.isFromInline()) {
                if (volatileInlineKeyboardActions.containsKey(command.getChat().id())) {
                    volatileInlineKeyboardActions.get(command.getChat().id()).getBotAction(command.getVolatileIndex()).execute(command, keyboardData);
                }
            } else if (volatileActions.containsKey(command.getUser().id())) {
                volatileActions.get(command.getUser().id()).getBotAction(command.getVolatileIndex()).execute(command, keyboardData);
                //volatileActions.remove(command.getUser().id());
            }
        } else if (actions.containsKey(command.getCommand())) {
            actions.get(command.getCommand()).execute(command, keyboardData);
        } else if (privateActions.containsKey(command.getCommand())) {
            privateActions.get(command.getCommand()).execute(command, keyboardData);
        }
    }

    private void dispatch(Message message) {
        if (volatileKeyboardActions.containsKey(message.chat().id())) {
            String cmd = volatileKeyboardActions.get(message.chat().id()).getCommand(message.text());
            Command command = Utils.processCommand(message.from(), message.chat(), cmd);
            this.emitCommand(command, null);
        }
    }

    public String getInfo() {
        String info = "";
        for (Map.Entry<String, BotAction> entry : actions.entrySet()) {
            info += entry.getValue() + "\n";
        }
        return info;
    }

    private interface FileService {
        @GET
        Call<ResponseBody> getFile(@Url String url);
    }
}
