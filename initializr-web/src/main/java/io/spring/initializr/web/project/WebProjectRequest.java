/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.web.project;

import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.beans.BeanWrapperImpl;

/**
 * A {@link ProjectRequest} with some additional information to identify the request.
 *
 * @author Madhura Bhave
 */
public class WebProjectRequest extends ProjectRequest {

	private final Map<String, Object> parameters = new LinkedHashMap<>();

	/**
	 * Return the additional parameters that can be used to further identify the request.
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return this.parameters;
	}

	/**
	 * Initialize the state of this request with defaults defined in the
	 * {@link InitializrMetadata metadata}.
	 * @param metadata the metadata to use
	 */
	public void initialize(InitializrMetadata metadata) {
		BeanWrapperImpl bean = new BeanWrapperImpl(this);
		metadata.defaults().forEach((key, value) -> {
			if (bean.isWritableProperty(key)) {
				// We want to be able to infer a package name if none has been
				// explicitly set
				if (!key.equals("packageName")) {
					bean.setPropertyValue(key, value);
				}
			}
		});
	}

}
