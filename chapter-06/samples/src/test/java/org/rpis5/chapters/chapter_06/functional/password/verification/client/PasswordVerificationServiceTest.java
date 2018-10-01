package org.rpis5.chapters.chapter_06.functional.password.verification.client;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rpis5.chapters.chapter_06.functional.password.verification.server.StandaloneApplication;
import reactor.test.StepVerifier;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.*;

public class PasswordVerificationServiceTest {

    @Before
    public void setUp() throws InterruptedException {
        new Thread(StandaloneApplication::main).start();
        Thread.sleep(1000);
    }

    @Test
    public void checkApplicationRunning() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(18);
        DefaultPasswordVerificationService service =
                new DefaultPasswordVerificationService(WebClient.builder());

        StepVerifier.create(service.check("test", encoder.encode("test")))
                    .expectSubscription()
                    .expectComplete()
                    .verify(Duration.ofSeconds(30));

    }
}