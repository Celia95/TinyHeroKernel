package com.raincoatmoon.Core;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;

public interface BotSender {
    void send(Long chatID, String msg, Keyboard keyboard);
    void send(Long chatID, String msg, Keyboard keyboard, ParseMode parseMode, boolean disableWebPagePreview);

    void edit(Message message, Long chatID, String newContent, InlineKeyboardMarkup keyboard);
    void edit(Message message, Long chatID, String newContent, InlineKeyboardMarkup keyboard, ParseMode parseMode, boolean disableWebPagePreview);

    void sendChatAction(Long chatID, String action);

    void sendPhoto(Long chatID, java.io.File photo, String caption);
    void sendFile(Long chatID, java.io.File file, String caption);

    void sendInlineResults(InlineQuery query, InlineQueryResult... results);
}
