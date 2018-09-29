package org.rpis5.chapters.chapter_03.vert.x;

import io.reactivex.Flowable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.streams.Pump;
import io.vertx.ext.reactivestreams.ReactiveReadStream;

public class VertxDemoApp extends AbstractVerticle {

    @Override
    public void start() {
        LogService logsService = new MockLogService();
        vertx.createHttpServer()
             .requestHandler(request -> {

                 ReactiveReadStream<Buffer> rrs = ReactiveReadStream.readStream();
                 HttpServerResponse response = request.response();

                 Flowable<Buffer> logs = Flowable.fromPublisher(logsService.stream())
                                                 .map(Buffer::buffer)
                                                 .doOnTerminate(response::end);

                 logs.subscribe(rrs);

                 response.setStatusCode(200);
                 response.setChunked(true);
                 response.putHeader("Content-Type", "text/plain");
                 response.putHeader("Connection", "keep-alive");

                 Pump.pump(rrs, response)
                     .start();
             })
             .listen(8080);
        System.out.println("HTTP server started on port 8080");
    }

    public static void main(final String[] args) {
        Launcher.executeCommand("run", VertxDemoApp.class.getName());
    }
}
