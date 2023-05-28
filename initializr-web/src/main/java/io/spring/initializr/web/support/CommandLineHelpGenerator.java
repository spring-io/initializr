/*
 * Copyright 2012-2023 the original author or authors.
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
package io.spring.initializr.web.support;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.MetadataElement;
import io.spring.initializr.metadata.Type;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Generate help pages for command-line clients.
 *
 * @author Stephane Nicoll
 */
public class CommandLineHelpGenerator {

    private static final String LOGO = "  .   ____          _            __ _ _\n" + " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\\n" + "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\\n" + " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )\n" + "  '  |____| .__|_| |_|_| |_\\__, | / / / /\n" + " =========|_|==============|___/=/_/_/_/";

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final TemplateRenderer template;

    private final int maxColumnWidth;

    public CommandLineHelpGenerator(TemplateRenderer template) {
        this(template, 60);
    }

    public CommandLineHelpGenerator(TemplateRenderer template, int maxColumnWidth) {
        this.template = template;
        this.maxColumnWidth = maxColumnWidth;
    }

    /**
     * Generate the capabilities of the service as a generic plain text document. Used
     * when no particular agent was detected.
     * @param metadata the initializr metadata
     * @param serviceUrl the service URL
     * @return the generic capabilities text document
     * @throws IOException if rendering the capabilities failed
     */
    public String generateGenericCapabilities(InitializrMetadata metadata, String serviceUrl) throws IOException {
        Map<String, Object> model = initializeCommandLineModel(metadata, serviceUrl);
        model.put("hasExamples", false);
        return this.template.render("cli/cli-capabilities", model);
    }

    /**
     * Generate the capabilities of the service using "curl" as a plain text document.
     * @param metadata the initializr metadata
     * @param serviceUrl the service URL
     * @return the generic capabilities text document
     * @throws IOException if rendering the capabilities failed
     */
    public String generateCurlCapabilities(InitializrMetadata metadata, String serviceUrl) throws IOException {
        Map<String, Object> model = initializeCommandLineModel(metadata, serviceUrl);
        model.put("examples", this.template.render("cli/curl-examples", model));
        model.put("hasExamples", true);
        return this.template.render("cli/cli-capabilities", model);
    }

    /**
     * Generate the capabilities of the service using "HTTPie" as a plain text document.
     * @param metadata the initializr metadata
     * @param serviceUrl the service URL
     * @return the generic capabilities text document
     * @throws IOException if rendering the capabilities failed
     */
    public String generateHttpieCapabilities(InitializrMetadata metadata, String serviceUrl) throws IOException {
        Map<String, Object> model = initializeCommandLineModel(metadata, serviceUrl);
        model.put("examples", this.template.render("cli/httpie-examples", model));
        model.put("hasExamples", true);
        return this.template.render("cli/cli-capabilities", model);
    }

    /**
     * Generate the capabilities of the service using Spring Boot CLI as a plain text
     * document.
     * @param metadata the initializr metadata
     * @param serviceUrl the service URL
     * @return the generic capabilities text document
     * @throws IOException if rendering the capabilities failed
     */
    public String generateSpringBootCliCapabilities(InitializrMetadata metadata, String serviceUrl) throws IOException {
        Map<String, Object> model = initializeSpringBootCliModel(metadata, serviceUrl);
        model.put("hasExamples", false);
        return this.template.render("cli/boot-cli-capabilities", model);
    }

