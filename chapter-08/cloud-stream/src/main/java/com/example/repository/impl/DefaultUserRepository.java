package com.example.repository.impl;

import com.example.domain.Message;
import com.example.domain.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
class DefaultUserRepository implements UserRepository {

    private final ReactiveMongoOperations mongoOperations;

    @Autowired
    DefaultUserRepository(ReactiveMongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Mono<User> findMostActive() {
        return Mono.fromDirect(mongoOperations
                .aggregate(Aggregation.newAggregation(
                        Aggregation.group("user.id")
                                .addToSet("user.name").as("name")
                                .addToSet("user.displayName").as("displayName")
                                .count().as("popularity"),
                        Aggregation.sort(new Sort(new Sort.Order(Sort.Direction.DESC, "popularity"))),
                        Aggregation.limit(1),
                        Aggregation.unwind("name"),
                        Aggregation.unwind("displayName")
                ), Message.class, User.class));
    }

    @Override
    public Mono<User> findMostPopular() {
        return Mono.fromDirect(mongoOperations
                .aggregate(Aggregation.newAggregation(
                        Aggregation.unwind("mentions"),
                        Aggregation.group("mentions.userId")
                                .addToSet("mentions.screenName").as("name")
                                .addToSet("mentions.screenName").as("displayName")
                                .count().as("popularity"),
                        Aggregation.sort(new Sort(new Sort.Order(Sort.Direction.DESC, "popularity"))),
                        Aggregation.limit(1),
                        Aggregation.unwind("name"),
                        Aggregation.unwind("displayName")
                ), Message.class, User.class));
    }
}
