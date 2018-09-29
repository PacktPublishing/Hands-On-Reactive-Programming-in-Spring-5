package org.rpis5.chapters.chapter_02.pub_sub_app;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@SpringBootApplication
public class Application implements AsyncConfigurer {

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);
   }

   @Override
   public Executor getAsyncExecutor() {
      ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
      executor.setThreadNamePrefix("sse-");
      executor.setCorePoolSize(2);
      executor.setMaxPoolSize(100);
      executor.setQueueCapacity(5);
      executor.initialize();
      return executor;
   }

   @Override
   public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
      return new SimpleAsyncUncaughtExceptionHandler();
   }
}
