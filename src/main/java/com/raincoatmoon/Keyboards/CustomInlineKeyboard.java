package com.raincoatmoon.Keyboards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public abstract class CustomInlineKeyboard {
    protected static final int BUTTONS_PER_ROW = 2;
    protected static final int BUTTONS_PER_MSG = 6;
    protected String cmd;
    protected String goBackCommand;
    protected boolean withGoBack;

    public abstract InlineKeyboardMarkup createFullKeyboard();
    public abstract InlineKeyboardMarkup createKeyboard(KeyboardData keyboardData);
    protected abstract InlineKeyboardButton getElement(int i);

    public CustomInlineKeyboard withGoBack(String goBackCommand) {
        this.withGoBack = true;
        this.goBackCommand = goBackCommand;
        return this;
    }

    protected InlineKeyboardMarkup createKeyboard(int first, int last, boolean prev, boolean next) {
        int numElems = last - first + 1;
        int xsize = numElems / BUTTONS_PER_ROW + (numElems % BUTTONS_PER_ROW > 0? 1: 0) + (prev? 1: 0) + (next? 1: 0) + (withGoBack? 1: 0);
        InlineKeyboardButton inlineKeys[][] = new InlineKeyboardButton[xsize][];
        if (prev) {
            inlineKeys[0] = new InlineKeyboardButton[1];
            inlineKeys[0][0] = new InlineKeyboardButton("<<").callbackData("/keyboard " + (first - BUTTONS_PER_MSG) + " " + (first - 1) + " 0 0 " + cmd);
        }
        for (int i = first; i <= last; i++) {
            if ((i - first) % BUTTONS_PER_ROW == 0) { //(i - first)
                int subSize = BUTTONS_PER_ROW;
                if (i + BUTTONS_PER_ROW - 1 > last) subSize = last - i + 1;
                inlineKeys[((i - first) / BUTTONS_PER_ROW) + (prev? 1: 0)] = new InlineKeyboardButton[subSize];
            }
            inlineKeys[((i - first) / BUTTONS_PER_ROW) + (prev? 1: 0)][i % BUTTONS_PER_ROW] = getElement(i);
        }
        if (next) {
            inlineKeys[xsize - 1 - (withGoBack? 1: 0)] = new InlineKeyboardButton[1];
            inlineKeys[xsize - 1 - (withGoBack? 1: 0)][0] = new InlineKeyboardButton(">>").callbackData("/keyboard " + (last + 1) + " " + (last + BUTTONS_PER_MSG) + " 0 0 " + cmd);
        }
        if (withGoBack) {
            inlineKeys[xsize - 1] = new InlineKeyboardButton[1];
            inlineKeys[xsize - 1][0] = new InlineKeyboardButton("Go back").callbackData(goBackCommand);
        }
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(inlineKeys);
        return keyboard;
    }
}
