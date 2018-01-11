package com.raincoatmoon.Core;

import com.raincoatmoon.TaskManager.PendingTasksManager;

public abstract class BotModule {
    protected BotSender sender;
    protected CommandManager commandManager;
    protected PendingTasksManager pendingTasksManager;

    public BotModule(BotSender sender, CommandManager commandManager, PendingTasksManager pendingTasksManager) {
        this.sender = sender;
        this.commandManager = commandManager;
        this.pendingTasksManager = pendingTasksManager;
    }
}
