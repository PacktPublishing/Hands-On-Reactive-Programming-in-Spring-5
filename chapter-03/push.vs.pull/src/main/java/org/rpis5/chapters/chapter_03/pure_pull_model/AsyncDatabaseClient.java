package org.rpis5.chapters.chapter_03.pure_pull_model;

import java.util.concurrent.CompletionStage;

public interface AsyncDatabaseClient {

	CompletionStage<Item> getNextAfterId(String id);
}
