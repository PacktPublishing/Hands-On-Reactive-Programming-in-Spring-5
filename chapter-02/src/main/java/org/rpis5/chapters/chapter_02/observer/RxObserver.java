package org.rpis5.chapters.chapter_02.observer;

public interface RxObserver<T> {
   void onNext(T next);
   void onComplete();
   void onError(Exception e);
}
