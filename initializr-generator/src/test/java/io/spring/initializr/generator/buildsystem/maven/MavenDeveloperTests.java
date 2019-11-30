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

package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link MavenDeveloper}
 *
 * @author Jafer Khan Shamshad
 */
public class MavenDeveloperTests {

	@Test
	void developerWithIdOnly() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
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
	void developerWithName() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isNull();
		assertThat(developer.getUrl()).isNull();
		assertThat(developer.getOrganization()).isNull();
		assertThat(developer.getOrganizationUrl()).isNull();
		assertThat(developer.getRoles()).hasSize(0);
		assertThat(developer.getTimezone()).isNull();
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithEmail() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad")
				.email("jaferkhan@example.com").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isEqualTo("jaferkhan@example.com");
		assertThat(developer.getUrl()).isNull();
		assertThat(developer.getOrganization()).isNull();
		assertThat(developer.getOrganizationUrl()).isNull();
		assertThat(developer.getRoles()).hasSize(0);
		assertThat(developer.getTimezone()).isNull();
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithUrl() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad")
				.email("jaferkhan@example.com").url("http://www.example.com/jaferkhan").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isEqualTo("jaferkhan@example.com");
		assertThat(developer.getUrl()).isEqualTo("http://www.example.com/jaferkhan");
		assertThat(developer.getOrganization()).isNull();
		assertThat(developer.getOrganizationUrl()).isNull();
		assertThat(developer.getRoles()).hasSize(0);
		assertThat(developer.getTimezone()).isNull();
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithOrganization() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad")
				.email("jaferkhan@example.com").url("http://www.example.com/jaferkhan").organization("ACME")
				.organizationUrl("http://www.example.com").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isEqualTo("jaferkhan@example.com");
		assertThat(developer.getUrl()).isEqualTo("http://www.example.com/jaferkhan");
		assertThat(developer.getOrganization()).isEqualTo("ACME");
		assertThat(developer.getOrganizationUrl()).isEqualTo("http://www.example.com");
		assertThat(developer.getRoles()).hasSize(0);
		assertThat(developer.getTimezone()).isNull();
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithRoles() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad")
				.email("jaferkhan@example.com").url("http://www.example.com/jaferkhan").organization("ACME")
				.organizationUrl("http://www.example.com").role("developer").role("tester").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isEqualTo("jaferkhan@example.com");
		assertThat(developer.getUrl()).isEqualTo("http://www.example.com/jaferkhan");
		assertThat(developer.getOrganization()).isEqualTo("ACME");
		assertThat(developer.getOrganizationUrl()).isEqualTo("http://www.example.com");
		assertThat(developer.getRoles()).hasSize(2);
		assertThat(developer.getRoles().isEmpty()).isFalse();
		assertThat(developer.getRoles()).containsExactly("developer", "tester");
		assertThat(developer.getTimezone()).isNull();
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithTimezone() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad")
				.email("jaferkhan@example.com").url("http://www.example.com/jaferkhan").organization("ACME")
				.organizationUrl("http://www.example.com").role("developer").role("tester").timezone("Asia/Karachi")
				.build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isEqualTo("jaferkhan@example.com");
		assertThat(developer.getUrl()).isEqualTo("http://www.example.com/jaferkhan");
		assertThat(developer.getOrganization()).isEqualTo("ACME");
		assertThat(developer.getOrganizationUrl()).isEqualTo("http://www.example.com");
		assertThat(developer.getRoles()).hasSize(2);
		assertThat(developer.getRoles()).containsExactly("developer", "tester");
		assertThat(developer.getTimezone()).isEqualTo("Asia/Karachi");
		assertThat(developer.getProperties()).hasSize(0);
	}

	@Test
	void developerWithProperties() {
		MavenDeveloper developer = new MavenDeveloper.Builder().id("jaferkhan").name("Jafer Khan Shamshad")
				.email("jaferkhan@example.com").url("http://www.example.com/jaferkhan").organization("ACME")
				.organizationUrl("http://www.example.com").role("developer").role("tester").timezone("Asia/Karachi")
				.property("hometown", "Mardan").property("ethnicity", "Pukhtun").property("religion", "Islam").build();
		assertThat(developer.getId()).isEqualTo("jaferkhan");
		assertThat(developer.getName()).isEqualTo("Jafer Khan Shamshad");
		assertThat(developer.getEmail()).isEqualTo("jaferkhan@example.com");
		assertThat(developer.getUrl()).isEqualTo("http://www.example.com/jaferkhan");
		assertThat(developer.getOrganization()).isEqualTo("ACME");
		assertThat(developer.getOrganizationUrl()).isEqualTo("http://www.example.com");
		assertThat(developer.getRoles()).hasSize(2);
		assertThat(developer.getRoles().isEmpty()).isFalse();
		assertThat(developer.getRoles()).containsExactly("developer", "tester");
		assertThat(developer.getTimezone()).isEqualTo("Asia/Karachi");
		assertThat(developer.getProperties()).hasSize(3);
		assertThat(developer.getProperties()).containsExactly(entry("hometown", "Mardan"),
				entry("ethnicity", "Pukhtun"), entry("religion", "Islam"));
	}

}
