package com.raincoatmoon.Keyboards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

public class BasicKeyboard extends CustomInlineKeyboard{
    private List<InlineKeyboardButton> elements;

    public BasicKeyboard(List<InlineKeyboardButton> elements, String cmd) {
        this.elements = elements;
        this.cmd = cmd;
    }

    @Override
    public InlineKeyboardMarkup createFullKeyboard() {
        return createKeyboard(0, elements != null? elements.size() - 1: -1, false, false);
    }

    @Override
    public InlineKeyboardMarkup createKeyboard(KeyboardData keyboardData) {
        if (keyboardData == null) keyboardData = new KeyboardData(0, BUTTONS_PER_MSG - 1);
        if (elements != null && elements.size() > 0) {
            int first = keyboardData.getFirst() >= 0? keyboardData.getFirst(): 0;
            int last = keyboardData.getLast() > elements.size() - 1? elements.size() - 1: keyboardData.getLast();
            boolean prev = first > 0;
            boolean next = last < elements.size() - 1;
            return createKeyboard(first, last, prev, next);
        } else return createFullKeyboard();
    }

    @Override
    protected InlineKeyboardButton getElement(int i) {
        return elements.get(i);
    }
}
