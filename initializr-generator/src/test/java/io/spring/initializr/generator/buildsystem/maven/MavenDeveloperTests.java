/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link MavenDeveloper}.
 *
 * @author Jafer Khan Shamshad
 */
class MavenDeveloperTests {

	@Test
	void developerWithIdOnly() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jsmith").build();
		assertThat(developer.getId()).isEqualTo("jsmith");
		assertThat(developer.getName()).isNull();
		assertThat(developer.getEmail()).isNull();
		assertThat(developer.getUrl()).isNull();
		assertThat(developer.getOrganization()).isNull();
		assertThat(developer.getOrganizationUrl()).isNull();
		assertThat(developer.getRoles()).hasSize(0);
		assertThat(developer.getTimezone()).isNull();
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithFullDetails() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jsmith")
			.name("John Smith")
			.email("john@example.com")
			.url("http://www.example.com/jsmith")
			.organization("Acme Corp")
			.organizationUrl("http://www.example.com")
			.role("developer")
			.role("tester")
			.timezone("Asia/Karachi")
			.property("prop1", "test1")
			.property("prop2", "test2")
			.property("prop3", "test3")
			.build();
		assertThat(developer.getId()).isEqualTo("jsmith");
		assertThat(developer.getName()).isEqualTo("John Smith");
		assertThat(developer.getEmail()).isEqualTo("john@example.com");
		assertThat(developer.getUrl()).isEqualTo("http://www.example.com/jsmith");
		assertThat(developer.getOrganization()).isEqualTo("Acme Corp");
		assertThat(developer.getOrganizationUrl()).isEqualTo("http://www.example.com");
		assertThat(developer.getRoles()).containsExactly("developer", "tester");
		assertThat(developer.getTimezone()).isEqualTo("Asia/Karachi");
		assertThat(developer.getProperties()).containsExactly(entry("prop1", "test1"), entry("prop2", "test2"),
				entry("prop3", "test3"));
	}

}
