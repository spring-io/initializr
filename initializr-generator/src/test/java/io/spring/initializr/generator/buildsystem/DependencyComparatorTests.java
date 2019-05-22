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

package io.spring.initializr.generator.buildsystem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DependencyComparator}.
 *
 * @author Stephane Nicoll
 */
class DependencyComparatorTests {

	private static final Dependency SPRING_BOOT_A = Dependency
			.withCoordinates("org.springframework.boot", "spring-boot-a").build();

	private static final Dependency SPRING_BOOT_B = Dependency
			.withCoordinates("org.springframework.boot", "spring-boot-b").build();

	private static final Dependency LIB_ALPHA = Dependency
			.withCoordinates("com.example.alpha", "test").build();

	private static final Dependency LIB_BETA = Dependency
			.withCoordinates("com.example.beta", "test").build();

	private final DependencyComparator comparator = new DependencyComparator();

	@Test
	void compareWithStarters() {
		assertThat(this.comparator.compare(SPRING_BOOT_A, SPRING_BOOT_B)).isNegative();
	}

	@Test
	void compareStarterToLib() {
		assertThat(this.comparator.compare(SPRING_BOOT_A, LIB_BETA)).isNegative();
	}

	@Test
	void compareLibToStarter() {
		assertThat(this.comparator.compare(LIB_ALPHA, SPRING_BOOT_A)).isPositive();
	}

	@Test
	void compareLibDifferentGroupId() {
		assertThat(this.comparator.compare(LIB_ALPHA, LIB_BETA)).isNegative();
	}

	@Test
	void compareLibSameGroupId() {
		assertThat(this.comparator.compare(LIB_BETA,
				Dependency.withCoordinates("com.example.beta", "a").build()))
						.isPositive();
	}

}