    protected Map<String, Object> initializeCommandLineModel(InitializrMetadata metadata, String serviceUrl) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("logo", LOGO);
        model.put("serviceUrl", serviceUrl);
        model.put("dependencies", generateDependencyTable(metadata));
        model.put("types", generateTypeTable(metadata, "Rel", false));
        Map<String, Object> defaults = metadata.defaults();
        defaults.put("applicationName", metadata.getConfiguration().generateApplicationName(metadata.getName().getContent()));
        defaults.put("baseDir", "no base dir");
        defaults.put("dependencies", "none");
        Map<String, Object> parametersDescription = buildParametersDescription(metadata);
        String[][] parameterTable = new String[defaults.size() + 1][];
        parameterTable[0] = new String[] { "Parameter", "Description", "Default value" };
        int i = 1;
        for (String id : defaults.keySet().stream().sorted().toList()) {
            String[] data = new String[3];
            data[0] = id;
            data[1] = (String) parametersDescription.get(id);
            data[2] = (String) defaults.get(id);
            parameterTable[i++] = data;
        }
        model.put("parameters", TableGenerator.generate(parameterTable, this.maxColumnWidth));
        return model;
    }

    protected Map<String, Object> initializeSpringBootCliModel(InitializrMetadata metadata, String serviceUrl) {
        Map<String, Object> model = new LinkedHashMap<>();
        model.put("logo", LOGO);
        model.put("serviceUrl", serviceUrl);
        model.put("dependencies", generateDependencyTable(metadata));
        model.put("types", generateTypeTable(metadata, "Id", true));
        Map<String, Object> defaults = metadata.defaults();
        Map<String, Object> parametersDescription = buildParametersDescription(metadata);
        String[][] parameterTable = new String[defaults.size() + 1][];
        parameterTable[0] = new String[] { "Id", "Description", "Default value" };
        int i = 1;
        for (String id : defaults.keySet().stream().sorted().toList()) {
            String[] data = new String[3];
            data[0] = id;
            data[1] = (String) parametersDescription.get(id);
            data[2] = (String) defaults.get(id);
            parameterTable[i++] = data;
        }
        model.put("parameters", TableGenerator.generate(parameterTable, this.maxColumnWidth));
        return model;
    }

    protected String generateDependencyTable(InitializrMetadata metadata) {
        String[][] dependencyTable = new String[metadata.getDependencies().getAll().size() + 1][];
        dependencyTable[0] = new String[] { "Id", "Description", "Required version" };
        int i = 1;
        for (Dependency dep : metadata.getDependencies().getAll().stream().sorted(Comparator.comparing(MetadataElement::getId)).toList()) {
            String[] data = new String[3];
            data[0] = dep.getId();
            data[1] = (dep.getDescription() != null) ? dep.getDescription() : dep.getName();
            data[2] = dep.getVersionRequirement();
            dependencyTable[i++] = data;
        }
        return TableGenerator.generate(dependencyTable, this.maxColumnWidth);
    }

    protected String generateTypeTable(InitializrMetadata metadata, String linkHeader, boolean addTags) {
        String[][] typeTable = new String[metadata.getTypes().getContent().size() + 1][];
        if (addTags) {
            typeTable[0] = new String[] { linkHeader, "Description", "Tags" };
        } else {
            typeTable[0] = new String[] { linkHeader, "Description" };
        }
        int i = 1;
        for (Type type : metadata.getTypes().getContent().stream().sorted(Comparator.comparing(MetadataElement::getId)).toList()) {
            String[] data = new String[typeTable[0].length];
            data[0] = (type.isDefault() ? type.getId() + " *" : type.getId());
            data[1] = (type.getDescription() != null) ? type.getDescription() : type.getName();
            if (addTags) {
                data[2] = buildTagRepresentation(type);
            }
            typeTable[i++] = data;
        }
        return TableGenerator.generate(typeTable, this.maxColumnWidth);
    }

    protected Map<String, Object> buildParametersDescription(InitializrMetadata metadata) {
        Map<String, Object> result = new LinkedHashMap<>();
        BeanWrapperImpl wrapper = new BeanWrapperImpl(metadata);
        for (PropertyDescriptor descriptor : wrapper.getPropertyDescriptors()) {
            Object value = wrapper.getPropertyValue(descriptor.getName());
            BeanWrapperImpl nested = new BeanWrapperImpl(value);
            if (nested.isReadableProperty("description") && nested.isReadableProperty("id")) {
                result.put((String) nested.getPropertyValue("id"), nested.getPropertyValue("description"));
            }
        }
        result.put("applicationName", "application name");
        result.put("baseDir", "base directory to create in the archive");
        return result;
    }

    private static String buildTagRepresentation(Type type) {
        if (type.getTags().isEmpty()) {
            return "";
        }
        return String.join(",", type.getTags().entrySet().stream().map((entry) -> entry.getKey() + ":" + entry.getValue()).toArray(String[]::new));
    }

    /**
     * Utility to generate a text table.
     */
    private static class TableGenerator {

        /**
         * Generate a table description for the specified {@code content}.
         * <p>
         * The {@code content} is a two-dimensional array holding the rows of the table.
         * The first entry holds the header of the table.
         * @param content the table content
         * @param maxWidth the width bound for each column
         * @return the generated table
         */
        static String generate(String[][] content, int maxWidth) {
            StringBuilder sb = new StringBuilder();
            boolean emptyRow = false;
            int[] columnsLength = computeColumnsLength(content, maxWidth);
            List<List<String[]>> formattedContent = new LinkedList<>();
            for (int i = 0; i < content.length; i++) {
                List<String[]> rows = computeRow(content, i, maxWidth);
                formattedContent.add(rows);
                if (rows.size() > 1) {
                    emptyRow = true;
                }
            }
            appendTableSeparation(sb, columnsLength);
            // Headers
            appendRow(sb, formattedContent, columnsLength, 0);
            appendTableSeparation(sb, columnsLength);
            for (int i = 1; i < formattedContent.size(); i++) {
                appendRow(sb, formattedContent, columnsLength, i);
                if (emptyRow && i < content.length - 1) {
                    appendEmptyRow(sb, columnsLength);
                }
            }
            appendTableSeparation(sb, columnsLength);
            return sb.toString();
        }

        private static void appendRow(StringBuilder sb, List<List<String[]>> formattedContent, int[] columnsLength, int rowIndex) {
            List<String[]> rows = formattedContent.get(rowIndex);
            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    sb.append("| ").append(fill(row[i], columnsLength[i])).append(" ");
                }
                sb.append("|");
                sb.append(NEW_LINE);
            }
        }

        private static List<String[]> computeRow(String[][] content, int rowIndex, int maxWidth) {
            String[] line = content[rowIndex];
            return HelpFormatter.format(line, maxWidth);
        }

        private static void appendEmptyRow(StringBuilder sb, int[] columnsLength) {
            for (int columnLength : columnsLength) {
                sb.append("| ").append(fill(null, columnLength)).append(" ");
            }
            sb.append("|");
            sb.append(NEW_LINE);
        }

        private static void appendTableSeparation(StringBuilder sb, int[] headersLength) {
            for (int headerLength : headersLength) {
                sb.append("+").append(multiply("-", headerLength + 2));
            }
            sb.append("+");
            sb.append(NEW_LINE);
        }

        private static String fill(String data, int columnSize) {
            if (data == null) {
                return multiply(" ", columnSize);
            } else {
                int i = columnSize - data.length();
                return data + multiply(" ", i);
            }
        }

        private static String multiply(String value, int size) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < size; i++) {
                s.append(value);
            }
            return s.toString();
        }

        private static int[] computeColumnsLength(String[][] content, int maxWidth) {
            int count = content[0].length;
            int[] result = new int[count];
            for (int i = 0; i < count; i++) {
                result[i] = largest(content, i, maxWidth);
            }
            return result;
        }

        private static int largest(String[][] content, int column, int maxWidth) {
            int max = 0;
            for (String[] rows : content) {
                if (rows != null) {
                    String s = rows[column];
                    if (s != null && s.length() > max) {
                        max = s.length();
                    }
                }
            }
            return Math.min(max, maxWidth);
        }
    }

    private static class HelpFormatter {

        /**
         * Formats a given content to a max width.
         * @param content the content to format.
         * @param maxWidth the max width of each column
         * @return the formatted rows.
         */
        private static List<String[]> format(String[] content, int maxWidth) {
            List<String[]> columns = lineWrap(content, maxWidth);
            List<String[]> rows = new ArrayList<>();
            for (int i = 0; i < largest(columns); ++i) {
                rows.add(computeRow(columns, i));
            }
            return rows;
        }

        private static String[] computeRow(List<String[]> columns, int index) {
            String[] line = new String[columns.size()];
            int position = 0;
            for (String[] column : columns) {
                line[position] = itemOrNull(column, index);
                position++;
            }
            return line;
        }

        private static List<String[]> lineWrap(String[] content, int maxWidth) {
            List<String[]> lineWrapped = new ArrayList<>();
            for (String column : content) {
                if (column == null) {
                    lineWrapped.add(new String[0]);
                } else {
                    lineWrapped.add(WordUtils.wrap(column, maxWidth).split(NEW_LINE));
                }
            }
            return lineWrapped;
        }

        private static int largest(List<String[]> columns) {
            int max = 0;
            for (String[] column : columns) {
                if (max < column.length) {
                    max = column.length;
                }
            }
            return max;
        }

        private static String itemOrNull(String[] column, int index) {
            return (index >= column.length) ? null : column[index];
        }
    }
}
