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

package io.spring.initializr.metadata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * A single select capability whose values can be parsed as versions.
 *
 * @author Sijun Yang
 */
public class VersionCapability extends SingleSelectCapability {

	@JsonCreator
	VersionCapability(@JsonProperty("id") String id) {
		this(id, null, null);
	}

	public VersionCapability(String id, @Nullable String title, @Nullable String description) {
		super(id, title, description);
	}

	public Version parseVersion(String text) {
		return getVersionParser().parseLatest(text);
	}

	public @Nullable Version safeParseVersion(@Nullable String text) {
		if (text == null) {
			return null;
		}
		return getVersionParser().safeParseLatest(text);
	}

	protected VersionParser getVersionParser() {
		List<Version> versions = getContent().stream().map((it) -> {
			String id = it.getId();
			Assert.state(id != null, "'id' must not be null");
			return Version.parse(id);
		}).toList();
		return new VersionParser(versions);
	}

}
