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
import java.util.List;
import java.util.function.Consumer;

/**
 * a profile of a {@link MavenBuild}.
 *
 * @author Joshua Xu
 **/
public class MavenProfile {

	private String id;

	private boolean activeByDefault;

	private List<Property> properties;

	public MavenProfile(Builder builder) {
		this.id = builder.id;
		this.activeByDefault = builder.activeByDefault;
		this.properties = builder.getPropertiesBuilder().getProperties();
	}

	public String getId() {
		return this.id;
	}

	public boolean isActiveByDefault() {
		return this.activeByDefault;
	}

	public List<Property> getProperties() {
		return this.properties;
	}

	public static class Builder {

		private String id;

		private boolean activeByDefault;

		private PropertiesBuilder propertiesBuilder = new PropertiesBuilder();

		public Builder(String id, boolean activateByDefault) {
			this.id = id;
			this.activeByDefault = activateByDefault;
		}

		public MavenProfile build() {
			return new MavenProfile(this);
		}

		public Builder cofiguration(Consumer<PropertiesBuilder> propertiesBuilder) {
			propertiesBuilder.accept(this.propertiesBuilder);
			return this;
		}

		public PropertiesBuilder getPropertiesBuilder() {
			return this.propertiesBuilder;
		}

		public boolean activeByDefault() {
			return this.activeByDefault;
		}

		public static class PropertiesBuilder {

			private List<Property> properties;

			public void add(String propName, String propValue) {
				if (this.properties == null) {
					this.properties = new ArrayList<>();
				}
				this.properties.add(new Property(propName, propValue));
			}

			public List<Property> getProperties() {
				return this.properties;
			}

		}

	}

	public static class Property {

		private String name;

		private String value;

		public Property(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Property property = (Property) o;
			if ((this.name != null) ? (!this.name.equals(property.name)) : (property.name != null)) {
				return false;
			}
			return (this.value != null) ? (this.value.equals(property.value)) : (property.value == null);
		}

		@Override
		public int hashCode() {
			int result = ((this.name != null) ? this.name.hashCode() : 0);
			result = 31 * result + ((this.value != null) ? this.value.hashCode() : 0);
			return result;
		}

	}

}
