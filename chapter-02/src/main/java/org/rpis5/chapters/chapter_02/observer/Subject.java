package org.rpis5.chapters.chapter_02.observer;

public interface Subject<T> {
   void registerObserver(Observer<T> observer);

   void unregisterObserver(Observer<T> observer);

   void notifyObservers(T event);
}
