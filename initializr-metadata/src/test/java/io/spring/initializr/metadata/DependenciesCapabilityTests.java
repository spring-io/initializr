/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link DependenciesCapability}.
 *
 * @author Stephane Nicoll
 */
class DependenciesCapabilityTests {

	@Test
	void indexedDependencies() {
		Dependency dependency = Dependency.withId("first");
		Dependency dependency2 = Dependency.withId("second");
		DependenciesCapability capability = createDependenciesCapability("foo", dependency, dependency2);
		capability.validate();

		assertThat(capability.get("first")).isSameAs(dependency);
		assertThat(capability.get("second")).isSameAs(dependency2);
		assertThat(capability.get("anotherId")).isNull();
	}

	@Test
	void addTwoDependenciesWithSameId() {
		Dependency dependency = Dependency.withId("conflict");
		Dependency dependency2 = Dependency.withId("conflict");
		DependenciesCapability capability = createDependenciesCapability("foo", dependency, dependency2);
		assertThatIllegalArgumentException().isThrownBy(capability::validate).withMessageContaining("conflict");
	}

	@Test
	void addDependencyWithAliases() {
		Dependency dependency = Dependency.withId("first");
		dependency.getAliases().add("alias1");
		dependency.getAliases().add("alias2");
		DependenciesCapability capability = createDependenciesCapability("foo", dependency);
		capability.validate();
		assertThat(capability.get("first")).isSameAs(dependency);
		assertThat(capability.get("alias1")).isSameAs(dependency);
		assertThat(capability.get("alias2")).isSameAs(dependency);
	}

	@Test
	void aliasClashWithAnotherDependency() {
		Dependency dependency = Dependency.withId("first");
		dependency.getAliases().add("alias1");
		dependency.getAliases().add("alias2");
		Dependency dependency2 = Dependency.withId("alias2");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(createDependencyGroup("foo", dependency));
		capability.getContent().add(createDependencyGroup("bar", dependency2));
		assertThatIllegalArgumentException().isThrownBy(capability::validate).withMessageContaining("alias2");
	}

	@Test
	void mergeAddEntry() {
		DependenciesCapability capability = createDependenciesCapability("foo", Dependency.withId("first"),
				Dependency.withId("second"));

		DependenciesCapability anotherCapability = createDependenciesCapability("foo", Dependency.withId("bar"),
				Dependency.withId("biz"));
		anotherCapability.getContent().add(createDependencyGroup("bar", Dependency.withId("third")));

		capability.merge(anotherCapability);
		assertThat(capability.getContent()).hasSize(2);
		assertThat(capability.get("first")).isNotNull();
		assertThat(capability.get("second")).isNotNull();
		assertThat(capability.get("third")).isNotNull();
	}

	@Test
	void addDefaultCompatibilityRange() {
		Dependency first = Dependency.withId("first");
		Dependency second = Dependency.withId("second");
		second.setCompatibilityRange("1.2.3.RELEASE");
		DependencyGroup group = createDependencyGroup("test", first, second);
		group.setCompatibilityRange("1.2.0.RELEASE");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(group);
		capability.validate();

		first = capability.get("first");
		assertThat(first).isNotNull();
		assertThat(first.getCompatibilityRange()).isEqualTo("1.2.0.RELEASE");
		second = capability.get("second");
		assertThat(second).isNotNull();
		assertThat(second.getCompatibilityRange()).isEqualTo("1.2.3.RELEASE");
	}

	@Test
	void addDefaultBom() {
		Dependency first = Dependency.withId("first");
		Dependency second = Dependency.withId("second");
		second.setBom("da-bom");
		DependencyGroup group = createDependencyGroup("test", first, second);
		group.setBom("test-bom");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(group);
		capability.validate();

		first = capability.get("first");
		assertThat(first).isNotNull();
		assertThat(first.getBom()).isEqualTo("test-bom");
		second = capability.get("second");
		assertThat(second).isNotNull();
		assertThat(second.getBom()).isEqualTo("da-bom");
	}

	@Test
	void addDefaultRepository() {
		Dependency first = Dependency.withId("first");
		Dependency second = Dependency.withId("second");
		second.setRepository("da-repo");
		DependencyGroup group = createDependencyGroup("test", first, second);
		group.setRepository("test-repo");

		DependenciesCapability capability = new DependenciesCapability();
		capability.getContent().add(group);
		capability.validate();

		first = capability.get("first");
		assertThat(first).isNotNull();
		assertThat(first.getRepository()).isEqualTo("test-repo");
		second = capability.get("second");
		assertThat(second).isNotNull();
		assertThat(second.getRepository()).isEqualTo("da-repo");
	}

	private static DependenciesCapability createDependenciesCapability(String groupName, Dependency... dependencies) {
		DependenciesCapability capability = new DependenciesCapability();
		DependencyGroup group = createDependencyGroup(groupName, dependencies);
		capability.getContent().add(group);
		return capability;
	}

	private static DependencyGroup createDependencyGroup(String groupName, Dependency... dependencies) {
		DependencyGroup group = DependencyGroup.create(groupName);
		for (Dependency dependency : dependencies) {
			group.getContent().add(dependency);
		}
		return group;
	}

}
