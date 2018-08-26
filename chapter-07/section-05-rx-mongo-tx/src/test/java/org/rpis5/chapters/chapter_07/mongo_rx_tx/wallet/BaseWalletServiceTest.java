package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import static java.time.Duration.between;
import static java.time.Instant.now;

@Slf4j
@DataMongoTest
class BaseWalletServiceTest {
   private final Random rnd = new Random();

   void simulateOperations(WalletService walletService) {
      int accounts = 500;
      int defaultBalance = 1000;
      int iterations = 10000;
      int parallelism = 200;

      // given
      // Clean up just in case
      walletService.removeAllClients()
         .block();

      List<String> clients = walletService.generateClients(accounts, defaultBalance)
         .doOnNext(name -> log.info("Created wallet for: {}", name))
         .collectList()
         .block();

      // when
      Scheduler mongoScheduler = Schedulers
         .newParallel("MongoOperations", parallelism);

      Instant startTime = now();
      Operations operations = Flux.range(0, iterations)
         .flatMap(i -> Mono
            .delay(Duration.ofMillis(rnd.nextInt(10)))
            .publishOn(mongoScheduler)
            .flatMap(_i -> {
               int amount = rnd.nextInt(defaultBalance);
               int from = rnd.nextInt(accounts);
               int to;
               do {
                  to = rnd.nextInt(accounts);
               } while (to == from);

               return walletService.transferMoney(
                  Mono.just(clients.get(from)),
                  Mono.just(clients.get(to)),
                  Mono.just(amount));
            }))
         .reduce(Operations.start(), Operations::outcome)
         .block();

      // then
      log.info("--- Results --------------------------------");
      WalletService.Statistics statistics = walletService.reportAllWallets()
         .block();
      log.info("Expected/actual total balance: {}$ / {}$ | Took: {}",
         accounts * defaultBalance, statistics.getTotalBalance(), between(startTime, now()));
      log.info("{}", statistics);
      log.info("{}", operations);

      log.info("Cleaning up database");
      walletService.removeAllClients()
         .block();
   }

   @ToString
   @RequiredArgsConstructor
   public static class Operations {
      private final int successful;
      private final int notEnoughFunds;
      private final int conflict;

      public Operations outcome(WalletService.TxResult result) {
         switch (result){
            case SUCCESS:
               return new Operations(successful + 1, notEnoughFunds, conflict);
            case NOT_ENOUGH_FUNDS:
               return new Operations(successful, notEnoughFunds + 1, conflict);
            case TX_CONFLICT:
               return new Operations(successful, notEnoughFunds, conflict + 1);
            default:
               throw new RuntimeException("Unexpected status:" + result);
         }
      }

      public static Operations start() {
         return new Operations(0, 0, 0);
      }
   }
}
