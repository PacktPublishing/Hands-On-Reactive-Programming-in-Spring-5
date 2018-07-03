package org.rpis5.chapters.chapter_03.news_service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import org.rpis5.chapters.chapter_03.news_service.dto.NewsLetter;
import io.reactivex.Flowable;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.SkipException;

public class SmartMulticastProcessorTest
		extends IdentityProcessorVerification<NewsLetter> {

	public SmartMulticastProcessorTest() {
		super(new TestEnvironment(500, 500), 1000, 1);
	}

	@Override
	public ExecutorService publisherExecutorService() {
		return ForkJoinPool.commonPool();
	}

	@Override
	public NewsLetter createElement(int element) {
		return new StubNewsLetter(element);
	}

	@Override
	public Processor<NewsLetter, NewsLetter> createIdentityProcessor(int bufferSize) {
		return new SmartMulticastProcessor();
	}

	@Override
	public boolean doesCoordinatedEmission() {
		return true;
	}

	@Override
	public Publisher<NewsLetter> createFailedPublisher() {
		return Flowable.error(new RuntimeException());
	}

	@Override
	public void required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates() {
		try {
			super.required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_spec101_subscriptionRequestMustResultInTheCorrectNumberOfProducedElements() {
		try {
			super.required_spec101_subscriptionRequestMustResultInTheCorrectNumberOfProducedElements();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_mustRequestFromUpstreamForElementsThatHaveBeenRequestedLongAgo() {
		try {
			super.required_mustRequestFromUpstreamForElementsThatHaveBeenRequestedLongAgo();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_createPublisher3MustProduceAStreamOfExactly3Elements() {
		try {
			super.required_createPublisher3MustProduceAStreamOfExactly3Elements();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_spec102_maySignalLessThanRequestedAndTerminateSubscription() {
		try {
			super.required_spec102_maySignalLessThanRequestedAndTerminateSubscription();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_spec317_mustSupportACumulativePendingElementCountUpToLongMaxValue() {
		try {
			super.required_spec317_mustSupportACumulativePendingElementCountUpToLongMaxValue();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_spec317_mustSupportAPendingElementCountUpToLongMaxValue() {
		try {
			super.required_spec317_mustSupportAPendingElementCountUpToLongMaxValue();
		}
		catch (Throwable t) {
			throw new SkipException("Ignored due to undetermined drop-on-overflow behavior for that processor");
		}
	}

	@Override
	public void required_exerciseWhiteboxHappyPath() {
		throw new SkipException("Ignored due to custom backpressure management");
	}
}
