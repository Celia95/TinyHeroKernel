package com.raincoatmoon.ExampleModule;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.raincoatmoon.Core.*;
import com.raincoatmoon.Keyboards.ExtendedInlineKeyboard;
import com.raincoatmoon.Keyboards.ExtendedReplyKeyboard.ExtendedKeyboardButton;
import com.raincoatmoon.Keyboards.ExtendedReplyKeyboard.ExtendedReplyKeyboard;
import com.raincoatmoon.Keyboards.ExtendedReplyKeyboard.GeneratedReplyKeyboard;
import com.raincoatmoon.Keyboards.KeyboardData;
import com.raincoatmoon.TaskManager.PendingTasksManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleModule extends BotModule {
    public ExampleModule(BotSender sender, CommandManager commandManager, PendingTasksManager pendingTasksManager) {
        super(sender, commandManager, pendingTasksManager);

        // You can subscribe as much commands as you want for each module.
        commandManager.subscribe(new BotAction("test", "Dummy command", (Command cmd, KeyboardData keyboardData) -> {
            // Each message sent to a user can contain an inline keyboard or a reply keyboard.
            Keyboard keyboard = new ExtendedReplyKeyboard(Arrays.asList(
                    new ExtendedKeyboardButton("1", "/test"),
                    new ExtendedKeyboardButton("2", "/test2"),
                    new ExtendedKeyboardButton("3", "/test3"),
                    new ExtendedKeyboardButton("4", "/test4")
            ), commandManager, cmd.getChat()).createFullKeyboard();
            sender.send(cmd.getChat().id(), "TEST", keyboard);
        }));

        // You can subscribe internal private actions for inner purposes.
        commandManager.subscribePrivateAction(new BotAction("test2", "Dummy command 2", (Command cmd, KeyboardData keyboardData) -> {
            Keyboard keyboard = new GeneratedReplyKeyboard<>(Arrays.asList(1, 2, 3, 4, 5), i -> {
                return new ExtendedKeyboardButton(i + "", "/test2");
            }, commandManager, cmd.getChat()).withGoBack("/test2").createFullKeyboard();
            sender.send(cmd.getChat().id(), "TEST", keyboard);
        }));
        commandManager.subscribePrivateAction(new BotAction("test3", "Dummy command 3", (Command cmd, KeyboardData keyboardData) -> {
            sender.sendChatAction(cmd.getChat().id(), "typing");
            String info = "/1 First Action\n" +
                        "/2 Second Action\n" +
                        "/3 Third Action\n" +
                        "/4 Forth Action";

            // You can provide volatile actions (a.k.a. commands inside a text message)
            commandManager.subscribeVolatileActions(cmd.getUser(), indx ->
                new BotAction("", "", (Command cmd2, KeyboardData keyboardData2) -> {
                    String answer = "";
                    switch (indx) {
                        case 1: answer += "First Action"; break;
                        case 2: answer += "Second Action"; break;
                        case 3: answer += "Third Action"; break;
                        case 4: answer += "Forth Action"; break;
                    }
                    answer += " chosen";
                    sender.send(cmd2.getChat().id(), answer, null);
                })
            );
            sender.send(cmd.getChat().id(), info, null);
        }));

        commandManager.subscribe(new BotAction("test4", "Dummy command 4", (Command cmd, KeyboardData keyboardData) -> {
            //Inline keyboard generated from a List<String>
            List<String> list= new ArrayList<>();
            list.add("1");
            list.add("2");
            list.add("3");
            list.add("4");
            Keyboard keyboard = new ExtendedInlineKeyboard<>(list, s -> {
                // With this lambda function you decide the behavior of your inline keyboard.
                // Callback data is the equivalent command of a button.
                if (s.equals("1"))
                    return new InlineKeyboardButton("1").callbackData("/test");
                else
                    return new InlineKeyboardButton(s).callbackData("/test" + s);
            }, "/").createFullKeyboard();
            sender.send(cmd.getChat().id(), "TEST", keyboard);
            // You can use sender.edit(...) for replacing the original message instead of sending several messages.
        }));

        // You can receive and send files from users.
        commandManager.subscribeFileObserver(extendedFile -> {
            sender.send(extendedFile.getChat().id(), extendedFile.getFileName() + "--" + Utils.getExtension(extendedFile.getFileName()), null);
            sender.send(extendedFile.getChat().id(), "Received!", null);
            sender.sendFile(extendedFile.getChat().id(), new File(extendedFile.getFullPath()), "Echoooo!");
        });
    }
}
