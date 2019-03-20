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

package io.spring.initializr.generator.io;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link SimpleIndentStrategy}.
 *
 * @author Stephane Nicoll
 */
class SimpleIndentStrategyTests {

	@Test
	void noLevelIsAllowed() {
		assertThat(new SimpleIndentStrategy("  ").apply(0)).isEqualTo("");
	}

	@Test
	void singleLevelIndentSpace() {
		assertThat(new SimpleIndentStrategy("  ").apply(1)).isEqualTo("  ");
	}

	@Test
	void singleLevelIndentTab() {
		assertThat(new SimpleIndentStrategy("\t").apply(1)).isEqualTo("\t");
	}

	@Test
	void multiLevelIndentSpace() {
		assertThat(new SimpleIndentStrategy("  ").apply(3)).isEqualTo("      ");
	}

	@Test
	void multiLevelIndentTab() {
		assertThat(new SimpleIndentStrategy("\t").apply(3)).isEqualTo("\t\t\t");
	}

	@Test
	void mustHaveIndent() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new SimpleIndentStrategy(null));
	}

	@Test
	void indentLevelMustNotBeNegative() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> new SimpleIndentStrategy(" ").apply(-1))
				.withMessageContaining("Indent level must not be negative")
				.withMessageContaining("-1");
	}

}
