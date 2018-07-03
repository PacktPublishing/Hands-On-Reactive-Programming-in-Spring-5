package org.rpis5.chapters.chapter_03.push_model;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class DelayedFakeAsyncDatabaseClient implements AsyncDatabaseClient {

	@Override
	public Observable<Item> getStreamOfItems() {
		return Observable.range(1, Integer.MAX_VALUE)
		                 .map(i -> new Item("" + i))
		                 .delay(50, TimeUnit.MILLISECONDS)
		                 .delaySubscription(100, TimeUnit.MILLISECONDS);
	}
}
