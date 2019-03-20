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

package io.spring.initializr.generator.packaging;

import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link Packaging}.
 *
 * @author Stephane Nicoll
 */
class PackagingTests {

	@Test
	void jarPackaging() {
		Packaging jar = Packaging.forId("jar");
		assertThat(jar).isInstanceOf(JarPackaging.class);
		assertThat(jar.id()).isEqualTo("jar");
		assertThat(jar.toString()).isEqualTo("jar");
	}

	@Test
	void warPackaging() {
		Packaging war = Packaging.forId("war");
		assertThat(war).isInstanceOf(WarPackaging.class);
		assertThat(war.id()).isEqualTo("war");
		assertThat(war.toString()).isEqualTo("war");
	}

	@Test
	void unknownPackaging() {
		assertThatIllegalStateException().isThrownBy(() -> Packaging.forId("unknown"))
				.withMessageContaining("Unrecognized packaging id 'unknown'");
	}

}
