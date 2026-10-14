/*
 * Copyright 2012 - present the original author or authors.
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
		.withCoordinates("org.springframework.boot", "spring-boot-a")
		.build();

	private static final Dependency SPRING_BOOT_B = Dependency
		.withCoordinates("org.springframework.boot", "spring-boot-b")
		.build();

	private static final Dependency LIB_ALPHA = Dependency.withCoordinates("com.example.alpha", "test").build();

	private static final Dependency LIB_BETA = Dependency.withCoordinates("com.example.beta", "test").build();

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
		assertThat(this.comparator.compare(LIB_BETA, Dependency.withCoordinates("com.example.beta", "a").build()))
			.isPositive();
	}

}
