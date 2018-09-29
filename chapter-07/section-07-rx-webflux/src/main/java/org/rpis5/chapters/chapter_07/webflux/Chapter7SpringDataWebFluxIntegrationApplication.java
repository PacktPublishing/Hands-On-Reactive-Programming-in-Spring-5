package org.rpis5.chapters.chapter_07.webflux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class Chapter7SpringDataWebFluxIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication
         .run(Chapter7SpringDataWebFluxIntegrationApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> routerFunction(
	   ChatHandler chatHandler
   ) {
		return RouterFunctions
			.route(
				GET("/"),
				serverRequest -> ServerResponse
					.ok()
					.contentType(MediaType.APPLICATION_STREAM_JSON)
					.body(chatHandler
                  .messageStream(), Message.class))
         .andRoute(
            GET("/{user}"),
            serverRequest -> {
               String user = serverRequest.pathVariable("user");
               return ServerResponse
                  .ok()
                  .contentType(MediaType.APPLICATION_STREAM_JSON)
                  .body(chatHandler
                     .messageStreamForUser(user), Message.class);
            });
	}
}
