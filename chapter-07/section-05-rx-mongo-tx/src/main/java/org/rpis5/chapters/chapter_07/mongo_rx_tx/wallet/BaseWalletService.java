package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseWalletService implements WalletService {
   protected final WalletRepository walletRepository;

   public Flux<String> generateClients(Integer number, Integer defaultBalance) {
      return walletRepository.saveAll(
         Flux.range(1, number)
         .map(id -> format("client-%05d", id))
         .map(owner -> Wallet.wallet(owner, defaultBalance))
      ).map(Wallet::getOwner);
   }

   @Override
   public Mono<Statistics> reportAllWallets() {
      return walletRepository
         .findAll()
         .sort(Comparator.comparing(Wallet::getOwner))
         .doOnNext(w ->
            log.info(format("%10s: %7d$ (d: %5s | w: %5s)",
            w.getOwner(), w.getBalance(), w.getDepositOperations(), w.getWithdrawOperations())))
         .reduce(new Statistics(), Statistics::withWallet);
   }

   @Override
   public Mono<Void> removeAllClients() {
      return walletRepository.deleteAll();
   }
}
