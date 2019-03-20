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

package io.spring.initializr.web.autoconfigure;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/**
 * Post-process the environment to extract the service credentials provided by
 * CloudFoundry. Injects the elastic service URI if present.
 *
 * @author Stephane Nicoll
 */
public class CloudfoundryEnvironmentPostProcessor
		implements EnvironmentPostProcessor, Ordered {

	private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

	private static final int ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment,
			SpringApplication springApplication) {

		Map<String, Object> map = new LinkedHashMap<>();
		String uri = environment.getProperty("vcap.services.stats-index.credentials.uri");
		if (StringUtils.hasText(uri)) {
			map.put("initializr.stats.elastic.uri", uri);
			addOrReplace(environment.getPropertySources(), map);
		}
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	private static void addOrReplace(MutablePropertySources propertySources,
			Map<String, Object> map) {
		MapPropertySource target = null;
		if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
			PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
			if (source instanceof MapPropertySource) {
				target = (MapPropertySource) source;
				for (String key : map.keySet()) {
					if (!target.containsProperty(key)) {
						target.getSource().put(key, map.get(key));
					}
				}
			}
		}
		if (target == null) {
			target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
		}
		if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
			propertySources.addLast(target);
		}
	}

}
