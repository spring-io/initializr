/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.test.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Text content related test utilities.
 *
 * @author Stephane Nicoll
 */
public final class TextTestUtils {

	private TextTestUtils() {
	}

	/**
	 * Split the given source into lines.
	 * @param source the source
	 * @return the lines
	 */
	public static List<String> readAllLines(String source) {
		String[] lines = source.split("\\r?\\n");
		return Arrays.asList(lines);
	}

	/**
	 * Read the content from the specified {@link Path source}. Check the given
	 * {@code source} is a regular file.
	 * @param source a text file
	 * @return the content of the file
	 */
	public static String readContent(Path source) {
		assertThat(source).isRegularFile();
		try (InputStream stream = Files.newInputStream(source)) {
			return StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
