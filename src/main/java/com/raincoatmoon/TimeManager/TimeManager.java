package com.raincoatmoon.TimeManager;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.GetChatMember;
import com.raincoatmoon.*;
import com.raincoatmoon.Core.*;
import com.raincoatmoon.Keyboards.BasicKeyboard;
import com.raincoatmoon.Keyboards.DynamicInlineKeyboard;
import com.raincoatmoon.Keyboards.ExtendedInlineKeyboard;
import com.raincoatmoon.Keyboards.KeyboardData;
import com.raincoatmoon.TaskManager.PendingTask;
import com.raincoatmoon.TaskManager.PendingTasksManager;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeManager extends BotModule {
    private int last;
    private static final List<InternalAction> actions = Arrays.asList(
            new InternalAction(Strings.ADD, "/addCron"),
            new InternalAction(Strings.CHECK, "/checkCron")
    );

    public TimeManager(BotSender sender, CommandManager commandManager, PendingTasksManager pendingTasksManager, TelegramBot bot) {
        super(sender, commandManager, pendingTasksManager);
        last = DateTime.now().getSecondOfDay();
        Observable.interval(60, TimeUnit.SECONDS, Schedulers.newThread())
                .subscribe(aLong -> {
                    int now = DateTime.now().getSecondOfDay();
                    List<CronTask> cronTasks = DBManager.getCronTasks(last, now);
                    Observable.fromIterable(cronTasks).subscribeOn(Schedulers.newThread())
                            .subscribe(cronTask -> {
                                Chat chat = bot.execute(new GetChat(cronTask.getUserID() + "")).chat();
                                User user = bot.execute(new GetChatMember(chat.id(), cronTask.getUserID())).chatMember().user();
                                Command command = Utils.processCommand(user, chat, cronTask.getCmd());
                                commandManager.emitCommand(command, null);
                            }, e -> e.printStackTrace());
                    last = now;
                }, e -> e.printStackTrace());

        commandManager.subscribe(new BotAction("cron", Strings.DESCRIPTION, (Command cmd, KeyboardData keyboardData) -> {
            if (cmd.parSize() == 0) { //TODO: Refactor
                String info = Strings.INFO;
                InlineKeyboardMarkup keyboard = new ExtendedInlineKeyboard<>(actions, action ->
                        new InlineKeyboardButton(action.getName()).callbackData(action.getCommand()), "")
                        .createFullKeyboard();
                sender.send(cmd.getChat().id(), info, keyboard);
            }
        }));

        commandManager.subscribePrivateAction(new BotAction("addCron", "", (Command cmd, KeyboardData keyboardData) -> {
            if (cmd.parSize() == 0) {
                String info = Strings.EXECUTE;
                InlineKeyboardMarkup keyboard = new DynamicInlineKeyboard(i -> {
                    int time = i * 3600; //Second of the day
                    return new InlineKeyboardButton(i + "h.").callbackData(cmd.toString() + " " + time);
                }, cmd.toString()).withLimit(23).createKeyboard(keyboardData);
                if (keyboardData != null && cmd.getBotMessage() != null) sender.edit(cmd.getBotMessage(), cmd.getChat().id(), info, keyboard);
                else sender.send(cmd.getChat().id(), info, keyboard);
            } else if (cmd.parSize() == 1) {
                String info = Strings.COMMAND;
                pendingTasksManager.subscribeTask(cmd.getUser(), new PendingTask((Message msg) -> {
                    CronTask cronTask = new CronTask(Integer.valueOf(cmd.getParameters().get(0)), cmd.getUser().id(),
                            msg.text(), Strings.CUSTOM_CRON + msg.text());
                    DBManager.insertCronTask(cronTask);
                    sender.send(cmd.getChat().id(), Strings.ADDED, null);
                }));
                sender.send(cmd.getChat().id(), info, new ForceReply()); //TODO: Remove previous msg
            } else {
                sender.send(cmd.getChat().id(), Strings.ERROR, null);
            }
        }));
        commandManager.subscribePrivateAction(new BotAction("checkCron", "", (Command cmd, KeyboardData keyboardData) -> {
            if (cmd.parSize() == 0) {
                InlineKeyboardMarkup keyboard = (new BasicKeyboard(null, cmd.toString())).withGoBack("/cron").createFullKeyboard();
                List<CronTask> cronTasks = DBManager.getCronTasks(cmd.getUser());
                String list = "";
                int count = 0;
                for (int i = 0; i < cronTasks.size(); i++) {
                    CronTask cronTask = cronTasks.get(i);
                    list += "/" + (i + 1) + " " + cronTask.toString() + "\n";
                    count++;
                    if (count >= 40 || i == cronTasks.size() - 1) {
                        sender.send(cmd.getChat().id(), list, i == cronTasks.size() - 1? keyboard: null, ParseMode.Markdown, false);
                        list = "";
                        count = 0;
                    }
                }
                if (cronTasks.isEmpty()) {
                    list = Strings.EMPTY;
                    sender.send(cmd.getChat().id(), list, keyboard, ParseMode.Markdown, false);
                }
            }
        }));
    }

    public static void createCronTask(Chat chat, User user, Command cmd, DateTime dateTime) {
        DBManager.insertCronTask(new CronTask(dateTime.getSecondOfDay(), user.id(), cmd.toString(), ""));
    }
}
