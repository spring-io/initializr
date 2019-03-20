/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.io;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A factory for {@link IndentingWriter} that provides customizations according to the
 * chosen content.
 *
 * @author Stephane Nicoll
 * @see SimpleIndentStrategy
 */
public final class IndentingWriterFactory {

	private final Function<Integer, String> defaultIndentingStrategy;

	private final Map<String, Function<Integer, String>> indentingStrategies;

	private IndentingWriterFactory(Builder builder) {
		this.defaultIndentingStrategy = builder.defaultIndentingStrategy;
		this.indentingStrategies = new HashMap<>(builder.indentingStrategies);
	}

	/**
	 * Create an {@link IndentingWriter} for the specified content and output.
	 * @param contentId the identifier of the content
	 * @param out the output to use
	 * @return a configured {@link IndentingWriter}
	 */
	public IndentingWriter createIndentingWriter(String contentId, Writer out) {
		Function<Integer, String> indentingStrategy = this.indentingStrategies
				.getOrDefault(contentId, this.defaultIndentingStrategy);
		return new IndentingWriter(out, indentingStrategy);
	}

	/**
	 * Create an {@link IndentingWriterFactory} with default settings.
	 * @return an {@link IndentingWriterFactory} with default settings
	 */
	public static IndentingWriterFactory withDefaultSettings() {
		return create(new SimpleIndentStrategy("    "));
	}

	/**
	 * Create a {@link IndentingWriterFactory} with a single indenting strategy.
	 * @param defaultIndentingStrategy the default indenting strategy to use
	 * @return an {@link IndentingWriterFactory}
	 */
	public static IndentingWriterFactory create(
			Function<Integer, String> defaultIndentingStrategy) {
		return new IndentingWriterFactory(new Builder(defaultIndentingStrategy));
	}

	/**
	 * Create a {@link IndentingWriterFactory}.
	 * @param defaultIndentingStrategy the default indenting strategy to use
	 * @param factory a consumer of the builder to apply further customizations
	 * @return an {@link IndentingWriterFactory}
	 */
	public static IndentingWriterFactory create(
			Function<Integer, String> defaultIndentingStrategy,
			Consumer<Builder> factory) {
		Builder factoryBuilder = new Builder(defaultIndentingStrategy);
		factory.accept(factoryBuilder);
		return new IndentingWriterFactory(factoryBuilder);
	}

	/**
	 * Settings customizer for {@link IndentingWriterFactory}.
	 */
	public static final class Builder {

		private final Function<Integer, String> defaultIndentingStrategy;

		private final Map<String, Function<Integer, String>> indentingStrategies = new HashMap<>();

		private Builder(Function<Integer, String> defaultIndentingStrategy) {
			this.defaultIndentingStrategy = defaultIndentingStrategy;
		}

		/**
		 * Register an indenting strategy for the specified content.
		 * @param contentId the identifier of the content to configure
		 * @param indentingStrategy the indent strategy for that particular content
		 * @return this builder
		 * @see #createIndentingWriter(String, Writer)
		 */
		public Builder indentingStrategy(String contentId,
				Function<Integer, String> indentingStrategy) {
			this.indentingStrategies.put(contentId, indentingStrategy);
			return this;
		}

	}

}
