package com.raincoatmoon;

import com.raincoatmoon.Core.BotModule;
import com.raincoatmoon.Core.BotSender;
import com.raincoatmoon.Core.CommandManager;
import com.raincoatmoon.ExampleModule.ExampleModule;
import com.raincoatmoon.Keyboards.KeyboardManager;
import com.raincoatmoon.TaskManager.PendingTasksManager;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static final List<BotModule> modules = new ArrayList<>();
    public static void init(BotSender sender, CommandManager commandManager, PendingTasksManager pendingTasksManager) {
        modules.add(new BasicModule(sender, commandManager, pendingTasksManager));
        modules.add(new ExampleModule(sender, commandManager, pendingTasksManager));
        modules.add(new KeyboardManager(sender, commandManager, pendingTasksManager));
        // Here you can add your modules.
    }
}
