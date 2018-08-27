package org.rpis5.chapters.chapter_02.rx_app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rx.Observable;

import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class TemperatureSensor {
   private static final Logger log = LoggerFactory.getLogger(TemperatureSensor.class);
   private final Random rnd = new Random();

   private final Observable<Temperature> dataStream =
      Observable
         .range(0, Integer.MAX_VALUE)
         .concatMap(ignore -> Observable
            .just(1)
            .delay(rnd.nextInt(5000), MILLISECONDS)
            .map(ignore2 -> this.probe()))
         .publish()
         .refCount();

   public Observable<Temperature> temperatureStream() {
     return dataStream;
   }

   private Temperature probe() {
      double actualTemp = 16 + rnd.nextGaussian() * 10;
      log.info("Asking sensor, sensor value: {}", actualTemp);
      return new Temperature(actualTemp);
   }
}
