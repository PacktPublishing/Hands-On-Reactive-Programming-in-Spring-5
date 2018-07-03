package org.rpis5.chapters.chapter_03.push_pull_model;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

public class TCKPullerTest extends PublisherVerification<Item> {

	public TCKPullerTest() {
		super(new TestEnvironment(1000, 1000));
	}

	@Override
	public Publisher<Item> createPublisher(long elements) {
		return new Puller().list(elements > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)elements);
	}

	@Override
	public Publisher<Item> createFailedPublisher() {
		return null;
	}
}
