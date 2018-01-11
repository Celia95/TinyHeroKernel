package com.raincoatmoon;

import com.raincoatmoon.Core.*;
import com.raincoatmoon.Keyboards.KeyboardData;
import com.raincoatmoon.TaskManager.PendingTasksManager;

public class BasicModule extends BotModule {
    public BasicModule(final BotSender sender, CommandManager commandManager, PendingTasksManager pendingTasksManager) {
        super(sender, commandManager, pendingTasksManager);
        commandManager.subscribe(new BotAction("start", "Execute /help", (Command cmd, KeyboardData keyboardData) -> {
            commandManager.emitCommand(new Command(cmd, "/help", null), null);
        }));
        commandManager.subscribe(new BotAction("help", "Show this help message", (Command cmd, KeyboardData keyboardData) -> {
            String info = commandManager.getInfo();
            sender.send(cmd.getChat().id(), info, null);
        }));
    }
}
