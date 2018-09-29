package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import static java.time.Duration.between;
import static java.time.Instant.now;

@SuppressWarnings("Duplicates")
@Slf4j
@DataMongoTest
class BaseWalletServiceTest {

   Tuple2<Long, Long> simulateOperations(WalletService walletService) {
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
      OperationalSimulation simulation = OperationalSimulation.builder()
         .walletService(walletService)
         .clients(clients)
         .defaultBalance(defaultBalance)
         .iterations(iterations)
         .simulationScheduler(mongoScheduler)
         .build();

      OperationStats operations = simulation
         .runSimulation()
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

      return Tuples.of((long) accounts * defaultBalance, statistics.getTotalBalance());
   }

   @Builder
   @RequiredArgsConstructor
   public static class OperationalSimulation {
      private final WalletService walletService;
      private final List<String> clients;
      private final int defaultBalance;
      private final int iterations;
      private final Scheduler simulationScheduler;

      private final Random rnd = new Random();

      public Mono<OperationStats> runSimulation() {
         return Flux.range(0, iterations)
            .flatMap(i -> Mono
               .delay(Duration.ofMillis(rnd.nextInt(10)))
               .publishOn(simulationScheduler)
               .flatMap(_i -> {
                  String fromOwner = randomOwner();
                  String toOwner = randomOwnerExcept(fromOwner);
                  int amount = randomTransferAmount();

                  return walletService.transferMoney(
                     Mono.just(fromOwner),
                     Mono.just(toOwner),
                     Mono.just(amount));
               }))
            .reduce(OperationStats.start(), OperationStats::countTxResult);
      }

      private int randomTransferAmount() {
         return rnd.nextInt(defaultBalance);
      }

      private String randomOwner() {
         int from = rnd.nextInt(clients.size());
         return clients.get(from);
      }

      private String randomOwnerExcept(String fromOwner) {
         String toOwner;
         do {
            int to = rnd.nextInt(clients.size());
            toOwner = clients.get(to);
         } while (fromOwner.equals(toOwner));
         return toOwner;
      }
   }

   @ToString
   @RequiredArgsConstructor
   public static class OperationStats {
      private final int successful;
      private final int notEnoughFunds;
      private final int conflict;

      public OperationStats countTxResult(WalletService.TxResult result) {
         switch (result){
            case SUCCESS:
               return new OperationStats(successful + 1, notEnoughFunds, conflict);
            case NOT_ENOUGH_FUNDS:
               return new OperationStats(successful, notEnoughFunds + 1, conflict);
            case TX_CONFLICT:
               return new OperationStats(successful, notEnoughFunds, conflict + 1);
            default:
               throw new RuntimeException("Unexpected status:" + result);
         }
      }

      public static OperationStats start() {
         return new OperationStats(0, 0, 0);
      }
   }
}
