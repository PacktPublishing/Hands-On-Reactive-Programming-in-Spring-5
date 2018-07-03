package org.rpis5.chapters.chapter_03.news_service;

import org.rpis5.chapters.chapter_03.WithEmbeddedMongo;
import org.rpis5.chapters.chapter_03.news_service.dto.News;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DBPublisherTest extends PublisherVerification<News> implements
                                                                 WithEmbeddedMongo {

    private final MongoCollection<News> collection;

    public DBPublisherTest() {
        super(new TestEnvironment(2000, 2000));
        this.collection = mongoClient().getDatabase("news")
                                       .getCollection("news", News.class);
    }

    @BeforeClass
    static void up() throws IOException {
        WithEmbeddedMongo.setUpMongo();
    }

    @AfterClass
    static void down() {
        WithEmbeddedMongo.tearDownMongo();
    }

    @Override
    public Publisher<News> createPublisher(long elements) {
        prepareRandomData(elements);
        return new DBPublisher(collection,"tech");
    }

    @Override
    public Publisher<News> createFailedPublisher() {
        return null;
    }

    private void prepareRandomData(long elements) {
        if (elements <= 0) {
            return;
        }

        Flowable<Success> successFlowable = Flowable.fromPublisher(collection.drop())
                .ignoreElements()
                .andThen(
                        Flowable
                                .rangeLong(0L, elements)
                                .map(l -> NewsHarness.generate())
                                .buffer(500, TimeUnit.MILLISECONDS)
                                .flatMap(collection::insertMany)
                );

        if (elements == Long.MAX_VALUE || elements == Integer.MAX_VALUE) {
            successFlowable.subscribe();
        } else {
            successFlowable.blockingSubscribe();
        }
    }
}



