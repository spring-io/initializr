/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.actuate.autoconfigure;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.actuate.stat.ProjectGenerationStatPublisher;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.autoconfigure.InitializrAutoConfiguration;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import io.spring.initializr.web.project.WebProjectRequest;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.boot.restclient.autoconfigure.RestClientAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link InitializrStatsAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
class InitializrStatsAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, InitializrAutoConfiguration.class,
				RestClientAutoConfiguration.class, InitializrStatsAutoConfiguration.class));

	@Test
	void autoConfigRegistersProjectGenerationStatPublisher() {
		this.contextRunner.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> assertThat(context).hasSingleBean(ProjectGenerationStatPublisher.class));
	}

	@Test
	void autoConfigRegistersRetryTemplate() {
		this.contextRunner.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> assertThat(context).hasSingleBean(RetryTemplate.class));
	}

	@Test
	void statsRetryTemplateConditionalOnMissingBean() {
		this.contextRunner.withUserConfiguration(CustomStatsRetryTemplateConfiguration.class)
			.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> {
				assertThat(context).hasSingleBean(RetryTemplate.class);
				RetryTemplate retryTemplate = context.getBean(RetryTemplate.class);
				RetryPolicy retryPolicy = retryTemplate.getRetryPolicy();
				BackOff backOff = retryPolicy.getBackOff();
				assertThat(backOff).isInstanceOf(ExponentialBackOff.class);
				ExponentialBackOff exponentialBackOff = (ExponentialBackOff) backOff;
				assertThat(exponentialBackOff.getMultiplier()).isEqualTo(10);
			});
	}

	@Test
	void customRestClientBuilderIsUsed() {
		this.contextRunner.withUserConfiguration(CustomRestClientConfiguration.class)
			.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> {
				InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults().build();
				WebProjectRequest request = new WebProjectRequest();
				request.initialize(metadata);
				context.getBean(ProjectGenerationStatPublisher.class)
					.handleEvent(new ProjectGeneratedEvent(request, metadata));
				assertThat(context.getBean(RecordingInterceptor.class).uris)
					.containsExactly(URI.create("http://localhost:9200/initializr/_doc/"));
			});
	}

	@Test
	void shouldBackOffIfElasticUriIsNotSet() {
		this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(ProjectGenerationStatPublisher.class));
	}

	@Test
	void shouldBackOffIfElasticUriIsEmpty() {
		this.contextRunner.withPropertyValues("initializr.stats.elastic.uri=")
			.run((context) -> assertThat(context).doesNotHaveBean(ProjectGenerationStatPublisher.class));
	}

	@Configuration
	static class CustomStatsRetryTemplateConfiguration {

		@Bean
		RetryTemplate statsRetryTemplate() {
			RetryTemplate retryTemplate = new RetryTemplate();
			retryTemplate.setRetryPolicy(RetryPolicy.builder()
				.backOff(new ExponentialBackOff(ExponentialBackOff.DEFAULT_INITIAL_INTERVAL, 10))
				.build());
			return retryTemplate;
		}

	}

	@Configuration
	static class InfrastructureConfiguration {

		@Bean
		InitializrMetadataProvider initializrMetadataProvider() {
			return mock(InitializrMetadataProvider.class);
		}

	}

	@Configuration
	@Import(InfrastructureConfiguration.class)
	static class CustomRestClientConfiguration {

		@Bean
		RecordingInterceptor recordingInterceptor() {
			return new RecordingInterceptor();
		}

		@Bean
		RestClientCustomizer testRestClientCustomizer(RecordingInterceptor interceptor) {
			return (builder) -> builder.requestInterceptor(interceptor);
		}

	}

	/**
	 * Records request URIs and short-circuits with {@code 201 Created}, so no Elastic
	 * instance is needed.
	 */
	static class RecordingInterceptor implements ClientHttpRequestInterceptor {

		private final List<URI> uris = new ArrayList<>();

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
			this.uris.add(request.getURI());
			return new MockClientHttpResponse(new byte[0], HttpStatus.CREATED);
		}

	}

}
