package org.rpis5.chapters.chapter_09;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ImportAutoConfiguration({
		TestSecurityConfiguration.class,
		TestWebClientBuilderConfiguration.class
})
@RunWith(SpringRunner.class)
@WithMockUser
@SpringBootTest
@AutoConfigureWebTestClient
public class PaymentControllerTests {

	@Autowired
	WebTestClient client;

	@MockBean
	ExchangeFunction exchangeFunction;

	@Test
	public void verifyPaymentsWasSentAndStored() {
		Mockito.when(exchangeFunction.exchange(Mockito.any()))
		       .thenReturn(Mono.just(MockClientResponse.create(201, Mono.empty())));
		client.post()
		      .uri("/payments/")
		      .syncBody(new Payment())
		      .exchange()
		      .expectStatus().is2xxSuccessful()
		      .returnResult(String.class)
		      .getResponseBody()
		      .as(StepVerifier::create)
		      .expectNextCount(1)
		      .expectComplete()
		      .verify();

		Mockito.verify(exchangeFunction).exchange(Mockito.any());
	}
}
