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

import io.spring.initializr.actuate.metric.ProjectGenerationMetricsListener

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter
import org.springframework.boot.actuate.autoconfigure.MetricExportAutoConfiguration
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository
import org.springframework.boot.actuate.metrics.writer.MetricWriter
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.util.ObjectUtils

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to handle the metrics of an initializr instance.
 *
 * @author Dave Syer
 * @since 1.0
 */
@Configuration
@AutoConfigureAfter([RedisAutoConfiguration, MetricExportAutoConfiguration])
class InitializrMetricsConfiguration {

	@Bean
	ProjectGenerationMetricsListener metricsListener(CounterService counterService) {
		new ProjectGenerationMetricsListener(counterService)
	}

	@ConditionalOnBean(RedisConnectionFactory)
	@ConditionalOnProperty(value = 'spring.metrics.export.enabled')
	@EnableScheduling
	@EnableConfigurationProperties(MetricsProperties)
	@Configuration
	public static class MetricsExportConfiguration {

		@Autowired
		RedisConnectionFactory connectionFactory

		@Autowired
		MetricsProperties metrics

		@Autowired
		ApplicationContext context

		@Bean
		@ExportMetricWriter
		MetricWriter writer() {
			new RedisMetricRepository(connectionFactory,
					metrics.prefix + metrics.getId(context.getId()) + '.'
							+ ObjectUtils.getIdentityHexString(context) + '.',
					metrics.key)
		}
	}


}
