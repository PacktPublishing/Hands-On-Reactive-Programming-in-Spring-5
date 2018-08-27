package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

@Slf4j
@DataMongoTest
class NaiveWalletServiceTest extends BaseWalletServiceTest {
   private static GenericContainer mongo;

   @BeforeAll
   public static void init() throws InterruptedException {
      mongo = new FixedHostPortGenericContainer("mongo:4.0.1")
         .withFixedExposedPort(27017, 27017)
         .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(10)));
      mongo.start();

      log.info("Giving MongoDB some time for initialization...");
      Thread.sleep(5_000);
      log.info("MongoDB started");
   }

   @AfterAll
   public static void cleanUp() {
      mongo.stop();
   }

   @DisplayName("Naive approach for data transfer")
   @Test
   public void testNaiveApproach(
      @Autowired WalletRepository walletRepository
   ) {
      WalletService walletService = new NaiveWalletService(walletRepository);
      simulateOperations(walletService);
   }
}
