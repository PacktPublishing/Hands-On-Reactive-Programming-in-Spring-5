package org.rpis5.chapters.chapter_10.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebConfiguration {

   @Bean
   public WebFilter indexHtmlFilter() {
      return (exchange, chain) -> {
         if (exchange.getRequest().getURI().getPath().equals("/")) {
            return chain.filter(exchange
               .mutate()
               .request(exchange
                  .getRequest()
                  .mutate()
                  .path("/index.html")
                  .build())
               .build());
         }
         return chain.filter(exchange);
      };
   }

}
