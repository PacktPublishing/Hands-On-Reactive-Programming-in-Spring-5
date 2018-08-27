package org.rpis5.chapters.chapter_02.seach_engine;

import rx.Observable;

import java.net.URL;

@SuppressWarnings("unused")
public interface RxSearchEngine {
   Observable<URL> search(String query);
}
