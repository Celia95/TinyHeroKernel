package com.raincoatmoon.Core;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.raincoatmoon.Keyboards.KeyboardData;
import io.reactivex.functions.Consumer;

public interface ActionSubscriber {
    void emitCommand(Command cmd, KeyboardData keyboardData);
    void emitFile(ExtendedFile file);
    void emitStringMessage(Message message);
    void subscribe(BotAction action);
    void subscribeFileObserver(Consumer<ExtendedFile> fileConsumer);
    void subscribeVolatileActions(User user, BotActionGenerator botActionGenerator);
    void subscribePrivateAction(BotAction action);
    void subscribeKeyboardActions(Chat chat, BotActionGenerator botActionGenerator);
    void subscribeKeyboardActions(Chat chat, BotActionFromString botActionGenerator);
}
