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

package io.spring.initializr.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * Configuration of the initializr service.
 *
 * @author Stephane Nicoll
 */
@ConfigurationProperties(prefix = "initializr")
public class InitializrProperties extends InitializrConfiguration {

	/**
	 * Dependencies, organized in groups (i.e. themes).
	 */
	@JsonIgnore
	private final List<DependencyGroup> dependencies = new ArrayList<>();

	/**
	 * Available project types.
	 */
	@JsonIgnore
	private final List<Type> types = new ArrayList<>();

	/**
	 * Available packaging types.
	 */
	@JsonIgnore
	private final List<DefaultMetadataElement> packagings = new ArrayList<>();

	/**
	 * Available java versions.
	 */
	@JsonIgnore
	private final List<DefaultMetadataElement> javaVersions = new ArrayList<>();

	/**
	 * Available programming languages.
	 */
	@JsonIgnore
	private final List<DefaultMetadataElement> languages = new ArrayList<>();

	/**
	 * Available Spring Boot versions.
	 */
	@JsonIgnore
	private final List<DefaultMetadataElement> bootVersions = new ArrayList<>();

	/**
	 * GroupId metadata.
	 */
	@JsonIgnore
	private final SimpleElement groupId = new SimpleElement("com.example");

	/**
	 * ArtifactId metadata.
	 */
	@JsonIgnore
	private final SimpleElement artifactId = new SimpleElement(null);

	/**
	 * Version metadata.
	 */
	@JsonIgnore
	private final SimpleElement version = new SimpleElement("0.0.1-SNAPSHOT");

	/**
	 * Name metadata.
	 */
	@JsonIgnore
	private final SimpleElement name = new SimpleElement("demo");

	/**
	 * Description metadata.
	 */
	@JsonIgnore
	private final SimpleElement description = new SimpleElement(
			"Demo project for Spring Boot");

	/**
	 * Package name metadata.
	 */
	@JsonIgnore
	private final SimpleElement packageName = new SimpleElement(null);

	public List<DependencyGroup> getDependencies() {
		return dependencies;
	}

	public List<Type> getTypes() {
		return types;
	}

	public List<DefaultMetadataElement> getPackagings() {
		return packagings;
	}

	public List<DefaultMetadataElement> getJavaVersions() {
		return javaVersions;
	}

	public List<DefaultMetadataElement> getLanguages() {
		return languages;
	}

	public List<DefaultMetadataElement> getBootVersions() {
		return bootVersions;
	}

	public SimpleElement getGroupId() {
		return groupId;
	}

	public SimpleElement getArtifactId() {
		return artifactId;
	}

	public SimpleElement getVersion() {
		return version;
	}

	public SimpleElement getName() {
		return name;
	}

	public SimpleElement getDescription() {
		return description;
	}

	public SimpleElement getPackageName() {
		return packageName;
	}

	public static class SimpleElement {

		/**
		 * Element title.
		 */
		private String title;

		/**
		 * Element description.
		 */
		private String description;

		/**
		 * Element default value.
		 */
		private String value;

		public SimpleElement(String value) {
			this.value = value;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public void apply(TextCapability capability) {
			if (StringUtils.hasText(title)) {
				capability.setTitle(title);
			}
			if (StringUtils.hasText(description)) {
				capability.setDescription(description);
			}
			if (StringUtils.hasText(value)) {
				capability.setContent(value);
			}
		}
	}

}
