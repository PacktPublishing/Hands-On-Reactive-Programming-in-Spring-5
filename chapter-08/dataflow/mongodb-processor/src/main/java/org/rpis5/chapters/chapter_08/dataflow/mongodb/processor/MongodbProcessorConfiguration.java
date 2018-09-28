/*
 * Copyright 2017 the original author or authors.
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

import reactor.core.publisher.Flux;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.Assert;

/**
 * A starter configuration for MongoDB Processor applications.
 *
 * @author Oleh Dokuka
 *
 */
@EnableBinding(Processor.class)
@EnableConfigurationProperties(MongodbProcessorProperties.class)
public class MongodbProcessorConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(MongodbProcessorConfiguration.class, args);
	}

	private final MongodbProcessorProperties properties;

	private final ReactiveMongoOperations mongoTemplate;

	private final StandardEvaluationContext evaluationContext;

	public MongodbProcessorConfiguration(
		MongodbProcessorProperties properties,
		ReactiveMongoOperations mongoTemplate,
		BeanFactory beanFactory
	) {
		this.properties = properties;
		this.mongoTemplate = mongoTemplate;
		this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(beanFactory);
	}

	@StreamListener
	@Output(Processor.OUTPUT)
	public Flux<Message<?>> mongoDbProcessor(@Input(Processor.INPUT) Flux<Message<?>> input) {
		Expression collectionExpression;

		if (this.properties.getCollectionExpression() == null) {
			collectionExpression = new LiteralExpression(this.properties.getCollection());
		} else {
			collectionExpression = this.properties.getCollectionExpression();
		}

		return input.concatMap(message -> {
			String collectionName = collectionExpression.getValue(this.evaluationContext, message, String.class);
			Assert.notNull(collectionName, "'collectionNameExpression' must not evaluate to null");

			return mongoTemplate.save(message.getPayload(), collectionName)
			                    .map(p -> new GenericMessage<>(p, message.getHeaders()));
		});
	}

}
