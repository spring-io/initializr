/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.metrics.export.Exporter
import org.springframework.boot.actuate.metrics.export.MetricCopyExporter
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository
import org.springframework.boot.actuate.metrics.repository.MetricRepository
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository
import org.springframework.boot.actuate.metrics.writer.MetricWriter
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.redis.RedisAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.util.ObjectUtils

/**
 * @author Dave Syer
 *
 */
@Configuration
@ConditionalOnBean(RedisConnectionFactory)
@ConditionalOnProperty(value='spring.metrics.export.enabled', matchIfMissing=true)
@EnableScheduling
@EnableConfigurationProperties(MetricsProperties)
@AutoConfigureAfter(value=RedisAutoConfiguration, name="org.springframework.boot.actuate.autoconfigure.MetricExportAutoConfiguration")
class InitializrMetricsExporterAutoConfiguration {

	@Autowired
	RedisConnectionFactory connectionFactory

	@Autowired
	MetricsProperties metrics

	@Autowired
	ApplicationContext context
	
	@Bean
	// @ExportMetricWriter // Add this when upgrading to Boot 1.3
	MetricWriter writer() {
		new RedisMetricRepository(connectionFactory,
				metrics.prefix + metrics.getId(context.getId()) + '.'
				+ ObjectUtils.getIdentityHexString(context) + '.',
				metrics.key)
	}

	// Remove this when upgrading to Boot 1.3
	@Bean
	@ConditionalOnMissingClass(name='org.springframework.boot.actuate.autoconfigure.ActuatorMetricWriter')
	@Primary
	MetricRepository reader() {
		new InMemoryMetricRepository()
	}

	// Remove this when upgrading to Boot 1.3
	@Bean
	@ConditionalOnMissingClass(name='org.springframework.boot.actuate.autoconfigure.ActuatorMetricWriter')
	Exporter exporter(InMemoryMetricRepository reader) {
		new MetricCopyExporter(reader, writer()) {
					@Override
					@Scheduled(fixedRateString = '${spring.metrics.export.default.delayMillis:5000}')
					void export() {
						super.export()
					}
				}
	}

}
