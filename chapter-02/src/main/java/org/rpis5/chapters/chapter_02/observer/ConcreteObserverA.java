package org.rpis5.chapters.chapter_02.observer;

public class ConcreteObserverA implements Observer<String> {
   @Override
   public void observe(String event) {
      System.out.println("Observer A: " + event);
   }
}
