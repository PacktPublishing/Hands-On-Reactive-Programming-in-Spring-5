package org.rpis5.chapters.chapter_03.news_service;

import org.rpis5.chapters.chapter_03.news_service.dto.News;
import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class NewsPreparationOperatorTest extends PublisherVerification<NewsLetter> {

    public NewsPreparationOperatorTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<NewsLetter> createPublisher(long elements) {
        return new NewsPreparationOperator(
                Flowable.just(new News()),
                "test"
        );
    }

    @Override
    public Publisher<NewsLetter> createFailedPublisher() {
        return new NewsPreparationOperator(
                Flowable.error(new RuntimeException()),
                "test"
        );
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1;
    }
}



