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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link Build}.
 *
 * @author Stephane Nicoll
 */
class BuildTests {

	@Test
	void buildWithDefaultBuildItemResolver() {
		TestBuild build = new TestBuild(null);
		Assertions.assertThatIllegalArgumentException()
				.isThrownBy(() -> build.dependencies().add("test"))
				.withMessageContaining("No such value with id 'test'");
		Assertions.assertThatIllegalArgumentException()
				.isThrownBy(() -> build.boms().add("another"))
				.withMessageContaining("No such value with id 'another'");
		Assertions.assertThatIllegalArgumentException()
				.isThrownBy(() -> build.repositories().add("repo"))
				.withMessageContaining("No such value with id 'repo'");
	}

	@Test
	void buildWithCustomBuildItemResolverResolveDependency() {
		BuildItemResolver resolver = mock(BuildItemResolver.class);
		Dependency dependency = mock(Dependency.class);
		given(resolver.resolveDependency("test")).willReturn(dependency);
		TestBuild build = new TestBuild(resolver);
		assertThat(build.dependencies().ids()).hasSize(0);
		build.dependencies().add("test");
		assertThat(build.dependencies().items()).containsExactly(dependency);
	}

	@Test
	void buildWithCustomBuildItemResolverResolveBom() {
		BuildItemResolver resolver = mock(BuildItemResolver.class);
		BillOfMaterials bom = mock(BillOfMaterials.class);
		given(resolver.resolveBom("another")).willReturn(bom);
		TestBuild build = new TestBuild(resolver);
		assertThat(build.boms().ids()).hasSize(0);
		build.boms().add("another");
		assertThat(build.boms().items()).containsExactly(bom);
	}

	@Test
	void buildWithCustomBuildItemResolverResolveRepository() {
		BuildItemResolver resolver = mock(BuildItemResolver.class);
		MavenRepository repository = mock(MavenRepository.class);
		given(resolver.resolveRepository("repo")).willReturn(repository);
		TestBuild build = new TestBuild(resolver);
		assertThat(build.repositories().ids()).hasSize(0);
		build.repositories().add("repo");
		assertThat(build.repositories().items()).containsExactly(repository);
	}

	private static class TestBuild extends Build {

		TestBuild(BuildItemResolver buildItemResolver) {
			super(buildItemResolver);
		}

	}

}
