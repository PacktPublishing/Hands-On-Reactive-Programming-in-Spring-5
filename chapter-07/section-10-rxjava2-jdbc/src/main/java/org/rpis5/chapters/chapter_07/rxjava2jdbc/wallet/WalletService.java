package org.rpis5.chapters.chapter_07.rxjava2jdbc.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {

   Mono<Void> initializeDatabase();

   Flux<String> generateClients(Integer number, Integer defaultBalance);

   Mono<TxResult> transferMoney(Mono<String> fromOwner, Mono<String> toOwner, Mono<Integer> amount);

   Mono<Statistics> reportAllWallets();

   Mono<Void> removeAllClients();

   enum TxResult {
      SUCCESS,
      NOT_ENOUGH_FUNDS,
      TX_CONFLICT
   }

   @NoArgsConstructor
   @AllArgsConstructor
   @Data
   class Statistics {
      private long totalAccounts;
      private long totalBalance;
      private long totalDeposits;
      private long totalWithdraws;

      public Statistics withWallet(WalletData w) {
         return new Statistics(
            this.totalAccounts + 1,
            this.totalBalance + w.balance(),
            this.totalDeposits + w.deposits(),
            this.totalWithdraws + w.withdraws());
      }
   }
}
