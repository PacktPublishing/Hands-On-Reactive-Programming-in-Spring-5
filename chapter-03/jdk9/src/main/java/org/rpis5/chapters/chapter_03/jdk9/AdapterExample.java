package org.rpis5.chapters.chapter_03.jdk9;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import org.rpis5.chapters.chapter_03.news_service.NewsServicePublisher;
import org.rpis5.chapters.chapter_03.news_service.NewsServiceSubscriber;
import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;

public class AdapterExample {

    public static void main(String[] args) {
        Flow.Publisher jdkPublisher = FlowAdapters.toFlowPublisher(new NewsServicePublisher(smp ->
            Flowable.intervalRange(0, 10, 0, 10, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .map(e -> NewsLetter.template()
                                        .title(String.valueOf(e))
                                        .digest(Collections.emptyList())
                                        .build())
                    .subscribe(smp)
        ));
        Publisher external = FlowAdapters.toPublisher(jdkPublisher);
        Flow.Publisher jdkPublisher2 = FlowAdapters.toFlowPublisher(
                external
        );

        NewsServiceSubscriber newsServiceSubscriber = new NewsServiceSubscriber(2);
        jdkPublisher2.subscribe(FlowAdapters.toFlowSubscriber(newsServiceSubscriber));



        while (true) {
            Optional<NewsLetter> letterOptional = newsServiceSubscriber.eventuallyReadDigest();

            if (letterOptional.isPresent()) {
                NewsLetter letter = letterOptional.get();
                System.out.println(letter);

                if (letter.getTitle().equals("9")) {
                    break;
                }
            }
        }
    }
}
