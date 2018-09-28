/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rpis5.chapters.chapter_08.dataflow.mongodb.processor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.integration.mongodb.store.MessageDocument;
import org.springframework.integration.mongodb.support.MongoDbMessageBytesConverter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.MutableMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;

/**
 * @author Oleh Dokuka
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	properties = {
		"spring.data.mongodb.port=0"
	}
)
@Import(MongodbProcessorApplicationTests.MongoTestConfig.class)
@DirtiesContext
public abstract class MongodbProcessorApplicationTests {

	@Autowired
	protected MongoTemplate mongoTemplate;

	@Autowired
	protected Processor channel;

	@Autowired
	protected MessageCollector collector;

	@Autowired
	protected MongodbProcessorProperties mongoDbSinkProperties;

	@TestPropertySource(properties = "mongodb.collection=testing")
	static public class CollectionNameTests extends MongodbProcessorApplicationTests {

		@Test
		public void test() {
			Map<String, String> data1 = Collections.singletonMap("foo", "bar");

			Map<String, String> data2 = new HashMap<>();
			data2.put("firstName", "Foo");
			data2.put("lastName", "Bar");

			this.channel.input().send(new GenericMessage<>(data1));
			this.channel.input().send(new GenericMessage<>(data2));
			this.channel.input().send(new GenericMessage<>("{\"my_data\": \"THE DATA\"}"));

			assertThat(collector.forChannel(channel.output()), receivesPayloadThat(is("{\"foo\":\"bar\"}")));
			assertThat(collector.forChannel(channel.output()), receivesPayloadThat(is("{\"firstName\":\"Foo\",\"lastName\":\"Bar\"}")));
			assertThat(collector.forChannel(channel.output()), receivesPayloadThat(is("{\"my_data\": \"THE DATA\"}")));

			List<Document> result =
					this.mongoTemplate.findAll(Document.class, mongoDbSinkProperties.getCollection());

			assertEquals(3, result.size());

			Document dbObject = result.get(0);
			assertNotNull(dbObject.get("_id"));
			assertEquals(dbObject.get("foo"), "bar");
			assertNotNull(dbObject.get("_class"));

			dbObject = result.get(1);
			assertEquals(dbObject.get("firstName"), "Foo");
			assertEquals(dbObject.get("lastName"), "Bar");

			dbObject = result.get(2);
			assertNull(dbObject.get("_class"));
			assertEquals(dbObject.get("my_data"), "THE DATA");
		}



	}

	@TestPropertySource(properties = "mongodb.collection-expression=headers.collection")
	static public class CollectionExpressionStoreMessageTests extends
	                                                          MongodbProcessorApplicationTests {

		@Test
		@SuppressWarnings("rawtypes")
		public void test() throws JsonProcessingException {
			Message<String> mutableMessage = MutableMessageBuilder.withPayload("foo")
					.setHeader("test", "1")
					.build();
			MessageDocument payload = new MessageDocument(mutableMessage);

			this.channel.input()
			            .send(MessageBuilder.withPayload(payload)
			                                .setHeader("collection", "testing2")
			                                .build());

			assertThat(collector.forChannel(channel.output()),
					receivesPayloadThat(is(new ObjectMapper().writeValueAsString(payload))));

			List<MessageDocument> result = this.mongoTemplate.findAll(MessageDocument.class, "testing2");

			assertEquals(1, result.size());
			Message<?> message = result.get(0).getMessage();
			assertEquals(mutableMessage, message);
		}

	}

	@TestConfiguration
	@EntityScan(basePackageClasses = MessageDocument.class)
	public static class MongoTestConfig {

		@Bean
		public MongoCustomConversions customConversions() {
			return new MongoCustomConversions(Collections.singletonList(new MongoDbMessageBytesConverter()));
		}

	}

}
