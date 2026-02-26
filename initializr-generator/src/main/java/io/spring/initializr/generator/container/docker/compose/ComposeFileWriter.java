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

package io.spring.initializr.generator.container.docker.compose;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import io.spring.initializr.generator.io.IndentingWriter;
import org.jspecify.annotations.Nullable;

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
		writeServices(writer, compose);
		writeConfigs(writer, compose);
	}

	private void writeServices(IndentingWriter writer, ComposeFile compose) {
		if (compose.services().isEmpty()) {
			writer.println("services: {}");
			return;
		}
		writer.println("services:");
		compose.services()
			.values()
			.sorted(Comparator.comparing(ComposeService::getName))
			.forEach((service) -> writeService(writer, service));
	}

	private void writeConfigs(IndentingWriter writer, ComposeFile compose) {
		if (compose.configs().isEmpty()) {
			return;
		}
		writer.println();
		writer.println("configs:");
		compose.configs().entries().forEach((config) -> writeConfig(writer, config));
	}

	private void writeService(IndentingWriter writer, ComposeService service) {
		writer.indented(() -> {
			writer.println(service.getName() + ":");
			writer.indented(() -> {
				writer.println("image: '%s:%s'".formatted(service.getImage(), service.getImageTag()));
				writerServiceEnvironment(writer, service.getEnvironment());
				writerServiceLabels(writer, service.getLabels());
				writeServicePortMappings(writer, service.getPortMappings());
				writeServiceCommand(writer, service.getCommand());
				writeServiceConfigs(writer, service.getConfigs());
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

	private void writeServicePortMappings(IndentingWriter writer, Set<PortMapping> portMappings) {
		if (portMappings.isEmpty()) {
			return;
		}
		writer.println("ports:");
		writer.indented(() -> {
			for (PortMapping portMapping : portMappings) {
				if (portMapping.isFixed()) {
					writer.println("- '%d:%d'".formatted(portMapping.getHostPort(), portMapping.getContainerPort()));
				}
				else {
					writer.println("- '%d'".formatted(portMapping.getContainerPort()));
				}
			}
		});
	}

	private void writeServiceCommand(IndentingWriter writer, @Nullable String command) {
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

	private void writeServiceConfigs(IndentingWriter writer, Set<ComposeServiceConfig> configs) {
		if (configs.isEmpty()) {
			return;
		}
		writer.println("configs:");
		for (ComposeServiceConfig config : configs) {
			writer.indented(() -> {
				if (config instanceof ShortComposeServiceConfig shortConfig) {
					writeShortServiceConfig(writer, shortConfig);
				}
				else if (config instanceof LongComposeServiceConfig longConfig) {
					writeLongServiceConfig(writer, longConfig);
				}
				else {
					throw new IllegalStateException("Unsupported config type: " + config.getClass());
				}
			});
		}
	}

	private void writeShortServiceConfig(IndentingWriter writer, ShortComposeServiceConfig shortConfig) {
		writer.println("- \"%s\"".formatted(shortConfig.id()));
	}

	private void writeLongServiceConfig(IndentingWriter writer, LongComposeServiceConfig longConfig) {
		writer.println("- source: \"%s\"".formatted(longConfig.source()));
		if (longConfig.target() != null) {
			writer.println("  target: \"%s\"".formatted(longConfig.target()));
		}
		if (longConfig.uid() != null) {
			writer.println("  uid: \"%d\"".formatted(longConfig.uid()));
		}
		if (longConfig.gid() != null) {
			writer.println("  gid: \"%d\"".formatted(longConfig.gid()));
		}
		if (longConfig.mode() != null) {
			writer.println("  mode: 0%s".formatted(Integer.toOctalString(longConfig.mode())));
		}
	}

	private void writeConfig(IndentingWriter writer, Map.Entry<String, ComposeConfig> entry) {
		writer.indented(() -> {
			String id = entry.getKey();
			ComposeConfig config = entry.getValue();
			writer.println("%s:".formatted(id));
			writer.indented(() -> {
				if (config.getName() != null) {
					writer.println("name: \"%s\"".formatted(config.getName()));
				}
				if (config.isExternal()) {
					writer.println("external: true");
				}
				if (config.getFile() != null) {
					writer.println("file: \"%s\"".formatted(config.getFile()));
				}
				if (config.getEnvironment() != null) {
					writer.println("environment: \"%s\"".formatted(config.getEnvironment()));
				}
				if (config.getContent() != null) {
					writeContent(writer, config.getContent());
				}
			});
		});
	}

	private void writeContent(IndentingWriter writer, String content) {
		String[] lines = content.split("\n");
		if (lines.length == 1) {
			writer.println("content: \"%s\"".formatted(lines[0]));
		}
		else {
			writer.println("content: |");
			writer.indented(() -> {
				for (String line : lines) {
					writer.println(line);
				}
			});
		}
	}

}
