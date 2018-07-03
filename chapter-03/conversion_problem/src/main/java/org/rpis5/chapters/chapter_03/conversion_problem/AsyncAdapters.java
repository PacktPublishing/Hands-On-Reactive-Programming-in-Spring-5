package org.rpis5.chapters.chapter_03.conversion_problem;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

public final class AsyncAdapters {

	public static <T> CompletionStage<T> toCompletion(ListenableFuture<T> future) {

		CompletableFuture<T> completableFuture = new CompletableFuture<>();

		future.addCallback(completableFuture::complete,
				completableFuture::completeExceptionally);

		return completableFuture;
	}

	public static <T> ListenableFuture<T> toListenable(CompletionStage<T> stage) {
		SettableListenableFuture<T> future = new SettableListenableFuture<>();

		stage.whenComplete((v, t) -> {
			if (t == null) {
				future.set(v);
			}
			else {
				future.setException(t);
			}
		});

		return future;
	}
}
