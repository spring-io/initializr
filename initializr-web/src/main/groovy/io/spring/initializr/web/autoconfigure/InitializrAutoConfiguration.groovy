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

import com.github.benmanes.caffeine.cache.Caffeine
import io.spring.initializr.generator.ProjectGenerator
import io.spring.initializr.generator.ProjectRequestPostProcessor
import io.spring.initializr.generator.ProjectRequestResolver
import io.spring.initializr.generator.ProjectResourceLocator
import io.spring.initializr.metadata.DependencyMetadataProvider
import io.spring.initializr.metadata.InitializrMetadataBuilder
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.metadata.InitializrProperties
import io.spring.initializr.util.GroovyTemplate
import io.spring.initializr.web.project.MainController
import io.spring.initializr.web.support.DefaultDependencyMetadataProvider
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider
import io.spring.initializr.web.ui.UiController

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.bind.RelaxedPropertyResolver
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.web.client.RestTemplate

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

	@Autowired(required = false)
	List<ProjectRequestPostProcessor> postProcessors = []

	@Bean
	WebConfig webConfig() {
		new WebConfig()
	}

	@Bean
	@ConditionalOnMissingBean
	MainController initializrMainController(InitializrMetadataProvider metadataProvider,
											GroovyTemplate groovyTemplate,
											ProjectGenerator projectGenerator,
											DependencyMetadataProvider dependencyMetadataProvider) {
		new MainController(metadataProvider, groovyTemplate, projectGenerator, dependencyMetadataProvider)
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
	@ConditionalOnMissingBean
	GroovyTemplate groovyTemplate(Environment environment) {
		def resolver = new RelaxedPropertyResolver(environment, 'spring.groovy.template.')
		boolean cache = resolver.getProperty('cache', Boolean.class, true)
		def groovyTemplate = new GroovyTemplate()
		groovyTemplate.cache = cache
		groovyTemplate
	}

	@Bean
	@ConditionalOnMissingBean
	ProjectRequestResolver projectRequestResolver() {
		new ProjectRequestResolver(postProcessors)
	}

	@Bean
	ProjectResourceLocator projectResourceLocator() {
		return new ProjectResourceLocator()
	}

	@Bean
	@ConditionalOnMissingBean(InitializrMetadataProvider)
	InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties) {
		def metadata = InitializrMetadataBuilder.fromInitializrProperties(properties).build()
		new DefaultInitializrMetadataProvider(metadata, new RestTemplate())
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

	private static Cache createConcurrentMapCache(Long timeToLive, String name) {
		new CaffeineCache(name, Caffeine
				.newBuilder()
				.expireAfterWrite(timeToLive, TimeUnit.SECONDS)
				.build())
	}

}
