package com.raincoatmoon.Keyboards;

import com.raincoatmoon.Core.*;
import com.raincoatmoon.TaskManager.PendingTasksManager;

public class KeyboardManager extends BotModule {
    public KeyboardManager(BotSender sender, CommandManager commandManager, PendingTasksManager pendingTasksManager) {
        super(sender, commandManager, pendingTasksManager);
        commandManager.subscribePrivateAction(new BotAction("keyboard", "", (Command cmd, KeyboardData keyboardData) -> {
            KeyboardData keyboardData1 = new KeyboardData(cmd.getParameters());
            Command command = new Command(cmd, cmd.getParameters().subList(4, cmd.getParameters().size()));
            commandManager.emitCommand(command, keyboardData1);
        }));
    }
}
