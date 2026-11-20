/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.actuate.stat;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.spring.initializr.actuate.stat.StatsProperties.Elastic;
import io.spring.initializr.web.project.ProjectRequestEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.core.retry.RetryException;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Publish stats for each project generated to an Elastic index.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public class ProjectGenerationStatPublisher {

	private static final Log logger = LogFactory.getLog(ProjectGenerationStatPublisher.class);

	private final ProjectRequestDocumentFactory documentFactory;

	private final JsonMapper jsonMapper;

	private final RestClient restClient;

	private URI requestUrl;

	private final RetryTemplate retryTemplate;

	/**
	 * Create a new instance using the specified {@link RestClient.Builder}.
	 * @param documentFactory the document factory
	 * @param statsProperties the stats properties
	 * @param restClientBuilder the rest client builder
	 * @param retryTemplate the retry template
	 */
	public ProjectGenerationStatPublisher(ProjectRequestDocumentFactory documentFactory,
			StatsProperties statsProperties, RestClient.Builder restClientBuilder, RetryTemplate retryTemplate) {
		this.documentFactory = documentFactory;
		this.jsonMapper = createJsonMapper();
		StatsProperties.Elastic elastic = statsProperties.getElastic();
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(determineEntityUrl(elastic));
		this.restClient = configureAuthorization(restClientBuilder.clone(), elastic, uriBuilder).build();
		this.requestUrl = uriBuilder.userInfo(null).build().toUri();
		this.retryTemplate = retryTemplate;
	}

	/**
	 * Create a new instance using the specified {@link RestTemplateBuilder}.
	 * @param documentFactory the document factory
	 * @param statsProperties the stats properties
	 * @param restTemplateBuilder the rest template builder
	 * @param retryTemplate the retry template
	 * @deprecated in favor of
	 * {@link #ProjectGenerationStatPublisher(ProjectRequestDocumentFactory, StatsProperties, RestClient.Builder, RetryTemplate)}
	 */
	@Deprecated(forRemoval = true)
	@SuppressWarnings("removal")
	public ProjectGenerationStatPublisher(ProjectRequestDocumentFactory documentFactory,
			StatsProperties statsProperties, RestTemplateBuilder restTemplateBuilder, RetryTemplate retryTemplate) {
		this(documentFactory, statsProperties, RestClient.builder(restTemplateBuilder.build()), retryTemplate);
	}

	/**
	 * Publish the given event to the index. Failures are logged.
	 * @param event the event
	 */
	@EventListener
	@Async
	public void handleEvent(ProjectRequestEvent event) {
		String json = null;
		try {
			ProjectRequestDocument document = this.documentFactory.createDocument(event);
			if (logger.isDebugEnabled()) {
				logger.debug("Publishing " + document);
			}
			json = toJson(document);
			publish(json);
		}
		catch (Exception ex) {
			logger.warn("Failed to publish stat to index, document follows %n%n%s%n".formatted(json), ex);
		}
	}

	private void publish(String json) throws RetryException {
		this.retryTemplate.execute(() -> {
			this.restClient.post()
				.uri(this.requestUrl)
				.contentType(MediaType.APPLICATION_JSON)
				.body(json)
				.retrieve()
				.toBodilessEntity();
			return null;
		});
	}

	private String toJson(ProjectRequestDocument stats) {
		try {
			return this.jsonMapper.writeValueAsString(stats);
		}
		catch (JacksonException ex) {
			throw new IllegalStateException("Cannot convert to JSON", ex);
		}
	}

	private static JsonMapper createJsonMapper() {
		return JsonMapper.builder()
			.changeDefaultPropertyInclusion((handler) -> handler.withValueInclusion(JsonInclude.Include.NON_NULL))
			.build();
	}

	/**
	 * Update the URL to which documents are published.
	 * @param requestUrl the URL
	 */
	protected void updateRequestUrl(URI requestUrl) {
		this.requestUrl = requestUrl;
	}

	private static RestClient.Builder configureAuthorization(RestClient.Builder restClientBuilder, Elastic elastic,
			UriComponentsBuilder uriComponentsBuilder) {
		String userInfo = uriComponentsBuilder.build().getUserInfo();
		if (StringUtils.hasText(userInfo)) {
			String[] credentials = userInfo.split(":");
			return basicAuth(restClientBuilder, credentials[0], credentials[1]);
		}
		else if (StringUtils.hasText(elastic.getUsername()) && StringUtils.hasText(elastic.getPassword())) {
			return basicAuth(restClientBuilder, elastic.getUsername(), elastic.getPassword());
		}
		return restClientBuilder;
	}

	private static RestClient.Builder basicAuth(RestClient.Builder restClientBuilder, String username,
			String password) {
		return restClientBuilder.defaultHeaders((headers) -> headers.setBasicAuth(username, password));
	}

	private static URI determineEntityUrl(Elastic elastic) {
		String entityUrl = elastic.getUri() + "/" + elastic.getIndexName() + "/_doc/";
		try {
			return new URI(entityUrl);
		}
		catch (URISyntaxException ex) {
			throw new IllegalStateException("Cannot create entity URL: " + entityUrl, ex);
		}
	}

}
