/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.actuate.autoconfigure

import io.spring.initializr.actuate.stat.ProjectGenerationStatPublisher
import io.spring.initializr.actuate.stat.ProjectRequestDocumentFactory
import io.spring.initializr.actuate.stat.StatsProperties
import io.spring.initializr.metadata.InitializrMetadataProvider

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to publish statistics of each generated project.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(StatsProperties)
@ConditionalOnProperty('initializr.stats.elastic.uri')
class InitializrStatsAutoConfiguration {

	@Autowired
	private StatsProperties statsProperties

	@Bean
	ProjectGenerationStatPublisher projectRequestStatHandler(InitializrMetadataProvider provider) {
		new ProjectGenerationStatPublisher(new ProjectRequestDocumentFactory(provider),
				statsProperties, statsRetryTemplate())
	}

	@Bean
	@ConditionalOnMissingBean(name = "statsRetryTemplate")
	RetryTemplate statsRetryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate()
		def backOffPolicy = new ExponentialBackOffPolicy(initialInterval: 3000L, multiplier: 3)
		def retryPolicy = new SimpleRetryPolicy(statsProperties.elastic.maxAttempts, Collections
				.<Class<? extends Throwable>, Boolean> singletonMap(Exception.class, true))
		retryTemplate.setBackOffPolicy(backOffPolicy)
		retryTemplate.setRetryPolicy(retryPolicy)
		retryTemplate
	}

}
