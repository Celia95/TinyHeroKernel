package com.raincoatmoon.Keyboards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class DynamicInlineKeyboard extends CustomInlineKeyboard {
    private ElementGenerator elementGenerator;
    private int limit = -1;
    private boolean withLimit;

    public DynamicInlineKeyboard(ElementGenerator elementGenerator, String cmd) {
        this.elementGenerator = elementGenerator;
        this.cmd = cmd;
    }

    public DynamicInlineKeyboard withLimit(int l) {
        limit = l;
        withLimit = true;
        return this;
    }

    @Override
    public InlineKeyboardMarkup createFullKeyboard() {
        return null;
    }

    @Override
    public InlineKeyboardMarkup createKeyboard(KeyboardData keyboardData) {
        if (keyboardData == null) keyboardData = new KeyboardData(0, BUTTONS_PER_MSG - 1);
        int first = keyboardData.getFirst() >= 0? keyboardData.getFirst(): 0;
        int last = keyboardData.getLast();
        if (withLimit && last > limit) last = limit;
        boolean prev = first > 0;
        boolean next = !withLimit || (withLimit && last < limit);
        return createKeyboard(first, last, prev, next);
    }

    @Override
    protected InlineKeyboardButton getElement(int i) {
        return elementGenerator.getElement(i);
    }

    public interface ElementGenerator {
        InlineKeyboardButton getElement(int i);
    }
}
