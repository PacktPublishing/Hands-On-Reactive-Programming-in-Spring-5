package org.rpis5.chapters.chapter_03.push_pull_model;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;

public class DelayedFakeAsyncDatabaseClient implements AsyncDatabaseClient {

	@Override
	public Publisher<Item> getStreamOfItems() {
		return Flowable.range(1, Integer.MAX_VALUE)
		               .map(i -> new Item("" + i))
		               .delay(50, TimeUnit.MILLISECONDS)
		               .hide()
		               .subscribeOn(Schedulers.io())
		               .delaySubscription(100, TimeUnit.MILLISECONDS);
	}
}
