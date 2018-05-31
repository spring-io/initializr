/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.web.test;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.restdocs.RestDocumentationContext;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.snippet.TemplatedSnippet;
import org.springframework.restdocs.snippet.WriterResolver;
import org.springframework.restdocs.templates.TemplateEngine;

/**
 * Creates a separate snippet for a single field in a larger payload. The output comes in
 * a sub-directory ("response-fields") of one containing the request and response
 * snippets, with a file name the same as the path. An exception to the last rule is if
 * you pick a single array element by using a path like `foo.bar[0]`, the snippet file
 * name is then just the array name (because asciidoctor cannot import snippets with
 * brackets in the name).
 *
 * @author Dave Syer
 */
public class ResponseFieldSnippet extends TemplatedSnippet {

	private final String path;

	private final JsonFieldProcessor fieldProcessor = new JsonFieldProcessor();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Integer index;

	private final String file;

	public ResponseFieldSnippet(String path) {
		super("response-fields", Collections.emptyMap());
		String file = path;
		if (path.endsWith("]")) {
			// In this project we actually only need snippets whose last segment is an
			// array index, so we can deal with it as a special case here. Ideally the
			// restdocs implementation of JsonField would support this use case as well.
			String index = path.substring(path.lastIndexOf("[") + 1);
			index = index.substring(0, index.length() - 1);
			this.index = Integer.valueOf(index);
			path = path.substring(0, path.lastIndexOf("["));
			file = file.replace("]", "").replace("[", ".");
		}
		else {
			this.index = null;
		}
		this.file = file;
		this.path = path;
		this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	/*
	 * Copy of super class method, but changes the path of the output file to include the
	 * path
	 */
	@Override
	public void document(Operation operation) throws IOException {
		RestDocumentationContext context = (RestDocumentationContext) operation
				.getAttributes().get(RestDocumentationContext.class.getName());
		WriterResolver writerResolver = (WriterResolver) operation.getAttributes()
				.get(WriterResolver.class.getName());
		try (Writer writer = writerResolver.resolve(
				operation.getName() + "/" + getSnippetName(), this.file, context)) {
			Map<String, Object> model = createModel(operation);
			model.putAll(getAttributes());
			TemplateEngine templateEngine = (TemplateEngine) operation.getAttributes()
					.get(TemplateEngine.class.getName());
			writer.append(templateEngine.compileTemplate(getSnippetName()).render(model));
		}
	}

	@Override
	protected Map<String, Object> createModel(Operation operation) {
		try {
			Object object = this.objectMapper.readValue(
					operation.getResponse().getContentAsString(), Object.class);
			Object field = this.fieldProcessor.extract(JsonFieldPath.compile(this.path),
					object);
			if (field instanceof List && this.index != null) {
				field = ((List<?>) field).get(this.index);
			}
			return Collections.singletonMap("value",
					this.objectMapper.writeValueAsString(field));
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

}
