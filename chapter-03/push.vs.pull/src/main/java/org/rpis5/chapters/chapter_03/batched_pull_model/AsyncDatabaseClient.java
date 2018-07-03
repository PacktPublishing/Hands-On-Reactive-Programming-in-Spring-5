package org.rpis5.chapters.chapter_03.batched_pull_model;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface AsyncDatabaseClient {

	CompletionStage<List<Item>> getNextBatchAfterId(String id, int count);
}
