package org.rpis5.chapters.chapter_03.push_pull_model;

import org.reactivestreams.Publisher;

public class Puller {

	final AsyncDatabaseClient dbClient = new DelayedFakeAsyncDatabaseClient();

	public Publisher<Item> list(int count) {
		Publisher<Item> source = dbClient.getStreamOfItems();
		TakeFilterOperator<Item> takeFilter =
				new TakeFilterOperator<>(source, count, this::isValid);

		return takeFilter;
	}

	boolean isValid(Item item) {
		return Integer.parseInt(item.getId()) % 2 == 0;
	}
}
