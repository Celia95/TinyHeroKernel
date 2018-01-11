package com.raincoatmoon.Keyboards;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

public class ExtendedInlineKeyboard<T> extends CustomInlineKeyboard{
    private List<T> elements;
    private Generator<T> generator;

    public ExtendedInlineKeyboard(List<T> elements, Generator<T> generator, String cmd) {
        this.elements = elements;
        this.generator = generator;
        this.cmd = cmd;
    }

    @Override
    public InlineKeyboardMarkup createFullKeyboard() {
        return elements.size() > 0? createKeyboard(0, elements.size() - 1, false, false): null;
    }

    @Override
    public InlineKeyboardMarkup createKeyboard(KeyboardData keyboardData) {
        if (keyboardData == null) keyboardData = new KeyboardData(0, BUTTONS_PER_MSG - 1);
        if (elements.size() > 0) {
            int first = keyboardData.getFirst() >= 0? keyboardData.getFirst(): 0;
            int last = keyboardData.getLast() > elements.size() - 1? elements.size() - 1: keyboardData.getLast();
            boolean prev = first > 0;
            boolean next = last < elements.size() - 1;
            return createKeyboard(first, last, prev, next);
        }
        return null;
    }

    @Override
    protected InlineKeyboardButton getElement(int i) {
        return generator.getKeyboardButton(elements.get(i));
    }

    public interface Generator<T> {
        InlineKeyboardButton getKeyboardButton(T o);
    }
}
