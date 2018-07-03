package org.rpis5.chapters.chapter_03.batched_pull_model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class DelayedFakeAsyncDatabaseClient implements AsyncDatabaseClient {

	@Override
	public CompletionStage<List<Item>> getNextBatchAfterId(String id, int count) {
		CompletableFuture<List<Item>> future = new CompletableFuture<>();

		Flowable.range(Integer.parseInt(id) + 1, count)
		        .map(i -> new Item("" + i))
		        .collectInto(new ArrayList<Item>(), ArrayList::add)
		        .delay(1000, TimeUnit.MILLISECONDS)
		        .subscribe(future::complete);

		return future;
	}
}
