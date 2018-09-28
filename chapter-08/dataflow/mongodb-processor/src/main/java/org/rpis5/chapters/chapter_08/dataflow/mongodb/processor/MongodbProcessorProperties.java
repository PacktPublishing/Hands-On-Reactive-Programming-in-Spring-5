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

import javax.validation.constraints.AssertTrue;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.Expression;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * @author Artem Bilan
 *
 */
@ConfigurationProperties("mongodb")
@Validated
public class MongodbProcessorProperties {

	/**
	 * The MongoDB collection to store data
	 */
	private String collection;

	/**
	 * The SpEL expression to evaluate MongoDB collection
	 */
	private Expression collectionExpression;

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public String getCollection() {
		return this.collection;
	}

	public void setCollectionExpression(Expression collectionExpression) {
		this.collectionExpression = collectionExpression;
	}

	public Expression getCollectionExpression() {
		return collectionExpression;
	}

	@AssertTrue(message = "One of 'collection' or 'collectionExpression' is required")
	private boolean isValid() {
		return StringUtils.hasText(this.collection) || this.collectionExpression != null;
	}

}
