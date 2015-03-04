/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.generator

import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.metadata.Type
import io.spring.initializr.util.VersionRange

import static io.spring.initializr.util.GroovyTemplate.template

/**
 * Generate help pages for command-line clients.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class CommandLineHelpGenerator {

	private static final String logo = '''
  .   ____          _            __ _ _
 /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\
( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\
 \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 '''

	/**
	 * Generate the capabilities of the service as a generic plain text
	 * document. Used when no particular agent was detected.
	 */
	String generateGenericCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeCommandLineModel(metadata, serviceUrl)
		model['hasExamples'] = false
		template 'cli-capabilities.txt', model
	}

	/**
	 * Generate the capabilities of the service using "curl" as a plain text
	 * document.
	 */
	String generateCurlCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeCommandLineModel(metadata, serviceUrl)
		model['examples'] = template 'curl-examples.txt', model
		model['hasExamples'] = true
		template 'cli-capabilities.txt', model
	}

	/**
	 * Generate the capabilities of the service using "HTTPie" as a plain text
	 * document.
	 */
	String generateHttpieCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeCommandLineModel(metadata, serviceUrl)
		model['examples'] = template 'httpie-examples.txt', model
		model['hasExamples'] = true
		template 'cli-capabilities.txt', model
	}

	/**
	 * Generate the capabilities of the service using Spring Boot CLI as a plain
	 * text document.
	 */
	String generateSpringBootCliCapabilities(InitializrMetadata metadata, String serviceUrl) {
		def model = initializeSpringBootCliModel(metadata, serviceUrl)
		model['hasExamples'] = false
		template('boot-cli-capabilities.txt', model)
	}

	protected Map initializeCommandLineModel(InitializrMetadata metadata, serviceUrl) {
		Map model = [:]
		model['logo'] = logo
		model['serviceUrl'] = serviceUrl
		model['dependencies'] = generateDependencyTable(metadata)
		model['types'] = generateTypeTable(metadata, 'Rel', false)

		Map defaults = metadata.defaults()
		defaults['applicationName'] = metadata.configuration.generateApplicationName(metadata.name.content)
		defaults['baseDir'] = 'no base dir'
		defaults['dependencies'] = 'none'

		Map parametersDescription = buildParametersDescription(metadata)
		String[][] parameterTable = new String[defaults.size() + 1][];
		parameterTable[0] = ["Parameter", "Description", "Default value"]
		defaults.sort().keySet().eachWithIndex { id, i ->
			String[] data = new String[3]
			data[0] = id
			data[1] = parametersDescription[id]
			data[2] = defaults[id]
			parameterTable[i + 1] = data
		}
		model['parameters'] = TableGenerator.generate(parameterTable)

		model
	}

	protected Map initializeSpringBootCliModel(InitializrMetadata metadata, serviceUrl) {
		Map model = [:]
		model['logo'] = logo
		model['serviceUrl'] = serviceUrl
		model['dependencies'] = generateDependencyTable(metadata)
		model['types'] = generateTypeTable(metadata, 'Id', true)


		Map defaults = metadata.defaults()
		Map parametersDescription = buildParametersDescription(metadata)
		String[][] parameterTable = new String[defaults.size() + 1][];
		parameterTable[0] = ["Id", "Description", "Default value"]
		defaults.keySet().eachWithIndex { id, i ->
			String[] data = new String[3]
			data[0] = id
			data[1] = parametersDescription[id]
			data[2] = defaults[id]
			parameterTable[i + 1] = data
		}
		model['parameters'] = TableGenerator.generate(parameterTable)
		model
	}

	protected String generateDependencyTable(InitializrMetadata metadata) {
		String[][] dependencyTable = new String[metadata.dependencies.all.size() + 1][];
		dependencyTable[0] = ["Id", "Description", "Required version"]
		new ArrayList(metadata.dependencies.all).sort { a, b -> a.id <=> b.id }
				.eachWithIndex { dep, i ->
			String[] data = new String[3]
			data[0] = dep.id
			data[1] = dep.description ?: dep.name
			data[2] = buildVersionRangeRepresentation(dep.versionRange)
			dependencyTable[i + 1] = data
		}
		TableGenerator.generate(dependencyTable)
	}

	protected String generateTypeTable(InitializrMetadata metadata, String linkHeader, boolean addTags) {
		String[][] typeTable = new String[metadata.types.content.size() + 1][];
		if (addTags) {
			typeTable[0] = [linkHeader, "Description", "Tags"]
		}
		else {
			typeTable[0] = [linkHeader, "Description"]
		}
		new ArrayList<>(metadata.types.content).sort { a, b -> a.id <=> b.id }.eachWithIndex { type, i ->
			String[] data = new String[typeTable[0].length]
			data[0] = (type.default ? type.id + " *" : type.id)
			data[1] = type.description ?: type.name
			if (addTags) {
				data[2] = buildTagRepresentation(type)
			}
			typeTable[i + 1] = data;
		}
		TableGenerator.generate(typeTable)
	}

	protected Map buildParametersDescription(InitializrMetadata metadata) {
		Map result = [:]
		metadata.properties.each { key, value ->
			if (value.hasProperty('description') && value.hasProperty('id')) {
				result[value.id] = value['description']
			}
		}
		result['applicationName'] = 'application name'
		result['baseDir'] = 'base directory to create in the archive'
		result
	}

	private static String buildVersionRangeRepresentation(String range) {
		if (!range) {
			return null
		}
		VersionRange versionRange = VersionRange.parse(range)
		if (versionRange.higherVersion == null) {
			return ">= $range"
		} else {
			return range.trim()
		}
	}

	private static String buildTagRepresentation(Type type) {
		if (type.tags.isEmpty()) {
			return "";
		}
		type.tags.collect { key, value ->
			"$key:$value"
		}.join(",")
	}

	private static class TableGenerator {

		static final String NEW_LINE = System.getProperty("line.separator")

		/**
		 * Generate a table description for the specified {@code content}.
		 * <p>
		 * The {@code content} is a two-dimensional array holding the rows
		 * of the table. The first entry holds the header of the table.
		 */
		public static String generate(String[][] content) {
			StringBuilder sb = new StringBuilder()
			int[] columnsLength = computeColumnsLength(content)
			appendTableSeparation(sb, columnsLength)
			appendRow(sb, content, columnsLength, 0) // Headers
			appendTableSeparation(sb, columnsLength)
			for (int i = 1; i < content.length; i++) {
				appendRow(sb, content, columnsLength, i)
			}
			appendTableSeparation(sb, columnsLength)
			sb.toString()
		}


		private static void appendRow(StringBuilder sb, String[][] content,
									  int[] columnsLength, int rowIndex) {
			String[] row = content[rowIndex]
			for (int i = 0; i < row.length; i++) {
				sb.append("| ").append(fill(row[i], columnsLength[i])).append(" ")
			}
			sb.append("|")
			sb.append(NEW_LINE)
		}

		private static void appendTableSeparation(StringBuilder sb, int[] headersLength) {
			for (int headerLength : headersLength) {
				sb.append("+").append("-".multiply(headerLength + 2))
			}
			sb.append("+")
			sb.append(NEW_LINE)
		}

		private static String fill(String data, int columnSize) {
			if (data == null) {
				return " ".multiply(columnSize)
			} else {
				int i = columnSize - data.length()
				return data + " ".multiply(i)
			}
		}

		private static int[] computeColumnsLength(String[][] content) {
			int count = content[0].length
			int[] result = new int[count]
			for (int i = 0; i < count; i++) {
				result[i] = largest(content, i)
			}
			return result
		}

		private static int largest(String[][] content, int column) {
			int max = 0
			for (String[] rows : content) {
				String s = rows[column]
				if (s && s.length() > max) {
					max = s.length()
				}
			}
			return max
		}

	}

}
