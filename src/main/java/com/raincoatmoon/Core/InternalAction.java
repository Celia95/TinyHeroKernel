package com.raincoatmoon.Core;

public class InternalAction {
    private String command;
    private String name;

    public InternalAction(String name, String command) {
        this.command = command;
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }
}
