/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.container.docker.compose;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import io.spring.initializr.generator.io.IndentingWriter;

import org.springframework.util.StringUtils;

/**
 * A {@link ComposeFile} writer for {@code compose.yaml}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 * @author Eddú Meléndez
 */
public class ComposeFileWriter {

	/**
	 * Write a {@linkplain ComposeFile compose.yaml} using the specified
	 * {@linkplain IndentingWriter writer}.
	 * @param writer the writer to use
	 * @param compose the compose file to write
	 */
	public void writeTo(IndentingWriter writer, ComposeFile compose) {
		writer.println("services:");
		compose.services()
			.values()
			.sorted(Comparator.comparing(ComposeService::getName))
			.forEach((service) -> writeService(writer, service));
	}

	private void writeService(IndentingWriter writer, ComposeService service) {
		writer.indented(() -> {
			writer.println(service.getName() + ":");
			writer.indented(() -> {
				writer.println("image: '%s:%s'".formatted(service.getImage(), service.getImageTag()));
				writerServiceEnvironment(writer, service.getEnvironment());
				writerServiceLabels(writer, service.getLabels());
				writerServicePorts(writer, service.getPorts());
				writeServiceCommand(writer, service.getCommand());
			});
		});
	}

	private void writerServiceEnvironment(IndentingWriter writer, Map<String, String> environment) {
		if (environment.isEmpty()) {
			return;
		}
		writer.println("environment:");
		writer.indented(() -> {
			for (Map.Entry<String, String> env : environment.entrySet()) {
				writer.println("- '%s=%s'".formatted(env.getKey(), env.getValue()));
			}
		});
	}

	private void writerServicePorts(IndentingWriter writer, Set<Integer> ports) {
		if (ports.isEmpty()) {
			return;
		}
		writer.println("ports:");
		writer.indented(() -> {
			for (Integer port : ports) {
				writer.println("- '%d'".formatted(port));
			}
		});
	}

	private void writeServiceCommand(IndentingWriter writer, String command) {
		if (!StringUtils.hasText(command)) {
			return;
		}
		writer.println("command: '%s'".formatted(command));
	}

	private void writerServiceLabels(IndentingWriter writer, Map<String, String> labels) {
		if (labels.isEmpty()) {
			return;
		}
		writer.println("labels:");
		writer.indented(() -> {
			for (Map.Entry<String, String> label : labels.entrySet()) {
				writer.println("- \"%s=%s\"".formatted(label.getKey(), label.getValue()));
			}
		});
	}

}
