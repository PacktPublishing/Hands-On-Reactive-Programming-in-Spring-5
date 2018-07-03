package org.rpis5.chapters.chapter_03.pure_pull_model;

import java.util.Queue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class PullerTest {

	@Test
	public void pullTest() throws InterruptedException {
		CountDownLatch l = new CountDownLatch(1);
		Puller puller = new Puller();

		CompletionStage<Queue<Item>> list = puller.list(10);

		list.thenAccept(q -> {
			MatcherAssert.assertThat(q, Matchers.allOf(
					Matchers.hasSize(10),
					Matchers.contains(
							Matchers.hasProperty("id", Matchers.equalTo("2")),
							Matchers.hasProperty("id", Matchers.equalTo("4")),
							Matchers.hasProperty("id", Matchers.equalTo("6")),
							Matchers.hasProperty("id", Matchers.equalTo("8")),
							Matchers.hasProperty("id", Matchers.equalTo("10")),
							Matchers.hasProperty("id", Matchers.equalTo("12")),
							Matchers.hasProperty("id", Matchers.equalTo("14")),
							Matchers.hasProperty("id", Matchers.equalTo("16")),
							Matchers.hasProperty("id", Matchers.equalTo("18")),
							Matchers.hasProperty("id", Matchers.equalTo("20"))
					)
			));
			l.countDown();
		})
		.exceptionally(t -> {
			l.countDown();
			throw new RuntimeException(t);
		});

		l.await();
	}
}
