package com.raincoatmoon.Core;

import com.raincoatmoon.Keyboards.KeyboardData;

public class BotAction {
    protected String name;
    protected String description;
    protected Action action;

    public BotAction(String name, String description, Action action) {
        this.name = name;
        this.description = description;
        this.action = action;
    }

    public void execute(Command cmd, KeyboardData keyboardData) {
        action.execute(cmd,keyboardData);
    }

    public String getCommandName() {
        return "/" + name;
    }

    public interface Action {
        void execute(Command cmd, KeyboardData keyboardData);
    }

    @Override
    public String toString() {
        return getCommandName() + ": " + description;
    }
}
