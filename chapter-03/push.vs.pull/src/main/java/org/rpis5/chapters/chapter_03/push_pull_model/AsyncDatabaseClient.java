package org.rpis5.chapters.chapter_03.push_pull_model;

import org.reactivestreams.Publisher;

public interface AsyncDatabaseClient {

	Publisher<Item> getStreamOfItems();
}
