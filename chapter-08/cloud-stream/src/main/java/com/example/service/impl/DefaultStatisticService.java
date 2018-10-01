package com.example.service.impl;

import java.time.Duration;
import java.util.Arrays;

import com.example.controller.vm.UserVM;
import com.example.controller.vm.UsersStatisticVM;
import com.example.repository.MessageRepository;
import com.example.repository.UserRepository;
import com.example.service.StatisticService;
import com.example.service.gitter.dto.MessageResponse;
import com.example.service.impl.utils.MessageMapper;
import com.example.service.impl.utils.UserMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = {
     "com.example.repository",
     "com.example.repository.impl",
     "com.example.service.impl.DefaultStatisticService"
})
@EnableReactiveMongoRepositories (basePackages = {
        "com.example.repository",
        "com.example.repository.impl"
})
@EnableBinding(Processor.class)
public class DefaultStatisticService implements StatisticService {
    private static final UserVM EMPTY_USER = new UserVM("", "");

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public DefaultStatisticService(UserRepository userRepository,
            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @StreamListener(Processor.INPUT)
    @Output(Processor.OUTPUT)
    public Flux<UsersStatisticVM> updateStatistic(Flux<MessageResponse> messagesFlux) {
        return messagesFlux.map(MessageMapper::toDomainUnit)
                           .transform(messageRepository::saveAll)
                           .retryBackoff(Long.MAX_VALUE, Duration.ofMillis(500))
                           .onBackpressureLatest()
                           .concatMap(e -> this.doGetUserStatistic(), 1)
                           .onErrorContinue((t, e) -> {});
    }

    private Mono<UsersStatisticVM> doGetUserStatistic() {
        Mono<UserVM> topActiveUserMono = userRepository.findMostActive()
                                                       .map(UserMapper::toViewModelUnits)
                                                       .defaultIfEmpty(EMPTY_USER);

        Mono<UserVM> topMentionedUserMono = userRepository.findMostPopular()
                                                          .map(UserMapper::toViewModelUnits)
                                                          .defaultIfEmpty(EMPTY_USER);

        return Mono.zip(topActiveUserMono, topMentionedUserMono, UsersStatisticVM::new)
                   .timeout(Duration.ofSeconds(2));
    }

    public static void main(String[] args) {
        String[] newArgs = Arrays.copyOf(args, args.length + 1);
        newArgs[args.length] = "--spring.profiles.active=statistic";

        SpringApplication.run(DefaultStatisticService.class, args);
    }
}