package org.rpis5.chapters.chapter_03.push_model;

import rx.Observable;

public class Puller {

	final AsyncDatabaseClient dbClient = new DelayedFakeAsyncDatabaseClient();

	public Observable<Item> list(int count) {
		return dbClient.getStreamOfItems()
		               .filter(this::isValid)
		               .take(count);
	}

	boolean isValid(Item item) {
		return Integer.parseInt(item.getId()) % 2 == 0;
	}
}
