package org.rpis5.chapters.chapter_02.observer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class ConcreteSubject implements Subject<String> {
   private final Set<Observer<String>> observers =
           new CopyOnWriteArraySet<>();

   public void registerObserver(Observer<String> observer) {
      observers.add(observer);
   }

   public void unregisterObserver(Observer<String> observer) {
      observers.remove(observer);
   }

   public void notifyObservers(String event) {
      observers.forEach(observer -> observer.observe(event));
   }
}
