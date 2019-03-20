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

package io.spring.initializr.generator.language;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Annotation}.
 *
 * @author Stephane Nicoll
 */
class AnnotationTests {

	@Test
	void annotationWithNoAttribute() {
		Annotation annotation = Annotation.name("com.example.Test");
		assertThat(annotation.getName()).isEqualTo("com.example.Test");
		assertThat(annotation.getAttributes()).isEmpty();
	}

	@Test
	void annotationWithSingleAttribute() {
		Annotation annotation = Annotation.name("com.example.Test",
				(builder) -> builder.attribute("test", Enum.class,
						"com.example.Unit.CENTURIES, com.example.Unit.NANOS"));
		assertThat(annotation.getName()).isEqualTo("com.example.Test");
		assertThat(annotation.getAttributes()).hasSize(1);
		Annotation.Attribute attribute = annotation.getAttributes().get(0);
		assertThat(attribute.getName()).isEqualTo("test");
		assertThat(attribute.getType()).isEqualTo(Enum.class);
		assertThat(attribute.getValues())
				.containsExactly("com.example.Unit.CENTURIES, com.example.Unit.NANOS");
	}

}
