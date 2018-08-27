package org.rpis5.chapters.chapter_02.seach_engine;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface FutureSearchEngine {
   CompletableFuture<List<URL>> search(String query, int limit);
}
