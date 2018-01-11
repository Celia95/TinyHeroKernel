package com.raincoatmoon.Core;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import java.util.List;

public class Command {
    private User user;
    private Chat chat;
    private Message message;
    private String cmd;
    private List<String> par;
    private boolean isVolatile = false;
    private boolean fromInline = false;

    public Command(User user, Chat chat, String cmd, List<String> par) {
        this.user = user;
        this.chat = chat;
        this.cmd = cmd;
        this.par = par;
    }

    public Command(Command command, String cmd, List<String> par) {
        this.chat = command.chat;
        this.user = command.user;
        this.message = command.message;
        this.cmd = cmd;
        this.par = par;
    }

    public Command(Command command, List<String> par) {
        this.chat = command.chat;
        this.user = command.user;
        this.message = command.message;
        if (par.size() >= 1) {
            this.cmd = par.get(0);
            this.par = par.subList(1, par.size());
        }
    }

    public User getUser() {
        return user;
    }

    public Chat getChat() {
        return chat;
    }

    public String getCommand() {
        return cmd;
    }

    public List<String> getParameters() {
        return par;
    }

    public void setParameters(List<String> par) {
        this.par = par;
    }

    public Message getBotMessage() {
        return message;
    }

    public void setBotMessage(Message message) {
        this.message = message;
    }

    public int parSize() {
        return par.size();
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void setVolatile(boolean aVolatile) {
        isVolatile = aVolatile;
    }

    public boolean isFromInline() {
        return fromInline;
    }

    public void setFromInline(boolean fromInline) {
        this.fromInline = fromInline;
    }

    public int getVolatileIndex() {
        if (isVolatile) {
            return Integer.valueOf(cmd.substring(1));
        }
        return -1;
    }

    @Override
    public String toString() {
        String command = cmd;
        for (String p: par) {
            command += " " + p;
        }
        return command;
    }
}
