package org.rpis5.chapters.chapter_07.mongo_rx_tx.wallet;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WalletRepository
   extends ReactiveMongoRepository<Wallet, ObjectId> {

   Mono<Wallet> findByOwner(Mono<String> owner);
}
