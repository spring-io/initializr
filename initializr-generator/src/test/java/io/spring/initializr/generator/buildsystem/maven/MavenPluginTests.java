/*
 * Copyright 2012-2020 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.maven.MavenPlugin.Builder;
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
		MavenPlugin plugin = plugin("com.example", "test-plugin")
				.configuration((configuration) -> configuration.add("enabled", "false").add("skip", "true"))
				.configuration((configuration) -> configuration.add("another", "test")).build();
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getName)).containsExactly("enabled",
				"skip", "another");
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getValue)).containsExactly("false",
				"true", "test");
	}

	@Test
	void configurationParameterCanBeAdded() {
		MavenPlugin plugin = plugin("com.example", "test-plugin")
				.configuration((configuration) -> configuration.add("enabled", "true"))
				.configuration((configuration) -> configuration.add("skip", "false")).build();
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getName)).containsExactly("enabled",
				"skip");
		assertThat(plugin.getConfiguration().getSettings().stream().map(Setting::getValue)).containsExactly("true",
				"false");
	}

	@SuppressWarnings("unchecked")
	@Test
	void configurationParameterWithNestedValuesCanBeAdded() {
		MavenPlugin plugin = plugin("com.example", "test-plugin")
				.configuration((configuration) -> configuration.configure("items", (items) -> {
					items.add("item", (firstItem) -> firstItem.add("name", "one"));
					items.add("item", (secondItem) -> secondItem.add("name", "two"));
				})).build();
		assertThat(plugin.getConfiguration().getSettings()).hasSize(1);
		Setting setting = plugin.getConfiguration().getSettings().get(0);
		assertThat(setting.getName()).isEqualTo("items");
		assertThat(setting.getValue()).isInstanceOf(List.class);
		List<Setting> values = (List<Setting>) setting.getValue();
		assertThat(values.stream().map(Setting::getName)).containsExactly("item", "item");
		assertThat(values.stream().map(Setting::getValue)).anySatisfy((value) -> {
			assertThat(value).isInstanceOf(List.class);
			List<Setting> itemValues = (List<Setting>) value;
			assertThat(itemValues.stream().map(Setting::getName)).containsExactly("name");
			assertThat(itemValues.stream().map(Setting::getValue)).containsExactly("one");
		});
		assertThat(values.stream().map(Setting::getValue)).anySatisfy((value) -> {
			assertThat(value).isInstanceOf(List.class);
			List<Setting> itemValues = (List<Setting>) value;
			assertThat(itemValues.stream().map(Setting::getName)).containsExactly("name");
			assertThat(itemValues.stream().map(Setting::getValue)).containsExactly("two");
		});
		assertThat(values).hasSize(2);
	}

	@Test
	@SuppressWarnings("unchecked")
	void configurationParameterWithNestedValuesCanBeCustomized() {
		MavenPlugin plugin = plugin("com.example", "test-plugin")
				.configuration((configuration) -> configuration.configure("items", (items) -> items.add("item", "one")))
				.configuration((configuration) -> configuration.configure("items", (items) -> items.add("item", "two")))
				.build();
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
		MavenPlugin plugin = plugin("com.example", "test-plugin")
				.configuration((configuration) -> configuration.configure("items",
						(items) -> items.configure("item", (subItems) -> subItems.add("subItem", "one"))))
				.configuration(
						(configuration) -> configuration
								.configure("items",
										(items) -> items.configure("item",
												(subItems) -> subItems.add("subItem", "two").add("subItem", "three"))))
				.build();
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
		assertThat(subItems.stream().map(Setting::getName)).containsExactly("subItem", "subItem", "subItem");
		assertThat(subItems.stream().map(Setting::getValue)).containsExactly("one", "two", "three");
	}

	@Test
	void configurationParameterWithSingleValueCannotBeSwitchedToNestedValue() {
		MavenPlugin.Builder builder = plugin("com.example", "test-plugin")
				.configuration((configuration) -> configuration.add("test", "value"));
		assertThatIllegalArgumentException()
				.isThrownBy(() -> builder
						.configuration((customizer) -> customizer.configure("test", (test) -> test.add("one", "true"))))
				.withMessageContaining("test").withMessageContaining("value");
	}

	@Test
	void executionPhasesCanBeOverridden() {
		MavenPlugin plugin = plugin("com.example", "test-plugin").execution("test", (test) -> test.phase("compile"))
				.execution("test", (test) -> test.phase("process-resources")).build();
		assertThat(plugin.getExecutions()).hasSize(1);
		assertThat(plugin.getExecutions().get(0).getPhase()).isEqualTo("process-resources");
	}

	@Test
	void executionGoalsCanBeAmended() {
		MavenPlugin plugin = plugin("com.example", "test-plugin").execution("test", (test) -> test.goal("first"))
				.execution("test", (test) -> test.goal("second")).build();
		assertThat(plugin.getExecutions()).hasSize(1);
		assertThat(plugin.getExecutions().get(0).getGoals()).containsExactly("first", "second");
	}

	@Test
	void executionConfigurationCanBeOverridden() {
		MavenPlugin plugin = plugin("com.example", "test-plugin")
				.execution("test",
						(test) -> test.configuration((testConfiguration) -> testConfiguration.add("enabled", "true")))
				.execution("test",
						(test) -> test.configuration((testConfiguration) -> testConfiguration.add("another", "test")))
				.build();
		assertThat(plugin.getExecutions()).hasSize(1);
		List<Setting> settings = plugin.getExecutions().get(0).getConfiguration().getSettings();
		assertThat(settings.stream().map(Setting::getName)).containsExactly("enabled", "another");
		assertThat(settings.stream().map(Setting::getValue)).containsExactly("true", "test");
	}

	private MavenPlugin.Builder plugin(String groupId, String artifactId) {
		return new Builder(groupId, artifactId);
	}

}
