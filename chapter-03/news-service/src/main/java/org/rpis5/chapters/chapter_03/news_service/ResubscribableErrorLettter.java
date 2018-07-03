package org.rpis5.chapters.chapter_03.news_service;

import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import org.reactivestreams.Subscriber;

public interface ResubscribableErrorLettter {

    void resubscribe(Subscriber<? super NewsLetter> subscriber);
}
