package org.rpis5.chapters.chapter_07.webflux;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatHandler {
   private final MessageRepository messageRepository;

   public Flux<Message> messageStream() {
      return messageRepository.findBy();
   }

   public Flux<Message> messageStreamForUser(String user) {
      return messageRepository.findByUser(user);
   }
}
