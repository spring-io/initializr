/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.container.docker.compose;

import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link ComposeServiceContainer}.
 *
 * @author Stephane Nicoll
 * @author Eddú Meléndez
 */
class ComposeServiceContainerTests {

	@Test
	void isEmptyWithEmptyContainer() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithService() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.image("my-image"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasWithMatchingService() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.image("my-image"));
		assertThat(container.has("test")).isTrue();
	}

	@Test
	void hasWithNonMatchingName() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.image("my-image"));
		assertThat(container.has("another")).isFalse();
	}

	@Test
	void tagIsSetToLatestIfNotGiven() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.image("my-image"));
		assertThat(container.values()).singleElement().satisfies((service) -> {
			assertThat(service.getImage()).isEqualTo("my-image");
			assertThat(service.getImageTag()).isEqualTo("latest");
		});
	}

	@Test
	void tagIsSetToLatestIfNotGivenInImageTag() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image"));
		assertThat(container.values()).singleElement().satisfies((service) -> {
			assertThat(service.getImage()).isEqualTo("my-image");
			assertThat(service.getImageTag()).isEqualTo("latest");
		});
	}

	@Test
	void tagIsSetToGivenInImageTag() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image:1.2.3"));
		assertThat(container.values()).singleElement().satisfies((service) -> {
			assertThat(service.getImage()).isEqualTo("my-image");
			assertThat(service.getImageTag()).isEqualTo("1.2.3");
		});
	}

	@Test
	void portsAreSorted() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image").ports(8080));
		container.add("test", (service) -> service.ports(7070));
		assertThat(container.values()).singleElement()
			.satisfies((service) -> assertThat(service.getPorts()).containsExactly(7070, 8080));
	}

	@Test
	void environmentKeysAreSorted() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image").environment("z", "zz"));
		container.add("test", (service) -> service.environment("a", "aa"));
		assertThat(container.values()).singleElement()
			.satisfies((service) -> assertThat(service.getEnvironment()).containsExactly(entry("a", "aa"),
					entry("z", "zz")));
	}

	@Test
	void environmentIsMerged() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image").environment(Map.of("a", "aa", "z", "zz")));
		container.add("test", (service) -> service.environment(Map.of("a", "aaa", "b", "bb")));
		assertThat(container.values()).singleElement()
			.satisfies((service) -> assertThat(service.getEnvironment()).containsExactly(entry("a", "aaa"),
					entry("b", "bb"), entry("z", "zz")));
	}

	@Test
	void customizeService() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> {
			service.image("my-image");
			service.imageTag("my-image-tag");
			service.imageWebsite("https://example.com/my-image");
			service.environment("param", "value");
			service.ports(8080);
			service.command("run");
			service.label("foo", "bar");
		});
		assertThat(container.values()).singleElement().satisfies((service) -> {
			assertThat(service.getName()).isEqualTo("test");
			assertThat(service.getImage()).isEqualTo("my-image");
			assertThat(service.getImageTag()).isEqualTo("my-image-tag");
			assertThat(service.getImageWebsite()).isEqualTo("https://example.com/my-image");
			assertThat(service.getEnvironment()).containsOnly(entry("param", "value"));
			assertThat(service.getPorts()).containsOnly(8080);
			assertThat(service.getCommand()).isEqualTo("run");
			assertThat(service.getLabels()).containsOnly(entry("foo", "bar"));
		});
	}

	@Test
	void customizeTaskSeveralTimeReuseConfiguration() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> {
			service.image("my-image");
			service.imageTag("my-image-tag");
			service.imageWebsite("https://example.com/my-image");
			service.environment("param", "value");
			service.ports(7070);
			service.command("run");
		});
		container.add("test", (service) -> {
			service.image("my-image");
			service.imageTag("my-image-tag");
			service.imageWebsite("https://example.com/my-image");
			service.environment("param", "value2");
			service.ports(8080);
			service.command("run2");
		});
		assertThat(container.values()).singleElement().satisfies((service) -> {
			assertThat(service.getName()).isEqualTo("test");
			assertThat(service.getImage()).isEqualTo("my-image");
			assertThat(service.getImageTag()).isEqualTo("my-image-tag");
			assertThat(service.getImageWebsite()).isEqualTo("https://example.com/my-image");
			assertThat(service.getEnvironment()).containsOnly(entry("param", "value2"));
			assertThat(service.getPorts()).containsOnly(7070, 8080);
			assertThat(service.getCommand()).isEqualTo("run2");
		});
	}

	@Test
	void removeWithMatchingService() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.image("my-image"));
		assertThat(container.remove("test")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingName() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.image("my-image"));
		assertThat(container.remove("another")).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void labelKeysAreSorted() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image").label("z", "zz"));
		container.add("test", (service) -> service.label("a", "aa"));
		assertThat(container.values()).singleElement()
			.satisfies(
					(service) -> assertThat(service.getLabels()).containsExactly(entry("a", "aa"), entry("z", "zz")));
	}

	@Test
	void labelIsMerged() {
		ComposeServiceContainer container = new ComposeServiceContainer();
		container.add("test", (service) -> service.imageAndTag("my-image").labels(Map.of("a", "aa", "z", "zz")));
		container.add("test", (service) -> service.labels(Map.of("a", "aaa", "b", "bb")));
		assertThat(container.values()).singleElement()
			.satisfies((service) -> assertThat(service.getLabels()).containsExactly(entry("a", "aaa"), entry("b", "bb"),
					entry("z", "zz")));
	}

}
