package com.raincoatmoon.TaskManager;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;

import java.util.HashMap;

public class PendingTasksManager {
    private HashMap<Integer, PendingTask> pendingTasks;
    public PendingTasksManager() {
        pendingTasks = new HashMap<>();
    }

    public synchronized void subscribeTask(User user, PendingTask pendingTask) {
        pendingTasks.put(user.id(), pendingTask);
    }
    public synchronized void resumeTask(User user, Message msg) {
        if (pendingTasks.containsKey(user.id())) {
            PendingTask pendingTask = pendingTasks.get(user.id());
            pendingTasks.remove(user.id());
            pendingTask.exec(msg);
        }
    }
}
