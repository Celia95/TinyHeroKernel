package com.raincoatmoon;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.raincoatmoon.Core.*;
import com.raincoatmoon.TaskManager.PendingTasksManager;
import com.raincoatmoon.TimeManager.TimeManager;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.List;

public class Application {
    private static TelegramBot bot;
    private static PublishSubject<Update> updateStream;
    private static BotSender sender;
    private static CommandManager commandManager;
    private static PendingTasksManager pendingTasksManager;
    private static User me;
    private static TimeManager timeManager;

    private static InlineManager inlineManager;

    public static void init(boolean listen) {
        bot = TelegramBotAdapter.build(EnvVariables.TOKEN);
        sender = new BotSender() {
            @Override
            public void send(Long chatID, String msg, Keyboard keyboard) {
                Application.send(chatID, msg, keyboard, null, false);
            }

            @Override
            public void send(Long chatID, String msg, Keyboard keyboard, ParseMode parseMode, boolean disableWebPagePreview) {
                Application.send(chatID, msg, keyboard, parseMode, disableWebPagePreview);
            }

            @Override
            public void edit(Message message, Long chatID, String newContent, InlineKeyboardMarkup keyboard) {
                Application.edit(message, chatID, newContent, keyboard, null, false);
            }

            @Override
            public void edit(Message message, Long chatID, String newContent, InlineKeyboardMarkup keyboard, ParseMode parseMode, boolean disableWebPagePreview) {
                Application.edit(message, chatID, newContent, keyboard, parseMode, disableWebPagePreview);
            }

            public void sendChatAction(Long chatID, String action) {
                Application.sendChatAction(chatID,action);
            }

            @Override
            public void sendPhoto(Long chatID, java.io.File photo, String caption) {
                Application.sendPhoto(chatID, photo, caption);
            }

            @Override
            public void sendFile(Long chatID, java.io.File file, String caption) {
                Application.sendFile(chatID, file, caption);
            }

            @Override
            public void sendInlineResults(InlineQuery query, InlineQueryResult... results) {
                Application.sendInlineResults(query, results);
            }
        };
        commandManager = new CommandManager();
        pendingTasksManager = new PendingTasksManager();
        ModuleManager.init(sender, commandManager, pendingTasksManager);
        timeManager = new TimeManager(sender, commandManager, pendingTasksManager, bot);

        inlineManager = new InlineManager(sender);

        //Getting bot user information
        GetMeResponse response = bot.execute(new GetMe());
        me = response.user();

        if (listen) {
            updateStream = PublishSubject.create();
            updateStream.subscribeOn(Schedulers.trampoline()).subscribe(update -> {
                        Observable.just(update).subscribeOn(Schedulers.newThread())
                                .subscribe(Application::dispatch,
                                        (Throwable e) -> {
                                            e.printStackTrace();
                                        });
                    });
            bot.setUpdatesListener((List<Update> list) -> {
                for (Update update : list) {
                    updateStream.onNext(update);
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            });
        }
    }

    private static void dispatch(Update update) {
        System.out.println(update);
        if (update.message() != null) {
            DBManager.registerUser(update.message().from());
            if (update.message().replyToMessage() != null) {
                Message replyMessage = update.message().replyToMessage();
                if (replyMessage.from().id().equals(me.id())) { //Valid reply
                    pendingTasksManager.resumeTask(update.message().from(), update.message());
                    commandManager.emitStringMessage(update.message());
                }
            } else {
                Command cmd = Utils.processCommandFromMessage(update.message());
                if (cmd != null) commandManager.emitCommand(cmd, null);
                else commandManager.emitStringMessage(update.message());
            }
            if (update.message().document() != null) {
                GetFile request = new GetFile(update.message().document().fileId());
                GetFileResponse getFileResponse = bot.execute(request);
                File file = getFileResponse.file();
                String fullPath = bot.getFullFilePath(file);
                commandManager.emitFile(new ExtendedFile(fullPath, file, update.message().document(),
                                        update.message().chat(), update.message().from()));
            }
        } else if (update.callbackQuery() != null) {
            DBManager.registerUser(update.callbackQuery().from());
            Command cmd = Utils.processCommandFromCallback(update.callbackQuery());
            if (cmd != null) {
                cmd.setFromInline(true);
                commandManager.emitCommand(cmd, null);
            }
        } else if (update.inlineQuery() != null) {
            inlineManager.emitQuery(update.inlineQuery());
        }
    }

    private static void send(Long chatID, String msg, Keyboard keyboard, ParseMode parseMode, boolean disableWebPagePreview) {
        SendMessage sendMessage = new SendMessage(chatID, msg);
        if (parseMode != null) sendMessage = sendMessage.parseMode(parseMode);
        if (keyboard != null) sendMessage = sendMessage.replyMarkup(keyboard);
        sendMessage.disableWebPagePreview(disableWebPagePreview);
        bot.execute(sendMessage);
    }

    private static void edit(Message message, Long chatID, String msg, InlineKeyboardMarkup keyboard, ParseMode parseMode, boolean disableWebPagePreview) {
        EditMessageText editMessageText = new EditMessageText(chatID, message.messageId(), msg);
        if (parseMode != null) editMessageText = editMessageText.parseMode(parseMode);
        if (keyboard != null) editMessageText = editMessageText.replyMarkup(keyboard);
        editMessageText.disableWebPagePreview(disableWebPagePreview);
        bot.execute(editMessageText);
    }

    private static void sendChatAction(Long chatID, String action) {
        SendChatAction sendChatAction = new SendChatAction(chatID, action);
        bot.execute(sendChatAction);
    }

    private static void sendPhoto(Long chatID, java.io.File photo, String caption) {
        SendPhoto sendPhoto = new SendPhoto(chatID, photo).caption(caption);
        bot.execute(sendPhoto);
    }

    private static void sendFile(Long chatID, java.io.File file, String caption) {
        SendDocument sendDocument = new SendDocument(chatID, file).caption(caption);
        bot.execute(sendDocument);
    }

    private static void sendInlineResults(InlineQuery query, InlineQueryResult... results) {
        bot.execute(new AnswerInlineQuery(query.id(), results));
    }
}
