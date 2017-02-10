/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.web.support;

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.metadata.DefaultMetadataElement;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.web.client.RestTemplate;

/**
 * Reads metadata from the main spring.io website. This is a stateful service: create a
 * new instance whenever you need to refresh the content.
 *
 * @author Stephane Nicoll
 */
public class SpringBootMetadataReader {

	private final JSONObject content;

	/**
	 * Parse the content of the metadata at the specified url
	 */
	public SpringBootMetadataReader(RestTemplate restTemplate, String url) {
		this.content = new JSONObject(restTemplate.getForObject(url, String.class));
	}

	/**
	 * Return the boot versions parsed by this instance.
	 */
	public List<DefaultMetadataElement> getBootVersions() {
		JSONArray array = content.getJSONArray("projectReleases");
		List<DefaultMetadataElement> list = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject it = array.getJSONObject(i);
			DefaultMetadataElement version = new DefaultMetadataElement();
			version.setId(it.getString("version"));
			String name = it.getString("versionDisplayName");
			version.setName(it.getBoolean("snapshot") ? name + " (SNAPSHOT)" : name);
			version.setDefault(it.getBoolean("current"));
			list.add(version);
		}
		return list;
	}

}
