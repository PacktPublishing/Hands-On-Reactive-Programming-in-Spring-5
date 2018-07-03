package org.rpis5.chapters.chapter_03.conversion_problem;

import java.util.concurrent.CompletionStage;

public interface AsyncDatabaseClient {

	<T> CompletionStage<T> store(CompletionStage<T> stage);
}
