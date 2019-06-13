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

package io.spring.initializr.generator.spring.autoconfigure;

import io.spring.initializr.generator.spring.code.kotlin.InitializrMetadataKotlinVersionResolver;
import io.spring.initializr.generator.spring.code.kotlin.KotlinVersionResolver;
import io.spring.initializr.metadata.InitializrMetadataProvider;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for generation of Spring-based
 * projects.
 *
 * @author Andy Wilkinson
 */
public class GeneratorSpringAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public KotlinVersionResolver kotlinVersionResolver(InitializrMetadataProvider metadataProvider) {
		return new InitializrMetadataKotlinVersionResolver(metadataProvider);
	}

}
