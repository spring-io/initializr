package io.spring.initializr.generator

import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * Tests for {@link ProjectRequestResolver}.
 *
 * @author Stephane Nicoll
 */
class ProjectRequestResolverTests {

	def metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup('test', 'web', 'security', 'data-jpa')
			.build()

	final List<ProjectRequestPostProcessor> postProcessors = []
	final GenericProjectRequestPostProcessor processor = new GenericProjectRequestPostProcessor()

	@Before
	void setup() {
		this.postProcessors << processor
	}

	@Test
	void beforeResolution() {
		processor.before['javaVersion'] = '1.2'
		ProjectRequest request = resolve(createMavenProjectRequest(), postProcessors)
		assertEquals '1.2', request.javaVersion
		assertEquals '1.2', request.buildProperties.versions['java.version'].call()
	}

	@Test
	void afterResolution() {
		postProcessors << new ProjectRequestPostProcessorAdapter() {
			@Override
			void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
				request.buildProperties.maven.clear()
				request.buildProperties.maven['foo'] = { 'bar' }
			}
		}
		ProjectRequest request = resolve(createMavenProjectRequest(), postProcessors)
		assertEquals 1, request.buildProperties.maven.size()
		assertEquals 'bar', request.buildProperties.maven['foo'].call()
	}

	ProjectRequest resolve(def request, def processors) {
		new ProjectRequestResolver(processors)
				.resolve(request, metadata)
	}

	ProjectRequest createMavenProjectRequest(String... styles) {
		def request = createProjectRequest(styles)
		request.type = 'maven-project'
		request
	}

	ProjectRequest createProjectRequest(String... styles) {
		def request = new ProjectRequest()
		request.initialize(metadata)
		request.style.addAll Arrays.asList(styles)
		request
	}

	static class GenericProjectRequestPostProcessor implements ProjectRequestPostProcessor {

		final Map<String, Object> before = [:]
		final Map<String, Object> after = [:]

		@Override
		void postProcessBeforeResolution(ProjectRequest request, InitializrMetadata metadata) {
			before.forEach { k, v -> request.setProperty(k, v) }
		}

		@Override
		void postProcessAfterResolution(ProjectRequest request, InitializrMetadata metadata) {
			after.forEach { k, v -> request.setProperty(k, v) }
		}

	}

}
