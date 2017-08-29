/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import io.spring.initializr.util.VersionProperty;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.BeanWrapperImpl;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ProjectRequestResolver}.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestResolverTests {

	private InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("test", "web", "security", "data-jpa")
			.build();

	final List<ProjectRequestPostProcessor> postProcessors = new ArrayList<>();
	final GenericProjectRequestPostProcessor processor =
			new GenericProjectRequestPostProcessor();

	@Before
	public void setup() {
		this.postProcessors.add(processor);
	}

	@Test
	public void beforeResolution() {
		processor.before.put("javaVersion", "1.2");
		ProjectRequest request = resolve(createMavenProjectRequest(), postProcessors);
		assertEquals("1.2", request.getJavaVersion());
		assertEquals("1.2", request.getBuildProperties().getVersions()
				.get(new VersionProperty("java.version")).get());
	}

	@Test
	public void afterResolution() {
		postProcessors.add(new ProjectRequestPostProcessor() {
			@Override
			public void postProcessAfterResolution(ProjectRequest request,
					InitializrMetadata metadata) {
				request.getBuildProperties().getMaven().clear();
				request.getBuildProperties().getMaven().put("foo", () -> "bar");
			}
		});
		ProjectRequest request = resolve(createMavenProjectRequest(), postProcessors);
		assertEquals(1, request.getBuildProperties().getMaven().size());
		assertEquals("bar", request.getBuildProperties().getMaven().get("foo").get());
	}

	ProjectRequest resolve(ProjectRequest request,
			List<ProjectRequestPostProcessor> processors) {
		return new ProjectRequestResolver(processors).resolve(request, metadata);
	}

	ProjectRequest createMavenProjectRequest(String... styles) {
		ProjectRequest request = createProjectRequest(styles);
		request.setType("maven-project");
		return request;
	}

	ProjectRequest createProjectRequest(String... styles) {
		ProjectRequest request = new ProjectRequest();
		request.initialize(metadata);
		request.getStyle().addAll(Arrays.asList(styles));
		return request;
	}

	static class GenericProjectRequestPostProcessor
			implements ProjectRequestPostProcessor {

		final Map<String, Object> before = new LinkedHashMap<>();
		final Map<String, Object> after = new LinkedHashMap<>();

		@Override
		public void postProcessBeforeResolution(ProjectRequest request,
				InitializrMetadata metadata) {
			BeanWrapperImpl wrapper = new BeanWrapperImpl(request);
			before.forEach(wrapper::setPropertyValue);
		}

		@Override
		public void postProcessAfterResolution(ProjectRequest request,
				InitializrMetadata metadata) {
			BeanWrapperImpl wrapper = new BeanWrapperImpl(request);
			after.forEach(wrapper::setPropertyValue);
		}

	}

}
