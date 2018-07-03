package org.rpis5.chapters.chapter_03.push_model;

import rx.Observable;

public interface AsyncDatabaseClient {

	Observable<Item> getStreamOfItems();
}
