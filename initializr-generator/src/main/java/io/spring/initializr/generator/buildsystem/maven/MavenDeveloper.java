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

package io.spring.initializr.generator.buildsystem.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@code <developer>} in a Maven pom.
 *
 * @author Jafer Khan Shamshad
 */
public class MavenDeveloper {

	private final String id;

	private final String name;

	private final String email;

	private final String url;

	private final String organization;

	private final String organizationUrl;

	private final List<String> roles;

	private final String timezone;

	private final Map<String, String> properties;

	MavenDeveloper(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.email = builder.email;
		this.url = builder.url;
		this.organization = builder.organization;
		this.organizationUrl = builder.organizationUrl;
		this.roles = Collections.unmodifiableList(new ArrayList<>(builder.roles));
		this.timezone = builder.timezone;
		this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(builder.properties));
	}

	/**
	 * Return the ID of the developer.
	 * @return the ID
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return the name of the developer.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the email address of the developer.
	 * @return the email address
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Return the URL of the developer.
	 * @return the URL
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Return the organization's name of the developer.
	 * @return the organization
	 */
	public String getOrganization() {
		return this.organization;
	}

	/**
	 * Return the associated organization's URL of the developer.
	 * @return the organization's URL
	 */
	public String getOrganizationUrl() {
		return this.organizationUrl;
	}

	/**
	 * Return the roles of the developer.
	 * @return the roles
	 */
	public List<String> getRoles() {
		return this.roles;
	}

	/**
	 * Return the timezone associated with the developer.
	 * @return the timezone
	 */
	public String getTimezone() {
		return this.timezone;
	}

	/**
	 * Return other properties associated with the developer.
	 * @return other properties
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Builder for a {@link MavenDeveloper}.
	 */
	public static class Builder {

		private String id;

		private String name;

		private String email;

		private String url;

		private String organization;

		private String organizationUrl;

		private final List<String> roles = new ArrayList<>();

		private String timezone;

		private final Map<String, String> properties = new LinkedHashMap<>();

		/**
		 * Set the ID of the developer.
		 * @param id the ID of the developer or {@code null}
		 * @return this for method chaining
		 */
		public Builder id(String id) {
			this.id = id;
			return this;
		}

		/**
		 * Set the name of the developer.
		 * @param name the name of the developer or {@code null}
		 * @return this for method chaining
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Set the email address of the developer.
		 * @param email the email address of the developer or {@code null}
		 * @return this for method chaining
		 */
		public Builder email(String email) {
			this.email = email;
			return this;
		}

		/**
		 * Set the URL of the developer.
		 * @param url the URL of the developer or {@code null}
		 * @return this for method chaining
		 */
		public Builder url(String url) {
			this.url = url;
			return this;
		}

		/**
		 * Set the organization's name of the developer.
		 * @param organization the organization of the developer or {@code null}
		 * @return this for method chaining
		 */
		public Builder organization(String organization) {
			this.organization = organization;
			return this;
		}

		/**
		 * Set the associated organization's URL of the developer.
		 * @param organizationUrl the URL of the organization or {@code null}
		 * @return this for method chaining
		 */
		public Builder organizationUrl(String organizationUrl) {
			this.organizationUrl = organizationUrl;
			return this;
		}

		/**
		 * Add a role of the developer.
		 * @param role the role of the developer
		 * @return this for method chaining
		 */
		public Builder role(String role) {
			this.roles.add(role);
			return this;
		}

		/**
		 * Set the timezone associated with the developer.
		 * @param timezone the timezone that the developer lives in or {@code null}
		 * @return this for method chaining
		 */
		public Builder timezone(String timezone) {
			this.timezone = timezone;
			return this;
		}

		/**
		 * Add a property associated with the developer.
		 * @param key the appropriate key associated with the property
		 * @param value the value of the property
		 * @return this for method chaining
		 */
		public Builder property(String key, String value) {
			this.properties.put(key, value);
			return this;
		}

		public MavenDeveloper build() {
			return new MavenDeveloper(this);
		}

	}

}
