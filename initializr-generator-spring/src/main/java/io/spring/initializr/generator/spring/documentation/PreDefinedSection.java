/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.documentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.io.text.Section;

/**
 * Section that is pre-defined and always present in the document. You can only add
 * additional sections to pre-defined sections.
 *
 * @author Madhura Bhave
 */
public class PreDefinedSection implements Section {

	private final String title;

	private final List<Section> subSections = new ArrayList<>();

	public PreDefinedSection(String title) {
		this.title = title;
	}

	/**
	 * Add a sub-section.
	 * @param section the section
	 * @return this for method chaining
	 */
	public PreDefinedSection addSection(Section section) {
		this.subSections.add(section);
		return this;
	}

	@Override
	public void write(PrintWriter writer) throws IOException {
		if (!isEmpty()) {
			writer.println("# " + this.title);
			writer.println("");
			for (Section section : resolveSubSections(this.subSections)) {
				section.write(writer);
			}
		}
	}

	public boolean isEmpty() {
		return this.subSections.isEmpty();
	}

	/**
	 * Resolve the sections to render based on the current registered sections.
	 * @param sections the registered sections
	 * @return the sections to render
	 */
	protected List<Section> resolveSubSections(List<Section> sections) {
		return sections;
	}

}
