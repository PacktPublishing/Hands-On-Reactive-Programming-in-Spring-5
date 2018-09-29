package org.rpis5.chapters.chapter_09;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.reactivestreams.Publisher;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;

@SuppressWarnings("unchecked")
public class MockClientResponse implements ClientResponse {

	public static MockClientResponse create(int statusCode, Publisher body) {
		return new MockClientResponse(HttpStatus.resolve(statusCode), null, null, body);
	}

	private final HttpStatus                            status;
	private final HttpHeaders                           headers;
	private final MultiValueMap<String, ResponseCookie> cookies;
	private final Publisher                             responseBody;

	public MockClientResponse(HttpStatus status,
			HttpHeaders headers,
			MultiValueMap<String, ResponseCookie> cookies,
			Publisher responseBody) {
		this.status = status;
		this.headers = headers;
		this.cookies = cookies;
		this.responseBody = responseBody;
	}

	@Override
	public HttpStatus statusCode() {
		return status;
	}

	@Override
	public int rawStatusCode() {
		return status.value();
	}

	@Override
	public Headers headers() {
		return new DefaultHeaders();
	}

	@Override
	public MultiValueMap<String, ResponseCookie> cookies() {
		return cookies;
	}

	@Override
	public ExchangeStrategies strategies() {
		return new ExchangeStrategies() {
			@Override
			public List<HttpMessageReader<?>> messageReaders() {
				return Collections.emptyList();
			}

			@Override
			public List<HttpMessageWriter<?>> messageWriters() {
				return Collections.emptyList();
			}
		};
	}

	@Override
	public <T> T body(BodyExtractor<T, ? super ClientHttpResponse> extractor) {
		return (T) responseBody;
	}

	@Override
	public <T> Mono<T> bodyToMono(Class<? extends T> elementClass) {
		return body(null);
	}

	@Override
	public <T> Mono<T> bodyToMono(ParameterizedTypeReference<T> typeReference) {
		return body(null);
	}

	@Override
	public <T> Flux<T> bodyToFlux(Class<? extends T> elementClass) {
		return body(null);
	}

	@Override
	public <T> Flux<T> bodyToFlux(ParameterizedTypeReference<T> typeReference) {
		return body(null);
	}

	@Override
	public <T> Mono<ResponseEntity<T>> toEntity(Class<T> bodyType) {
		return this.<Mono<T>>body(null).map(ResponseEntity.status(status)::body);
	}

	@Override
	public <T> Mono<ResponseEntity<T>> toEntity(ParameterizedTypeReference<T> typeReference) {
		return this.<Mono<T>>body(null).map(ResponseEntity.status(status)::body);
	}

	@Override
	public <T> Mono<ResponseEntity<List<T>>> toEntityList(Class<T> elementType) {
		return this.<Flux<T>>body(null).collectList()
		                               .map(ResponseEntity.status(status)::body);
	}

	@Override
	public <T> Mono<ResponseEntity<List<T>>> toEntityList(ParameterizedTypeReference<T> typeReference) {
		return this.<Flux<T>>body(null).collectList()
		                               .map(ResponseEntity.status(status)::body);
	}

	private class DefaultHeaders implements Headers {

		private HttpHeaders delegate() {
			return headers;
		}

		@Override
		public OptionalLong contentLength() {
			return toOptionalLong(delegate().getContentLength());
		}

		@Override
		public Optional<MediaType> contentType() {
			return Optional.ofNullable(delegate().getContentType());
		}

		@Override
		public List<String> header(String headerName) {
			List<String> headerValues = delegate().get(headerName);
			return headerValues != null ? headerValues : Collections.emptyList();
		}

		@Override
		public HttpHeaders asHttpHeaders() {
			return HttpHeaders.readOnlyHttpHeaders(delegate());
		}

		private OptionalLong toOptionalLong(long value) {
			return value != -1 ? OptionalLong.of(value) : OptionalLong.empty();
		}

	}
}
