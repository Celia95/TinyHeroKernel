package com.raincoatmoon.Keyboards.ExtendedReplyKeyboard;

import com.pengrad.telegrambot.model.Chat;
import com.raincoatmoon.Core.CommandManager;

import java.util.List;

public class GeneratedReplyKeyboard<T> extends ExtendedReplyKeyboard{
    private List<T> elements;
    private Generator<T> generator;

    public GeneratedReplyKeyboard(List<T> elements, Generator<T> generator, CommandManager commandManager, Chat chat) {
        super(elements.size(), commandManager, chat);
        this.elements = elements;
        this.generator = generator;
    }

    @Override
    protected ExtendedKeyboardButton getButton(int i) {
        return generator.getKeyboardButton(elements.get(i));
    }

    public interface Generator<T> {
        ExtendedKeyboardButton getKeyboardButton(T o);
    }
}
