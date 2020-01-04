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

/**
 * Tests for {@link Scm}
 *
 * @author Joachim Pasquali
 */
public class ScmTest {

	@Test
	void emptyTest() {
		Scm scm = this.builder().build();
		assertThat(scm.isEmpty()).isTrue();
	}

	@Test
	void allElementsTest() {
		Scm scm = this.builder().connection("connection").developerConnection("developerConnection").url("url")
				.tag("tag").build();
		assertThat(scm.getConnection()).isEqualTo("connection");
		assertThat(scm.getDeveloperConnection()).isEqualTo("developerConnection");
		assertThat(scm.getTag()).isEqualTo("tag");
		assertThat(scm.getUrl()).isEqualTo("url");
		assertThat(scm.getChildScmConnectionInheritAppendPath()).isNull();
		assertThat(scm.getChildScmDeveloperConnectionInheritAppendPath()).isNull();
		assertThat(scm.getChildScmUrlInheritAppendPath()).isNull();
	}

	@Test
	void attributeFalseTest() {
		Scm scm = this.builder().childScmConnectionInheritAppendPath(Boolean.FALSE)
				.childScmDeveloperConnectionInheritAppendPath(Boolean.FALSE).childScmUrlInheritAppendPath(Boolean.FALSE)
				.build();
		assertThat(scm.getChildScmConnectionInheritAppendPath()).isFalse();
		assertThat(scm.getChildScmDeveloperConnectionInheritAppendPath()).isFalse();
		assertThat(scm.getChildScmUrlInheritAppendPath()).isFalse();

	}

	private Scm.Builder builder() {
		return new Scm.Builder();
	}

}
