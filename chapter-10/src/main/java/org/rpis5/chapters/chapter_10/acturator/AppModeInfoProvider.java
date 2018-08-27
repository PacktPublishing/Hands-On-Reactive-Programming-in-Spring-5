package org.rpis5.chapters.chapter_10.acturator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AppModeInfoProvider implements InfoContributor {
   private final Random rnd = new Random();

   @Override
   public void contribute(Info.Builder builder) {
      boolean appMode = rnd.nextBoolean();
      builder
         .withDetail("application-mode", appMode ? "experimental" : "stable");
   }
}
