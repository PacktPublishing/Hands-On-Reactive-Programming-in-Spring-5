package org.rpis5.chapters.chapter_09;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService service) {
		paymentService = service;
	}

	@GetMapping("")
	public Flux<Payment> list() {
		return paymentService.list();
	}

	@PostMapping("")
	public Mono<String> send(Mono<Payment> payment) {
		return paymentService.send(payment);
	}
}