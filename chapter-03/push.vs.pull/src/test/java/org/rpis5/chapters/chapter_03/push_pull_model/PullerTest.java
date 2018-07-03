package org.rpis5.chapters.chapter_03.push_pull_model;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PullerTest {

	@Test
	public void pullTest() throws InterruptedException {
		CountDownLatch l = new CountDownLatch(1);
		Puller puller = new Puller();

		puller.list(10)
		      .subscribe(new Subscriber<Item>() {
			      final ArrayList<Item> list = new ArrayList<>();

			      @Override
			      public void onSubscribe(Subscription s) {
				      s.request(Long.MAX_VALUE);
			      }

			      @Override
			      public void onNext(Item item) {
				      list.add(item);
			      }

			      @Override
			      public void onError(Throwable t) {

			      }

			      @Override
			      public void onComplete() {
				      MatcherAssert.assertThat(list, Matchers.allOf(
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
			      }
		      });

		if(!l.await(2, TimeUnit.SECONDS)) {
			throw new RuntimeException();
		}
	}

}
