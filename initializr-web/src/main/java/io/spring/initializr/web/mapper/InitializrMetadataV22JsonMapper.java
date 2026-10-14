/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.mapper;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionRange;

/**
 * A {@link InitializrMetadataJsonMapper} handling the metadata format for v2.2
 * <p>
 * Version 2.2 adds support for {@linkplain Version.Format#V2 SemVer version format}. Any
 * previous version formats versions to {@link Version.Format#V1}.
 *
 * @author Stephane Nicoll
 */
public class InitializrMetadataV22JsonMapper extends InitializrMetadataV21JsonMapper {

	@Override
	protected String formatVersion(String versionId) {
		return versionId;
	}

	@Override
	protected String formatVersionRange(VersionRange versionRange) {
		return versionRange.toRangeString();
	}

}
