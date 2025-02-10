/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.web.mapper;

import java.util.Collection;
import java.util.Collections;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionRange;

import org.springframework.hateoas.TemplateVariable;

/**
 * A {@link InitializrMetadataJsonMapper} handling the metadata format for v2.2
 * <p>
 * Version 2.2 adds support for {@linkplain Version.Format#V2 SemVer version format}. Any
 * previous version formats versions to {@link Version.Format#V1}.
 *
 * @author Stephane Nicoll
 */
public class InitializrMetadataV22JsonMapper extends InitializrMetadataV21JsonMapper {

	/**
	 * Create a new instance.
	 */
	public InitializrMetadataV22JsonMapper() {
		this(Collections.emptyList());
	}

	/**
	 * Create a new instance using the additional template variables.
	 * @param additionalTemplateVariables the additional template variables
	 */
	public InitializrMetadataV22JsonMapper(Collection<? extends TemplateVariable> additionalTemplateVariables) {
		super(additionalTemplateVariables);
	}

	@Override
	protected String formatVersion(String versionId) {
		return versionId;
	}

	@Override
	protected String formatVersionRange(VersionRange versionRange) {
		return versionRange.toRangeString();
	}

}
