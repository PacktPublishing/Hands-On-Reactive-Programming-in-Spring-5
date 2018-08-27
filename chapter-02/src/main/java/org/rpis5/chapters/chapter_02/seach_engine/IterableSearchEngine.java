package org.rpis5.chapters.chapter_02.seach_engine;

import java.net.URL;

@SuppressWarnings("unused")
public interface IterableSearchEngine {
   Iterable<URL> search(String query, int limit);
}
