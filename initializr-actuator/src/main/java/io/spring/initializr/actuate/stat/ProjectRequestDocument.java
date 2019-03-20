/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.actuate.stat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.web.support.Agent;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Define the statistics of a project generation.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestDocument {

	private long generationTimestamp;

	private String type;

	private String buildSystem;

	private String groupId;

	private String artifactId;

	private String javaVersion;

	private String language;

	private String packaging;

	private String packageName;

	private VersionInformation version;

	private ClientInformation client;

	private DependencyInformation dependencies;

	private ErrorStateInformation errorState;

	public long getGenerationTimestamp() {
		return this.generationTimestamp;
	}

	public void setGenerationTimestamp(long generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBuildSystem() {
		return this.buildSystem;
	}

	public void setBuildSystem(String buildSystem) {
		this.buildSystem = buildSystem;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getJavaVersion() {
		return this.javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPackaging() {
		return this.packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public VersionInformation getVersion() {
		return this.version;
	}

	public void setVersion(VersionInformation version) {
		this.version = version;
	}

	public ClientInformation getClient() {
		return this.client;
	}

	public void setClient(ClientInformation client) {
		this.client = client;
	}

	public DependencyInformation getDependencies() {
		return this.dependencies;
	}

	public void setDependencies(DependencyInformation dependencies) {
		this.dependencies = dependencies;
	}

	public ErrorStateInformation getErrorState() {
		return this.errorState;
	}

	public ErrorStateInformation triggerError() {
		if (this.errorState == null) {
			this.errorState = new ErrorStateInformation();
		}
		return this.errorState;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", ProjectRequestDocument.class.getSimpleName() + "[",
				"]").add("generationTimestamp=" + this.generationTimestamp)
						.add("type='" + this.type + "'")
						.add("buildSystem='" + this.buildSystem + "'")
						.add("groupId='" + this.groupId + "'")
						.add("artifactId='" + this.artifactId + "'")
						.add("javaVersion='" + this.javaVersion + "'")
						.add("language='" + this.language + "'")
						.add("packaging='" + this.packaging + "'")
						.add("packageName='" + this.packageName + "'")
						.add("version=" + this.version).add("client=" + this.client)
						.add("dependencies=" + this.dependencies)
						.add("errorState=" + this.errorState).toString();
	}

	/**
	 * Spring Boot version information.
	 */
	public static class VersionInformation {

		private final String id;

		private final String major;

		private final String minor;

		public VersionInformation(Version version) {
			this.id = version.toString();
			this.major = String.format("%s", version.getMajor());
			this.minor = (version.getMinor() != null)
					? String.format("%s.%s", version.getMajor(), version.getMinor())
					: null;
		}

		public String getId() {
			return this.id;
		}

		public String getMajor() {
			return this.major;
		}

		public String getMinor() {
			return this.minor;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "{", "}").add("id='" + this.id + "'")
					.add("major='" + this.major + "'").add("minor='" + this.minor + "'")
					.toString();
		}

	}

	/**
	 * Dependencies information.
	 */
	public static class DependencyInformation {

		private final String id;

		private final List<String> values;

		private final int count;

		public DependencyInformation(List<String> values) {
			this.id = computeDependenciesId(new ArrayList<>(values));
			this.values = values;
			this.count = values.size();
		}

		public String getId() {
			return this.id;
		}

		public List<String> getValues() {
			return this.values;
		}

		public int getCount() {
			return this.count;
		}

		private static String computeDependenciesId(List<String> dependencies) {
			if (ObjectUtils.isEmpty(dependencies)) {
				return "_none";
			}
			Collections.sort(dependencies);
			return StringUtils.collectionToDelimitedString(dependencies, " ");
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "{", "}").add("id='" + this.id + "'")
					.add("values=" + this.values).add("count=" + this.count).toString();
		}

	}

	/**
	 * Client information.
	 */
	public static class ClientInformation {

		private final String id;

		private final String version;

		private final String ip;

		private final String country;

		public ClientInformation(Agent agent, String ip, String country) {
			this.id = (agent != null) ? agent.getId().getId() : null;
			this.version = (agent != null) ? agent.getVersion() : null;
			this.ip = ip;
			this.country = country;
		}

		public String getId() {
			return this.id;
		}

		public String getVersion() {
			return this.version;
		}

		public String getIp() {
			return this.ip;
		}

		public String getCountry() {
			return this.country;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "{", "}").add("id='" + this.id + "'")
					.add("version='" + this.version + "'").add("ip='" + this.ip + "'")
					.add("country='" + this.country + "'").toString();
		}

	}

	/**
	 * Additional information about what part of the request is invalid.
	 */
	public static class ErrorStateInformation {

		private boolean invalid = true;

		private Boolean javaVersion;

		private Boolean language;

		private Boolean packaging;

		private Boolean type;

		private InvalidDependencyInformation dependencies;

		private String message;

		public boolean isInvalid() {
			return this.invalid;
		}

		public Boolean getJavaVersion() {
			return this.javaVersion;
		}

		public void setJavaVersion(Boolean javaVersion) {
			this.javaVersion = javaVersion;
		}

		public Boolean getLanguage() {
			return this.language;
		}

		public void setLanguage(Boolean language) {
			this.language = language;
		}

		public Boolean getPackaging() {
			return this.packaging;
		}

		public void setPackaging(Boolean packaging) {
			this.packaging = packaging;
		}

		public Boolean getType() {
			return this.type;
		}

		public void setType(Boolean type) {
			this.type = type;
		}

		public InvalidDependencyInformation getDependencies() {
			return this.dependencies;
		}

		public void triggerInvalidDependencies(List<String> dependencies) {
			this.dependencies = new InvalidDependencyInformation(dependencies);
		}

		public String getMessage() {
			return this.message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "{", "}").add("invalid=" + this.invalid)
					.add("javaVersion=" + this.javaVersion)
					.add("language=" + this.language).add("packaging=" + this.packaging)
					.add("type=" + this.type).add("dependencies=" + this.dependencies)
					.add("message='" + this.message + "'").toString();
		}

	}

	/**
	 * Invalid dependencies information.
	 */
	public static class InvalidDependencyInformation {

		private boolean invalid = true;

		private final List<String> values;

		public InvalidDependencyInformation(List<String> values) {
			this.values = values;
		}

		public boolean isInvalid() {
			return this.invalid;
		}

		public List<String> getValues() {
			return this.values;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", "{", "}").add(String.join(", ", this.values))
					.toString();
		}

	}

}
