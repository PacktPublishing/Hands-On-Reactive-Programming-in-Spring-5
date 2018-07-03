package org.rpis5.chapters.chapter_03.news_service;

import org.rpis5.chapters.chapter_03.news_service.dto.News;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.Date;

public class DBPublisher implements Publisher<News> {
    private final MongoCollection<News> collection;
    private final String category;

    public DBPublisher(MongoCollection<News> collection, String category) {
        this.collection = collection;
	    this.category = category;
    }

    @Override
    public void subscribe(Subscriber<? super News> s) {
	    FindPublisher<News> findPublisher = collection.find(News.class);
	    findPublisher.sort(Sorts.descending("publishedOn"))
			     .filter(Filters.and(
		            Filters.eq("category", category),
			        Filters.gt("publishedOn", today())
			     ))
	             .subscribe(s);
    }

    private Date today() {
	    Date date = new Date();
	    return new Date(date.getYear(), date.getMonth(), date.getDate());
    }
}
