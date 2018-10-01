package com.example.service.impl;

import java.time.Duration;
import java.util.Arrays;

import com.example.service.ChatService;
import com.example.service.gitter.GitterProperties;
import com.example.service.gitter.GitterUriBuilder;
import com.example.service.gitter.dto.MessageResponse;
import lombok.SneakyThrows;
import org.thymeleaf.util.ArrayUtils;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.StreamEmitter;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication(scanBasePackages = "com.example.service.impl.GitterService")
@EnableBinding(Source.class)
@EnableConfigurationProperties(GitterProperties.class)
public class GitterService implements ChatService<MessageResponse> {

    private final WebClient        webClient;
    private final GitterProperties gitterProperties;

    @Autowired
    public GitterService(WebClient.Builder builder, GitterProperties gitterProperties) {
        this.webClient = builder
                            .defaultHeader("Authorization", "Bearer " + gitterProperties.getAuth().getToken())
                            .build();
        this.gitterProperties = gitterProperties;
    }

    @StreamEmitter
    @Output(Source.OUTPUT)
    public Flux<MessageResponse> getMessagesStream() {
        return webClient.get()
                        .uri(GitterUriBuilder.from(gitterProperties.getStream())
                                             .build()
                                             .toUri())
                        .retrieve()
                        .bodyToFlux(MessageResponse.class)
                        .retryBackoff(Long.MAX_VALUE, Duration.ofMillis(500));
    }

    @SneakyThrows
    @StreamEmitter
    @Output(Source.OUTPUT)
    public Flux<MessageResponse> getLatestMessages() {
        return webClient.get()
                        .uri(GitterUriBuilder.from(gitterProperties.getApi())
                                             .build()
                                             .toUri())
                        .retrieve()
                        .bodyToFlux(MessageResponse.class)
                        .timeout(Duration.ofSeconds(1))
                        .retryBackoff(Long.MAX_VALUE, Duration.ofMillis(500));
    }

    public static void main(String... args) {
        String[] newArgs = Arrays.copyOf(args, args.length + 1);
        newArgs[args.length] = "--spring.profiles.active=gitter";

        SpringApplication.run(GitterService.class, newArgs);
    }
}
