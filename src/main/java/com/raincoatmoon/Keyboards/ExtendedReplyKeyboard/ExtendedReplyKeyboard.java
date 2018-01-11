package com.raincoatmoon.Keyboards.ExtendedReplyKeyboard;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.raincoatmoon.Core.CommandManager;
import com.raincoatmoon.Keyboards.CustomInlineKeyboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedReplyKeyboard {
    protected static final int BUTTONS_PER_ROW = 2;
    protected static final int BUTTONS_PER_MSG = 6;

    private List<ExtendedKeyboardButton> elements;
    private CommandManager commandManager;
    private Chat chat;
    protected int size;
    protected String goBackCommand;
    protected boolean withGoBack;

    protected ExtendedReplyKeyboard(int size, CommandManager commandManager, Chat chat) {
        this.size = size;
        this.commandManager = commandManager;
        this.chat = chat;
    }

    public ExtendedReplyKeyboard(List<ExtendedKeyboardButton> elements, CommandManager commandManager, Chat chat) {
        this.elements = elements;
        this.commandManager = commandManager;
        this.chat = chat;
        this.size = elements.size();
    }

    public ExtendedReplyKeyboard withGoBack(String goBackCommand) {
        this.withGoBack = true;
        this.goBackCommand = goBackCommand;
        return this;
    }

    public ReplyKeyboardMarkup createFullKeyboard() {
        Map<String, String> map = new HashMap<>();
        int numElems = this.size;
        int xsize = numElems / BUTTONS_PER_ROW + (numElems % BUTTONS_PER_ROW > 0? 1: 0) + (withGoBack? 1: 0);
        KeyboardButton inlineKeys[][] = new KeyboardButton[xsize][];
        for (int i = 0; i < numElems; i++) {
            if (i % BUTTONS_PER_ROW == 0) {
                int subSize = BUTTONS_PER_ROW;
                if (i + BUTTONS_PER_ROW > numElems) subSize = numElems - i;
                inlineKeys[i / BUTTONS_PER_ROW] = new KeyboardButton[subSize];
            }
            inlineKeys[i / BUTTONS_PER_ROW][i % BUTTONS_PER_ROW] = getButton(i).createButton();
            map.put(getButton(i).getTag(), getButton(i).getCallbackData());
        }
        if (withGoBack) {
            ExtendedKeyboardButton goBackButton = new ExtendedKeyboardButton("⬅️ Go back", goBackCommand);
            inlineKeys[xsize - 1] = new KeyboardButton[1];
            inlineKeys[xsize - 1][0] = goBackButton.createButton();
            map.put(goBackButton.getTag(), goBackButton.getCallbackData());
        }
        commandManager.subscribeKeyboardActions(chat, map::get);
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(inlineKeys).resizeKeyboard(true);
        return keyboard;
    }

    protected ExtendedKeyboardButton getButton(int i) {
        return elements.get(i);
    }
}
