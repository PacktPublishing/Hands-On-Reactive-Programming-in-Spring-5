package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * ATTENTION: Test requires running Docker Engine!
 * ATTENTION: If fails to start, please restart the test!
 */
@Slf4j
@DataMongoTest
class TransactionalWalletServiceTest extends BaseWalletServiceTest {
   private static GenericContainer mongo1;
   private static GenericContainer mongo2;
   private static GenericContainer mongo3;

   @BeforeAll
   public static void init() throws InterruptedException, IOException {
      mongo1 = initMongoNode(27017);
      mongo2 = initMongoNode(27018);
      mongo3 = initMongoNode(27019);

      log.info("Giving MongoDB cluster some time for initialization...");
      Thread.sleep(5_000);

      log.info("Configuring replica set");
      String replicaConfig =
         Resources.toString(Resources.getResource("replica-set-config.js"), Charsets.UTF_8)
            .replace("\n", "")
            .trim();
      log.info("Replica config: {}", replicaConfig);

      Container.ExecResult execResult = mongo1
         .execInContainer("/usr/bin/mongo", "--eval", replicaConfig);

      log.info("Replica result (ERR): {}", execResult.getStderr());
      log.info("Replica result (OUT): {}", execResult.getStdout());
      log.info("Giving time for Replica set initialization...");

      Thread.sleep(10_000);
      log.info("MongoDB started...");
   }

   private static GenericContainer initMongoNode(int exposePort) {
      GenericContainer node = new FixedHostPortGenericContainer("mongo:4.0.1")
         .withFixedExposedPort(exposePort, 27017)
         .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(10)))
         .withLogConsumer(new Consumer<OutputFrame>() {
            @Override
            public void accept(OutputFrame outputFrame) {
               log.debug("[Mongo-{}]: {}", exposePort, outputFrame.getUtf8String().trim());
            }
         })
         .withCommand("mongod --storageEngine wiredTiger --replSet reactive");
      node.start();

      return node;
   }

   @AfterAll
   public static void cleanUp() {
      mongo1.stop();
      mongo2.stop();
      mongo3.stop();
   }

   @DisplayName("Reactive transactions for data transfer")
   @Test
   public void testReactiveTransactionalApproach(
      @Autowired WalletRepository walletRepository,
      @Autowired ReactiveMongoTemplate mongoTemplate
   ) {
      WalletService walletService = new TransactionalWalletService(mongoTemplate, walletRepository);
      Tuple2<Long, Long> expectedActual = simulateOperations(walletService);

      // Whe know that balance should be the same with transactions
      Assert.assertEquals(expectedActual.getT1(), expectedActual.getT2());
   }
}
