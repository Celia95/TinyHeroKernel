package com.raincoatmoon.Keyboards.ExtendedReplyKeyboard;

import com.pengrad.telegrambot.model.request.KeyboardButton;

public class ExtendedKeyboardButton {
    private String tag;
    private String callbackData;

    public ExtendedKeyboardButton(String tag, String callbackData) {
        this.tag = tag;
        this.callbackData = callbackData;
    }

    public KeyboardButton createButton() {
        return new KeyboardButton(tag);
    }

    public String getTag() {
        return tag;
    }

    public String getCallbackData() {
        return callbackData;
    }
}
