package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static reactor.function.TupleUtils.function;

@Slf4j
@Component
public class NaiveWalletService extends BaseWalletService {

   public NaiveWalletService(WalletRepository walletRepository) {
      super(walletRepository);
   }

   @Override
   public Mono<TxResult> transferMoney(
      Mono<String> fromOwner,
      Mono<String> toOwner,
      Mono<Integer> amount
   ) {
      return Mono.zip(
         walletRepository.findByOwner(fromOwner),
         walletRepository.findByOwner(toOwner),
         amount
      ).flatMap(function((from, to, transferAmount) -> {
         if (from.hasEnoughFunds(transferAmount)) {
            from.withdraw(transferAmount);
            to.deposit(transferAmount);

            return Mono.zip(
               walletRepository.save(from),
               walletRepository.save(to))
               .flatMap(_i -> {
                  log.debug("Transferred from: {}, to: {}, amount: {}",
                     from.getOwner(), to.getOwner(), transferAmount);
                  return Mono.just(TxResult.SUCCESS);
               });
         } else {
            log.debug("FAILED to transfer from: {}, to: {}, amount: {}. Insufficient founds!",
               from.getOwner(), to.getOwner(), transferAmount);
            return Mono.just(TxResult.NOT_ENOUGH_FUNDS);
         }
      }));
   }
}
