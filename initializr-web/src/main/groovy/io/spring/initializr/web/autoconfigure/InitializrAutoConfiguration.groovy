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

package io.spring.initializr.web.autoconfigure

import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder
import io.spring.initializr.generator.ProjectGenerator
import io.spring.initializr.generator.ProjectResourceLocator
import io.spring.initializr.metadata.DependencyMetadataProvider
import io.spring.initializr.metadata.InitializrMetadataBuilder
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.metadata.InitializrProperties
import io.spring.initializr.web.project.MainController
import io.spring.initializr.web.support.DefaultDependencyMetadataProvider
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider
import io.spring.initializr.web.ui.UiController

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to configure Spring initializr. In a web environment,
 * configures the necessary controller to serve the applications from the
 * root context.
 *
 * <p>Project generation can be customized by defining a custom
 * {@link ProjectGenerator}.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(InitializrProperties)
class InitializrAutoConfiguration {

	@Bean
	WebConfig webConfig() {
		new WebConfig()
	}

	@Bean
	@ConditionalOnMissingBean
	MainController initializrMainController() {
		new MainController()
	}

	@Bean
	@ConditionalOnMissingBean
	UiController initializrUiController() {
		new UiController()
	}

	@Bean
	@ConditionalOnMissingBean
	ProjectGenerator projectGenerator() {
		new ProjectGenerator()
	}

	@Bean
	ProjectResourceLocator projectResourceLocator() {
		return new ProjectResourceLocator()
	}

	@Bean
	@ConditionalOnMissingBean(InitializrMetadataProvider)
	InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties) {
		def metadata = InitializrMetadataBuilder.fromInitializrProperties(properties).build()
		new DefaultInitializrMetadataProvider(metadata)
	}

	@Bean
	@ConditionalOnMissingBean
	DependencyMetadataProvider dependencyMetadataProvider() {
		new DefaultDependencyMetadataProvider()
	}

	@Bean
	@ConditionalOnMissingBean
	CacheManager cacheManager() {
		def cacheManager = new SimpleCacheManager()
		cacheManager.caches = Arrays.asList(
				createConcurrentMapCache(600, 'initializr'),
				new ConcurrentMapCache('dependency-metadata'),
				new ConcurrentMapCache("project-resources"))
		cacheManager
	}

	private static ConcurrentMapCache createConcurrentMapCache(Long timeToLive, String name) {
		def cacheBuilder = CacheBuilder.newBuilder()
				.expireAfterWrite(timeToLive, TimeUnit.SECONDS)

		def map = cacheBuilder.build().asMap()
		new ConcurrentMapCache(name, map, false)
	}

}
