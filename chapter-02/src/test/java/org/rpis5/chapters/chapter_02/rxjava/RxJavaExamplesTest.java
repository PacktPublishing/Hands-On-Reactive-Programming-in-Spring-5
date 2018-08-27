package org.rpis5.chapters.chapter_02.rxjava;

import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RxJavaExamplesTest {

   @Test
   @SuppressWarnings("Depricated")
   public void simpleRxJavaWorkflow() {
      Observable<String> observable = Observable.create(
         new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> sub) {
               sub.onNext("Hello, reactive world!");
               sub.onCompleted();
            }
         }
      );
   }

   @Test
   @SuppressWarnings("Depricated")
   public void simpleRxJavaWorkflowWithLambdas() {
      Observable.create(
         sub -> {
            sub.onNext("Hello, reactive world!");
            sub.onCompleted();
         }
      ).subscribe(
         System.out::println,
         System.err::println,
         () -> System.out.println("Done!")
      );
   }

   @Test
   public void creatingRxStreams() {
      Observable.just("1", "2", "3", "4");
      Observable.from(new String[]{"A", "B", "C"});
      Observable.from(Collections.<String>emptyList());

      Observable<String> hello = Observable.fromCallable(() -> "Hello ");
      Future<String> future =
              Executors.newCachedThreadPool().submit(() -> "World");
      Observable<String> world = Observable.from(future);

      Observable.concat(hello, world, Observable.just("!"))
         .forEach(System.out::print);
   }

   @Test
   public void zipOperatorExample() {
      Observable.zip(
              Observable.just("A", "B", "C"),
              Observable.just("1", "2", "3"),
              (x, y) -> x + y
      ).forEach(System.out::println);
   }

   @Test
   public void timeBasedSequenceExample() throws InterruptedException {
      Observable.interval(1, TimeUnit.SECONDS)
         .subscribe(e -> System.out.println("Received: " + e));

      Thread.sleep(5000);
   }

   @Test
   public void managingSubscription() {
      AtomicReference<Subscription> subscription = new AtomicReference<>();
      subscription.set(Observable.interval(100, MILLISECONDS)
         .subscribe(e -> {
            System.out.println("Received: " + e);
            if (e >= 3) {
               subscription.get().unsubscribe();
            }
         }));

      do {
         // executing something useful...
      } while (!subscription.get().isUnsubscribed());
   }

   @Test
   public void managingSubscription2() throws InterruptedException {
      CountDownLatch externalSignal = new CountDownLatch(3);

      Subscription subscription = Observable
              .interval(100, MILLISECONDS)
              .subscribe(System.out::println);

      externalSignal.await(450, MILLISECONDS);
      subscription.unsubscribe();
   }

   @Test
   public void deferSynchronousRequest() throws Exception {
      String query = "query";
      Observable.fromCallable(() -> doSlowSyncRequest(query))
         .subscribeOn(Schedulers.io())
         .subscribe(this::processResult);

      Thread.sleep(1000);
   }

   private String doSlowSyncRequest(String query) {
      return "result";
   }

   private void processResult(String result) {
      System.out.println(Thread.currentThread().getName() + ": " + result);
   }

}
