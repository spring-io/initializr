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

package io.spring.initializr.generator.spring.documentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.project.JvmVersionAdjustment;
import io.spring.initializr.generator.project.JvmVersionAdjustmentReason;
import io.spring.initializr.generator.project.MutableProjectDescription;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JvmVersionAdjustmentsHelpDocumentCustomizer}.
 *
 * @author Moritz Halbritter
 */
class JvmVersionAdjustmentsHelpDocumentCustomizerTests {

	private final MustacheTemplateRenderer templateRenderer = new MustacheTemplateRenderer("classpath:/templates");

	@Test
	void noAdjustmentsDoesNotAddSection() {
		MutableProjectDescription description = new MutableProjectDescription();
		HelpDocument document = new HelpDocument(this.templateRenderer);
		new JvmVersionAdjustmentsHelpDocumentCustomizer(description).customize(document);
		assertThat(document.getSections()).isEmpty();
	}

	@Test
	void recordsSpringBootAndDependencyAdjustmentsInOrder() {
		MutableProjectDescription description = new MutableProjectDescription();
		description
			.addJvmVersionAdjustment(new JvmVersionAdjustment("1.8", "17", JvmVersionAdjustmentReason.SPRING_BOOT));
		description.addJvmVersionAdjustment(new JvmVersionAdjustment("17", "21",
				JvmVersionAdjustmentReason.SELECTED_DEPENDENCY, "vaadin", "Vaadin"));
		HelpDocument document = new HelpDocument(this.templateRenderer);
		new JvmVersionAdjustmentsHelpDocumentCustomizer(description).customize(document);
		assertThat(document.getSections()).hasSize(1);
		String out = write(document);
		assertThat(out).contains("### JVM version", "JVM level was changed from 1.8 to 17", "Spring Boot",
				"JVM level was changed from 17 to 21", "Vaadin");
	}

	@Test
	void kotlinMessageWithVersionInDetail() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addJvmVersionAdjustment(
				new JvmVersionAdjustment("25", "21", JvmVersionAdjustmentReason.KOTLIN_COMPILER, null, "2.2.0"));
		HelpDocument document = new HelpDocument(this.templateRenderer);
		new JvmVersionAdjustmentsHelpDocumentCustomizer(description).customize(document);
		String out = write(document);
		assertThat(out).contains("Kotlin 2.2.0 does not support the previously selected JVM level");
	}

	@Test
	void getOrderIsBeforeLowestPrecedence() {
		assertThat(new JvmVersionAdjustmentsHelpDocumentCustomizer(new MutableProjectDescription()).getOrder())
			.isLessThan(org.springframework.core.Ordered.LOWEST_PRECEDENCE);
	}

	private String write(HelpDocument document) {
		try {
			StringWriter out = new StringWriter();
			document.write(new PrintWriter(out));
			return out.toString();
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
