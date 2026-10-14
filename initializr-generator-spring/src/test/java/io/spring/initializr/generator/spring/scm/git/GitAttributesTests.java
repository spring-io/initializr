/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.scm.git;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GitAttributes}.
 *
 * @author Moritz Halbritter
 */
class GitAttributesTests {

	@Test
	void shouldWriteGitAttributes() {
		GitAttributes attributes = new GitAttributes();
		attributes.add("/gradlew", "text", "eof=lf");
		attributes.add("*.bat", "text", "eol=crlf");
		attributes.add("*.jar", "binary");
		String written = writeToString(attributes);
		assertThat(written).isEqualToNormalizingNewlines("""
				/gradlew text eof=lf
				*.bat text eol=crlf
				*.jar binary
				""");
	}

	private String writeToString(GitAttributes attributes) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
			attributes.write(printWriter);
		}
		return stringWriter.toString();
	}

}
