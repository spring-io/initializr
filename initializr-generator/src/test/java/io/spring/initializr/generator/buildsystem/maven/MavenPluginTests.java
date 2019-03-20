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
	void configurationParameterCanBeCustomized() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((configuration) -> configuration.add("enabled", "false")
				.add("skip", "true"));
		plugin.configuration((configuration) -> configuration.add("another", "test"));
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getName))
				.containsExactly("enabled", "skip", "another");
		assertThat(
				plugin.getConfiguration().getSettings().stream().map(Setting::getValue))
						.containsExactly("false", "true", "test");
	}

	@Test
	void configurationParameterCanBeAdded() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((configuration) -> configuration.add("enabled", "true"));
		plugin.configuration((configuration) -> configuration.add("skip", "false"));
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getName))
				.containsExactly("enabled", "skip");
		assertThat(
				plugin.getConfiguration().getSettings().stream().map(Setting::getValue))
						.containsExactly("true", "false");
	}

	@Test
	@SuppressWarnings("unchecked")
	void configurationParameterWithNestedValuesCanBeCustomized() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((configuration) -> configuration.configure("items",
				(items) -> items.add("item", "one")));
		plugin.configuration((configuration) -> configuration.configure("items",
				(items) -> items.add("item", "two")));
		assertThat(plugin.getConfiguration().getSettings()).hasSize(1);
		Setting setting = plugin.getConfiguration().getSettings().get(0);
		assertThat(setting.getName()).isEqualTo("items");
		assertThat(setting.getValue()).isInstanceOf(List.class);
		List<Setting> values = (List<Setting>) setting.getValue();
		assertThat(values.stream().map(Setting::getName)).containsExactly("item", "item");
		assertThat(values.stream().map(Setting::getValue)).containsExactly("one", "two");
	}

	@Test
	@SuppressWarnings("unchecked")
	void configurationParameterWithSeveralLevelOfNestedValuesCanBeCustomized() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((configuration) -> configuration.configure("items",
				(items) -> items.configure("item",
						(subItems) -> subItems.add("subItem", "one"))));
		plugin.configuration((configuration) -> configuration.configure("items",
				(items) -> items.configure("item", (subItems) -> subItems
						.add("subItem", "two").add("subItem", "three"))));
		assertThat(plugin.getConfiguration().getSettings()).hasSize(1);
		Setting setting = plugin.getConfiguration().getSettings().get(0);
		assertThat(setting.getName()).isEqualTo("items");
		assertThat(setting.getValue()).isInstanceOf(List.class);
		List<Setting> items = (List<Setting>) setting.getValue();
		assertThat(items).hasSize(1);
		Setting item = items.get(0);
		assertThat(item.getName()).isEqualTo("item");
		assertThat(item.getValue()).isInstanceOf(List.class);
		List<Setting> subItems = (List<Setting>) item.getValue();
		assertThat(subItems.stream().map(Setting::getName)).containsExactly("subItem",
				"subItem", "subItem");
		assertThat(subItems.stream().map(Setting::getValue)).containsExactly("one", "two",
				"three");
	}

	@Test
	void configurationParameterWithSingleValueCannotBeSwitchedToNestedValue() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.configuration((configuration) -> configuration.add("test", "value"));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> plugin.configuration((customizer) -> customizer
						.configure("test", (test) -> test.add("one", "true"))))
				.withMessageContaining("test").withMessageContaining("value");
	}

	@Test
	void executionPhasesCanBeOverridden() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.execution("test", (test) -> test.phase("compile"));
		plugin.execution("test", (test) -> test.phase("process-resources"));
		assertThat(plugin.getExecutions()).hasSize(1);
		assertThat(plugin.getExecutions().get(0).getPhase())
				.isEqualTo("process-resources");
	}

	@Test
	void executionGoalsCanBeAmended() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.execution("test", (test) -> test.goal("first"));
		plugin.execution("test", (test) -> test.goal("second"));
		assertThat(plugin.getExecutions()).hasSize(1);
		assertThat(plugin.getExecutions().get(0).getGoals()).containsExactly("first",
				"second");
	}

	@Test
	void executionConfigurationCanBeOverridden() {
		MavenPlugin plugin = new MavenPlugin("com.example", "test-plugin");
		plugin.execution("test", (test) -> test.configuration(
				(testConfiguration) -> testConfiguration.add("enabled", "true")));
		plugin.execution("test", (test) -> test.configuration(
				(testConfiguration) -> testConfiguration.add("another", "test")));
		assertThat(plugin.getExecutions()).hasSize(1);
		List<Setting> settings = plugin.getExecutions().get(0).getConfiguration()
				.getSettings();
		assertThat(settings.stream().map(Setting::getName)).containsExactly("enabled",
				"another");
		assertThat(settings.stream().map(Setting::getValue)).containsExactly("true",
				"test");
	}

}
