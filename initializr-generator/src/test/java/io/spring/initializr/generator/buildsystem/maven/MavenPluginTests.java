/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.buildsystem.maven;

import java.util.List;

import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Setting;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link MavenPlugin}.
 *
 * @author Stephane Nicoll
 */
class MavenPluginTests {

	@Test
	void configurationCanBeCustomized() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((customizer) -> customizer.parameter("enabled", "false")
				.parameter("skip", "true"));
		plugin.configuration((customizer) -> customizer.parameter("another", "test"));
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getName))
				.containsExactly("enabled", "skip", "another");
		assertThat(
				plugin.getConfiguration().getSettings().stream().map(Setting::getValue))
						.containsExactly("false", "true", "test");
	}

	@Test
	void configurationCanBeOverridden() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((customizer) -> customizer.parameter("enabled", "true"));
		plugin.configuration((customizer) -> customizer.parameter("enabled", "false"));
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getName))
				.containsExactly("enabled");
		assertThat(
				plugin.getConfiguration().getSettings().stream().map(Setting::getValue))
						.containsExactly("false");
	}

	@Test
	@SuppressWarnings("unchecked")
	void configurationWithNestedValuesCanBeCustomized() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((customizer) -> customizer.parameter("items",
				(items) -> items.parameter("one", "true")));
		plugin.configuration((customizer) -> customizer.parameter("items",
				(items) -> items.parameter("two", "false")));
		assertThat(plugin.getConfiguration().getSettings()).hasSize(1);
		Setting setting = plugin.getConfiguration().getSettings().get(0);
		assertThat(setting.getName()).isEqualTo("items");
		assertThat(setting.getValue()).isInstanceOf(List.class);
		List<Setting> values = (List<Setting>) setting.getValue();
		assertThat(values.stream().map(Setting::getName)).containsExactly("one", "two");
		assertThat(values.stream().map(Setting::getValue)).containsExactly("true",
				"false");
	}

	@Test
	@SuppressWarnings("unchecked")
	void configurationWithSeveralLevelOfNestedValuesCanBeCustomized() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((customizer) -> customizer.parameter("items",
				(items) -> items.parameter("subItems",
						(subItems) -> subItems.parameter("one", "true"))));
		plugin.configuration((customizer) -> customizer.parameter("items",
				(items) -> items.parameter("subItems", (subItems) -> subItems
						.parameter("one", "false").parameter("two", "true"))));
		assertThat(plugin.getConfiguration().getSettings()).hasSize(1);
		Setting setting = plugin.getConfiguration().getSettings().get(0);
		assertThat(setting.getName()).isEqualTo("items");
		assertThat(setting.getValue()).isInstanceOf(List.class);
		List<Setting> items = (List<Setting>) setting.getValue();
		assertThat(items).hasSize(1);
		Setting item = items.get(0);
		assertThat(item.getName()).isEqualTo("subItems");
		assertThat(item.getValue()).isInstanceOf(List.class);
		List<Setting> subItems = (List<Setting>) item.getValue();
		assertThat(subItems.stream().map(Setting::getName)).containsExactly("one", "two");
		assertThat(subItems.stream().map(Setting::getValue)).containsExactly("false",
				"true");
	}

	@Test
	void configurationCannotBeSwitchedToNestedValue() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((customizer) -> customizer.parameter("test", "value"));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> plugin.configuration((customizer) -> customizer
						.parameter("test", (test) -> test.parameter("one", "true"))))
				.withMessageContaining("test").withMessageContaining("value");
	}

}
