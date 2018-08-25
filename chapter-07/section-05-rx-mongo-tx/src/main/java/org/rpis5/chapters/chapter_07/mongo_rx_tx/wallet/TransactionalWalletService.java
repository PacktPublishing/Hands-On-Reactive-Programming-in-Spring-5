/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

import static java.time.Instant.now;
import static reactor.function.TupleUtils.function;

@Slf4j
@Component
public class TransactionalWalletService extends BaseWalletService {
   private final ReactiveMongoTemplate mongoTemplate;

   public TransactionalWalletService(
      ReactiveMongoTemplate mongoTemplate,
      WalletRepository walletRepository
   ) {
      super(walletRepository);
      this.mongoTemplate = mongoTemplate;
   }

   @Override
   public Mono<TxResult> transferMoney(
      Mono<String> fromOwner,
      Mono<String> toOwner,
      Mono<Integer> requestAmount
   ) {
      return Mono.zip(
         fromOwner,
         toOwner,
         requestAmount
      ).flatMap(function((from, to, amount) -> {
         Instant start = now();
         return transferMoney(from, to, amount)
            .retryBackoff(10, Duration.ofMillis(10))
            .onErrorReturn(TxResult.TX_CONFLICT)
            .doOnSuccess(result -> log.info("Updated took: {}", Duration.between(start, now())));
      }));
   }

   private Mono<TxResult> transferMoney(
      String from,
      String to,
      Integer amount
   ) {
      return mongoTemplate.inTransaction().execute(session ->
         session
            .findOne(queryForOwner(from), Wallet.class)
            .flatMap(fromWallet -> session
               .findOne(queryForOwner(to), Wallet.class)
               .flatMap(toWallet -> {
                  if (fromWallet.hasEnoughFunds(amount)) {
                     fromWallet.withdraw(amount);
                     toWallet.deposit(amount);

                     return session.save(fromWallet)
                        .then(session.save(toWallet))
                        .then(Mono.just(TxResult.SUCCESS));
                  } else {
                     return Mono.just(TxResult.NOT_ENOUGH_FUNDS);
                  }
               }))
      )
         .onErrorResume(e -> Mono.error(new RuntimeException("Conflict")))
         .next();
   }

   private Query queryForOwner(String from) {
      return Query.query(new Criteria("owner").is(from));
   }
}
