/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.generator.test.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Path}-related test utilities.
 *
 * @author Stephane Nicoll
 */
public final class PathTestUtils {

	private PathTestUtils() {
	}

	/**
	 * Read all lines from the specified {@link Path source}. Check the given
	 * {@code source} is a regular file that ends with a new line.
	 * @param source a text file
	 * @return all lines from the file
	 */
	public static List<String> readAllLines(Path source) {
		assertThat(source).isRegularFile();
		try {
			BufferedReader reader = Files.newBufferedReader(source,
					StandardCharsets.UTF_8);
			StringWriter writer = new StringWriter();
			FileCopyUtils.copy(reader, writer);
			String content = writer.toString();
			String[] lines = content.split("\\r?\\n");
			assertThat(content).endsWith(System.lineSeparator());
			return Arrays.asList(lines);
		}
		catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

}
