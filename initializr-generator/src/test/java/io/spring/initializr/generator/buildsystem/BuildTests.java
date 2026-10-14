/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import io.spring.initializr.generator.buildsystem.BuildSettings.Builder;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.Nullable;
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

		private final TestBuildSettingsBuilder settings = new TestBuildSettingsBuilder();

		TestBuild(@Nullable BuildItemResolver buildItemResolver) {
			super(buildItemResolver);
		}

		@Override
		public Builder<?> settings() {
			return this.settings;
		}

		@Override
		public BuildSettings getSettings() {
			return this.settings.build();
		}

		private static final class TestBuildSettingsBuilder extends BuildSettings.Builder<TestBuildSettingsBuilder> {

			@Override
			public BuildSettings build() {
				return new BuildSettings(this);
			}

		}

	}

}
