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

package io.spring.initializr.versionresolver;

import java.nio.file.Path;
import java.util.Map;

/**
 * A {@code MavenVersionResolver} is used to resolve the versions of managed dependencies
 * or plugins. Implementations must be thread-safe.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public interface MavenVersionResolver {

	/**
	 * Resolves the versions in the managed dependencies of the bom identified by the
	 * given {@code groupId}, {@code artifactId}, and {@code version}.
	 * @param groupId bom group ID
	 * @param artifactId bom artifact ID
	 * @param version bom version
	 * @return the managed dependencies as a map of {@code groupId:artifactId} to
	 * {@code version}
	 */
	Map<String, String> resolveDependencies(String groupId, String artifactId, String version);

	/**
	 * Resolves the versions in the managed plugins of the pom identified by the given
	 * {@code groupId}, {@code artifactId}, and {@code version}.
	 * @param groupId pom group ID
	 * @param artifactId pom artifact ID
	 * @param version pom version
	 * @return the managed plugins as a map of {@code groupId:artifactId} to
	 * {@code version}
	 */
	Map<String, String> resolvePlugins(String groupId, String artifactId, String version);

	/**
	 * Creates a new {@code MavenVersionResolver} that uses the given {@code location} for
	 * its local cache. To avoid multiple instances attempting to write to the same
	 * location cache, callers should ensure that a unique location is used. The returned
	 * resolver can then be used concurrently by multiple threads.
	 * @param location cache location
	 * @return the resolver
	 */
	static MavenVersionResolver withCacheLocation(Path location) {
		return new DefaultMavenVersionResolver(location);
	}

}
