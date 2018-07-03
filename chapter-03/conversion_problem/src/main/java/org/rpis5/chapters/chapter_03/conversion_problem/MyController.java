package org.rpis5.chapters.chapter_03.conversion_problem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpMessageConverterExtractor;

@RestController
public class MyController {
	private final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

	{
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(new StringHttpMessageConverter());
		this.messageConverters.add(new MappingJackson2HttpMessageConverter());
	}

	@RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
	public ListenableFuture<?> requestData() {
		AsyncRestTemplate httpClient = new AsyncRestTemplate();
		AsyncDatabaseClient databaseClient = new FakeAsyncDatabaseClient();

		CompletionStage<String> completionStage = AsyncAdapters.toCompletion(httpClient.execute(
			"http://localhost:8080/hello",
			HttpMethod.GET,
			null,
			new HttpMessageConverterExtractor<>(String.class, messageConverters)
		));

		return AsyncAdapters.toListenable(databaseClient.store(completionStage));
	}
}                                                                  
