/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.metadata;

import java.util.Arrays;

import io.spring.initializr.util.Version;
import io.spring.initializr.util.VersionParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for {@link Dependency}.
 *
 * @author Stephane Nicoll
 */
public class DependencyTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void createRootSpringBootStarter() {
		Dependency d = new Dependency();
		d.asSpringBootStarter("");
		assertEquals("org.springframework.boot", d.getGroupId());
		assertEquals("spring-boot-starter", d.getArtifactId());
	}

	@Test
	public void setCoordinatesFromId() {
		Dependency dependency = Dependency.withId("org.foo:bar:1.2.3");
		dependency.resolve();
		assertEquals("org.foo", dependency.getGroupId());
		assertEquals("bar", dependency.getArtifactId());
		assertEquals("1.2.3", dependency.getVersion());
		assertEquals("org.foo:bar:1.2.3", dependency.getId());
	}

	@Test
	public void setCoordinatesFromIdNoVersion() {
		Dependency dependency = Dependency.withId("org.foo:bar");
		dependency.resolve();
		assertEquals("org.foo", dependency.getGroupId());
		assertEquals("bar", dependency.getArtifactId());
		assertNull(dependency.getVersion());
		assertEquals("org.foo:bar", dependency.getId());
	}

	@Test
	public void setIdFromCoordinates() {
		Dependency dependency = new Dependency();
		dependency.setGroupId("org.foo");
		dependency.setArtifactId("bar");
		dependency.setVersion("1.0");
		dependency.resolve();
		assertEquals("org.foo:bar", dependency.getId());
	}

	@Test
	public void setIdFromCoordinatesNoVersion() {
		Dependency dependency = new Dependency();
		dependency.setGroupId("org.foo");
		dependency.setArtifactId("bar");
		dependency.resolve();
		assertEquals("org.foo:bar", dependency.getId());
	}

	@Test
	public void setIdFromSimpleName() {
		Dependency dependency = Dependency.withId("web");
		dependency.resolve();
		assertEquals("org.springframework.boot", dependency.getGroupId());
		assertEquals("spring-boot-starter-web", dependency.getArtifactId());
		assertNull(dependency.getVersion());
		assertEquals("web", dependency.getId());
	}

	@Test
	public void invalidDependency() {
		thrown.expect(InvalidInitializrMetadataException.class);
		new Dependency().resolve();
	}

	@Test
	public void invalidDependencyScope() {
		Dependency dependency = Dependency.withId("web");

		thrown.expect(InvalidInitializrMetadataException.class);
		dependency.setScope("whatever");
	}

	@Test
	public void invalidSpringBootRange() {
		Dependency dependency = Dependency.withId("web");
		dependency.setVersionRange("A.B.C");

		thrown.expect(InvalidInitializrMetadataException.class);
		thrown.expectMessage("A.B.C");
		dependency.resolve();
	}

	@Test
	public void invalidIdFormatTooManyColons() {
		Dependency dependency = Dependency.withId("org.foo:bar:1.0:test:external");

		thrown.expect(InvalidInitializrMetadataException.class);
		dependency.resolve();
	}

	@Test
	public void invalidLink() {
		Dependency dependency = Dependency.withId("foo");
		dependency.getLinks().add(Link.create(null, "https://example.com"));

		thrown.expect(InvalidInitializrMetadataException.class);
		dependency.resolve();
	}

	@Test
	public void generateIdWithNoGroupId() {
		Dependency dependency = new Dependency();
		dependency.setArtifactId("bar");
		thrown.expect(IllegalArgumentException.class);
		dependency.generateId();
	}

	@Test
	public void generateIdWithNoArtifactId() {
		Dependency dependency = new Dependency();
		dependency.setGroupId("foo");
		thrown.expect(IllegalArgumentException.class);
		dependency.generateId();
	}

	@Test
	public void resolveNoMapping() {
		Dependency dependency = Dependency.withId("web");
		dependency.resolve();
		assertSame(dependency, dependency.resolve(Version.parse("1.2.0.RELEASE")));
	}

	@Test
	public void resolveInvalidMapping() {
		Dependency dependency = Dependency.withId("web");
		dependency.getMappings()
				.add(Dependency.Mapping.create("foo-bar", null, null, "0.1.0.RELEASE"));
		thrown.expect(InvalidInitializrMetadataException.class);
		thrown.expectMessage("foo-bar");
		dependency.resolve();
	}

	@Test
	public void resolveVersionRequirement() {
		Dependency dependency = Dependency.withId("web");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE"));
		dependency.resolve();
		Dependency resolved = dependency.resolve(Version.parse("1.1.5.RELEASE"));
		assertEquals(">=1.1.0.RELEASE and <1.2.0.RELEASE",
				resolved.getVersionRequirement());
	}

	@Test
	public void resolveMatchingVersionMapping() {
		Dependency dependency = Dependency.withId("web", null, null, "0.3.0.RELEASE");
		dependency.setDescription("A web dependency");
		dependency.getKeywords().addAll(Arrays.asList("foo", "bar"));
		dependency.getAliases().add("the-web");
		dependency.getFacets().add("web");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, null, "0.2.0.RELEASE"));
		dependency.resolve();

		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")),
				"org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.2.0.RELEASE")),
				"org.springframework.boot", "spring-boot-starter-web", "0.2.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")),
				"org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE"); // default
	}

	@Test
	public void resolveMatchArtifactMapping() {
		Dependency dependency = Dependency.withId("web", null, null, "0.3.0.RELEASE");
		dependency.setDescription("A web dependency");
		dependency.getKeywords().addAll(Arrays.asList("foo", "bar"));
		dependency.getAliases().add("the-web");
		dependency.getFacets().add("web");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", "org.spring.boot", null, null));
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, "starter-web", null));
		dependency.resolve();

		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")),
				"org.spring.boot", "spring-boot-starter-web", "0.3.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.2.0.RELEASE")),
				"org.springframework.boot", "starter-web", "0.3.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")),
				"org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE"); // default
	}

	@Test
	public void resolveMatchingVersionWithVariablePatch() {
		Dependency dependency = Dependency.withId("web", null, null, "0.3.0.RELEASE");
		dependency.setDescription("A web dependency");
		dependency.getKeywords().addAll(Arrays.asList("foo", "bar"));
		dependency.getAliases().add("the-web");
		dependency.getFacets().add("web");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.1.x.RELEASE]", null, null, "0.1.0.RELEASE"));
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.x.BUILD-SNAPSHOT, 1.2.0.RELEASE)", null, null, "0.2.0.RELEASE"));
		dependency.resolve();

		dependency.updateVersionRanges(new VersionParser(Arrays.asList(
				Version.parse("1.1.5.RELEASE"), Version.parse("1.1.6.BUILD-SNAPSHOT"))));
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")),
				"org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.6.BUILD-SNAPSHOT")),
				"org.springframework.boot", "spring-boot-starter-web", "0.2.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")),
				"org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE"); // default

		dependency.updateVersionRanges(new VersionParser(Arrays.asList(
				Version.parse("1.1.6.RELEASE"), Version.parse("1.1.7.BUILD-SNAPSHOT"))));
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")),
				"org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.6.RELEASE")),
				"org.springframework.boot", "spring-boot-starter-web", "0.1.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("1.1.7.BUILD-SNAPSHOT")),
				"org.springframework.boot", "spring-boot-starter-web", "0.2.0.RELEASE");
		validateResolvedWebDependency(dependency.resolve(Version.parse("2.1.3.M1")),
				"org.springframework.boot", "spring-boot-starter-web", "0.3.0.RELEASE"); // default
	}

	@Test
	public void resolveMatchingWithCustomGroupId() {
		Dependency dependency = Dependency.withId("foo", "com.acme", "foo",
				"0.3.0.RELEASE");
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.1.0.RELEASE, 1.2.0.RELEASE)", null, null, "1.0.0.RELEASE"));
		dependency.getMappings().add(Dependency.Mapping
				.create("[1.2.0.RELEASE, 1.3.0.RELEASE)", null, "bar", null));
		dependency.resolve();
		validateResolvedDependency(dependency.resolve(Version.parse("1.1.5.RELEASE")),
				"foo", "com.acme", "foo", "1.0.0.RELEASE");
		validateResolvedDependency(dependency.resolve(Version.parse("1.2.5.RELEASE")),
				"foo", "com.acme", "bar", "0.3.0.RELEASE");
	}

	private static void validateResolvedWebDependency(Dependency dependency,
			String expectedGroupId, String expectedArtifactId, String expectedVersion) {
		validateResolvedDependency(dependency, "web", expectedGroupId,
				expectedArtifactId, expectedVersion);
		assertEquals(2, dependency.getKeywords().size());
		assertEquals(1, dependency.getAliases().size());
		assertEquals(1, dependency.getFacets().size());
	}

	private static void validateResolvedDependency(Dependency dependency, String id,
			String expectedGroupId, String expectedArtifactId, String expectedVersion) {
		assertEquals(id, dependency.getId());
		assertEquals(expectedGroupId, dependency.getGroupId());
		assertEquals(expectedArtifactId, dependency.getArtifactId());
		assertEquals(expectedVersion, dependency.getVersion());
	}

}
