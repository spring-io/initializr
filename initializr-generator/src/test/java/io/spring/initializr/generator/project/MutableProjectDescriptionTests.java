/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.project;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.language.java.JavaLanguage;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MutableProjectDescription}.
 *
 * @author Stephane Nicoll
 */
class MutableProjectDescriptionTests {

	@Test
	void removeDependencyWithExistingDependencyReturnsDependency() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("core", mock(Dependency.class));
		Dependency testDependency = mock(Dependency.class);
		description.addDependency("test", testDependency);
		assertThat(description.removeDependency("test")).isSameAs(testDependency);
		assertThat(description.getRequestedDependencies()).containsOnlyKeys("core");
	}

	@Test
	void removeDependencyWithUnknownDependencyReturnsNull() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("core", mock(Dependency.class));
		assertThat(description.removeDependency("unknown")).isNull();
		assertThat(description.getRequestedDependencies()).containsOnlyKeys("core");
	}

	@Test
	void changeJvmVersionUpdatesLanguageAndRecordsChange() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage("17"));
		JvmVersionChangeReason reason = () -> "test";
		description.changeJvmVersion("21", reason);
		assertThat(description.getLanguage()).isNotNull();
		assertThat(description.getLanguage().id()).isEqualTo(JavaLanguage.ID);
		assertThat(description.getLanguage().jvmVersion()).isEqualTo("21");
		assertThat(description.getJvmVersionChangeReasons()).containsExactly(reason);
	}

	@Test
	void changeJvmVersionWithSameVersionDoesNotRecordChange() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage("21"));
		description.changeJvmVersion("21", () -> "test");
		assertThat(description.getLanguage()).isNotNull();
		assertThat(description.getLanguage().jvmVersion()).isEqualTo("21");
		assertThat(description.getJvmVersionChangeReasons()).isEmpty();
	}

	@Test
	void changeJvmVersionWithEmptyReasonIdThrowsException() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage("17"));
		assertThatIllegalArgumentException().isThrownBy(() -> description.changeJvmVersion("21", () -> ""));
	}

	@Test
	void jvmVersionChangesAreImmutable() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage("17"));
		description.changeJvmVersion("21", () -> "test");
		JvmVersionChangeReason reason = () -> "another";
		assertThatExceptionOfType(UnsupportedOperationException.class)
			.isThrownBy(() -> description.getJvmVersionChangeReasons().add(reason));
	}

	@Test
	void recordsEachJvmVersionChangeReason() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setLanguage(new JavaLanguage("17"));
		JvmVersionChangeReason firstReason = () -> "test";
		JvmVersionChangeReason secondReason = () -> "test";
		description.changeJvmVersion("21", firstReason);
		description.changeJvmVersion("25", secondReason);
		assertThat(description.getJvmVersionChangeReasons()).containsExactly(firstReason, secondReason);
	}

}
