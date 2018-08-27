package org.rpis5.chapters.chapter_02.rx_app_no_logs;

import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class TemperatureSensor {
   private final Random rnd = new Random();

   private final Observable<Temperature> dataStream =
      Observable
         .range(0, Integer.MAX_VALUE)
         .concatMap(tick -> Observable
            .just(tick)
            .delay(rnd.nextInt(5000), MILLISECONDS)
            .map(tickValue -> this.probe()))
         .publish()
         .refCount();

   private Temperature probe() {
      return new Temperature(16 + rnd.nextGaussian() * 10);
   }

   public Observable<Temperature> temperatureStream() {
      return dataStream;
   }
}
