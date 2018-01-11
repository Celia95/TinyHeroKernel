package com.raincoatmoon.Core;

import com.raincoatmoon.Keyboards.KeyboardData;

public class CommandMessage {
    private Command command;
    private KeyboardData keyboardData;

    public CommandMessage(Command command, KeyboardData keyboardData) {
        this.command = command;
        this.keyboardData = keyboardData;
    }

    public Command getCommand() {
        return command;
    }

    public KeyboardData getKeyboardData() {
        return keyboardData;
    }
}
