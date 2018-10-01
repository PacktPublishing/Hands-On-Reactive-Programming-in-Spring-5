package com.example.controller;

import java.util.Arrays;

import com.example.controller.vm.MessageVM;
import com.example.controller.vm.UsersStatisticVM;
import com.example.service.gitter.dto.MessageResponse;
import com.example.service.impl.utils.MessageMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/info")
@EnableBinding({MessagesSource.class, StatisticSource.class})
@SpringBootApplication(scanBasePackages = "com.example.controller")
public class InfoResource {

	private final ReplayProcessor<UsersStatisticVM> statisticStream = ReplayProcessor.cacheLast();
	private final ReplayProcessor<MessageVM>        messagesStream  = ReplayProcessor.create(50);

    @StreamListener
    public void listen(
        @Input(MessagesSource.INPUT) Flux<MessageResponse> messages,
	    @Input(StatisticSource.INPUT) Flux<UsersStatisticVM> statistic
    ) {
        messages.map(MessageMapper::toViewModelUnit)
                .subscribeWith(messagesStream);
	    statistic.subscribeWith(statisticStream);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<?> stream() {
	    return Flux.merge(messagesStream, statisticStream);
    }

	public static void main(String[] args) {
        String[] newArgs = Arrays.copyOf(args, args.length + 1);
        newArgs[args.length] = "--spring.profiles.active=ui";

        SpringApplication.run(InfoResource.class, args);
	}
}

interface StatisticSource  {

    String INPUT = "statistic";

    @Input(INPUT)
    MessageChannel input();
}

interface MessagesSource  {

    String INPUT = "messages";

    @Input(INPUT)
    MessageChannel input();
}