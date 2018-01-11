package com.raincoatmoon.TaskManager;

import com.pengrad.telegrambot.model.Message;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class PendingTask {
    private Action action;

    public PendingTask(Action action) {
        this.action = action;
    }

    public void exec(Message msg) {
        Observable.just(msg)
                .subscribeOn(Schedulers.newThread())
                .subscribe(msg2 -> {
                    if (action != null) action.exec(msg2);
                }, (Throwable e) -> {
                    e.printStackTrace();
                });
    }

    public interface Action {
        void exec(Message msg);
    }
}
