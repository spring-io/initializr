package io.spring.initializr

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * The metadata using by the initializr, that is:
 *
 * <ul>
 * <li>Known dependencies gathered in group</li>
 * <li>The build types supported by the service</li>
 * <li>Supported Java versions</li>
 * <li>Supported language</li>
 * <li>Supported Spring Boot versions</li>
 * </ul>
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
@ConfigurationProperties(prefix = 'initializr', ignoreUnknownFields = false)
class InitializrMetadata {

	final List<DependencyGroup> dependencies = new ArrayList<DependencyGroup>()

	final List<Type> types = new ArrayList<Type>()

	final List<Packaging> packagings = new ArrayList<Packaging>()

	final List<JavaVersion> javaVersions = new ArrayList<JavaVersion>()

	final List<Language> languages = new ArrayList<Language>()

	final List<BootVersion> bootVersions = new ArrayList<BootVersion>()

	/**
	 * Initializes a {@link ProjectRequest} instance with the defaults
	 * defined in this instance.
	 */
	void initializeProjectRequest(ProjectRequest request) {
		request.bootVersion = getDefault(bootVersions, request.bootVersion)
		request
	}

	static def getDefault(List elements, String defaultValue) {
		for (DefaultIdentifiableElement element : elements) {
			if (element.default) {
				return element.id
			}
		}
		return defaultValue
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class DependencyGroup {

		String name

		final List<Map<String,Object>> starters= new ArrayList<>()

	}

	static class Type extends DefaultIdentifiableElement {

		String action
	}

	static class Packaging extends DefaultIdentifiableElement {
	}

	static class JavaVersion extends DefaultIdentifiableElement {
	}

	static class Language extends DefaultIdentifiableElement {
	}

	static class BootVersion extends DefaultIdentifiableElement {
	}

	static class DefaultIdentifiableElement extends IdentifiableElement {

		@JsonIgnore
		private boolean defaultValue

		void setDefault(boolean defaultValue) {
			this.defaultValue = defaultValue
		}

		boolean isDefault() {
			return this.defaultValue
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	static class IdentifiableElement {

		String name

		String id

		String getName() {
			(name != null ? name : id)
		}
	}
}
