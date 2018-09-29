package org.rpis5.chapters.chapter_03.rxjava_reactivestreams;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import ratpack.func.Function;
import ratpack.server.RatpackServer;
import ratpack.sse.ServerSentEvents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static ratpack.sse.ServerSentEvents.serverSentEvents;

@SpringBootApplication
@RestController
public class LogServiceApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context =
                SpringApplication.run(LogServiceApplication.class, args);

        LogService logsService = context.getBean(LogService.class);
        FileService fileService = context.getBean(FileService.class);

        File tempFile = File.createTempFile("LogServiceApplication", ".tmp");
        tempFile.deleteOnExit();

        fileService.writeTo(tempFile.getAbsolutePath(), logsService.stream());

        RatpackServer.start(server ->
                server.handlers(chain ->
                        chain.all(ctx -> {

                            Publisher<String> logs = logsService.stream();

                            ServerSentEvents events = serverSentEvents(
                                    logs,
                                    event -> event.id(Objects::toString)
                                                  .event("log")
                                                  .data(Function.identity())
                            );

                            ctx.render(events);
                        })
                )
        );
    }

    @RequestMapping("/")
    public String root() {
        return "Please go to http://localhost:8080/logs";
    }

    @RequestMapping("/logs")
    public SseEmitter mockLogs() {
        SseEmitter emitter = new SseEmitter();
        Flowable.interval(300, TimeUnit.MILLISECONDS)
                .map(i -> "[" + System.nanoTime() + "] [LogServiceApplication] [Thread " + Thread.currentThread() + "] Some loge here " + i + "\n")
                .subscribe(emitter::send, emitter::completeWithError, emitter::complete);
        return emitter;
    }
}
