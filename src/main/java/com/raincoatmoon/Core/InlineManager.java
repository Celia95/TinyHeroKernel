package com.raincoatmoon.Core;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.List;

public class InlineManager {
    private BotSender sender;
    private PublishSubject<InlineQuery> queryStream;

    public InlineManager(BotSender sender) {
        this.sender = sender;
        queryStream = PublishSubject.create();
        queryStream.subscribeOn(Schedulers.trampoline())
                .subscribe(s -> {
                    Observable.just(s).subscribeOn(Schedulers.newThread())
                            .subscribe(this::dispatch, (Throwable e) -> {
                                e.printStackTrace();
                            });
                });
        // Here you can initialize your APIs or whatever you need for your inline bot.

    }

    public void emitQuery(InlineQuery query) {
        queryStream.onNext(query);
    }

    public void dispatch(InlineQuery query) {
        // Here you can handle the inline queries asynchronously.
        List<InlineQueryResultArticle> inlineQueryResultArticles = null;
        sender.sendInlineResults(query, inlineQueryResultArticles.toArray(
                new InlineQueryResultArticle[inlineQueryResultArticles.size()]));
    }
}
