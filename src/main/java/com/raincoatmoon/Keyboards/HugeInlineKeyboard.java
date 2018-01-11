package com.raincoatmoon.Keyboards;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.raincoatmoon.Core.BotAction;
import com.raincoatmoon.Core.Command;
import com.raincoatmoon.Core.CommandManager;
import com.raincoatmoon.Core.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HugeInlineKeyboard<T> extends ExtendedInlineKeyboard{
    private List<String> callbacks;
    private CommandManager commandManager;
    private Chat chat;

    public HugeInlineKeyboard(List<T> elements, Generator<T> generator, String cmd, CommandManager commandManager, Chat chat) {
        super(elements, generator, cmd);
        callbacks = new ArrayList<>();
        this.commandManager = commandManager;
        this.chat = chat;
    }

    private void setCallbacksManager() {
        commandManager.subscribeKeyboardActions(chat, (int indx) ->
                new BotAction("", "", (Command cmd, KeyboardData keyboardData) -> {
                    if (callbacks.size() > indx) {
                        String callBack = callbacks.get(indx);
                        Command newcmd = Utils.processCommand(cmd.getUser(), cmd.getChat(), callBack);
                        newcmd.setBotMessage(cmd.getBotMessage());
                        commandManager.emitCommand(newcmd, keyboardData);
                    }
                })
        );
    }

    @Override
    public InlineKeyboardMarkup createFullKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = super.createFullKeyboard();
        setCallbacksManager();
        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardMarkup createKeyboard(KeyboardData keyboardData) {
        InlineKeyboardMarkup inlineKeyboardMarkup = super.createKeyboard(keyboardData);
        setCallbacksManager();
        return inlineKeyboardMarkup;
    }

    @Override
    protected InlineKeyboardButton getElement(int i) {
        InlineKeyboardButton inlineKeyboardButton = super.getElement(i);
        System.out.println(inlineKeyboardButton.toString());
        try {
            Field f = inlineKeyboardButton.getClass().getDeclaredField("callback_data");
            f.setAccessible(true);
            String callback = (String) f.get(inlineKeyboardButton);
            callbacks.add(callback);
            inlineKeyboardButton.callbackData("/" + (callbacks.size() - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inlineKeyboardButton;
    }
}
