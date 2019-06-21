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

package io.spring.initializr.versionresolver;

import java.nio.file.Path;
import java.util.Map;

/**
 * A {@code DependencyManagementVersionResolver} is used to resolve the versions in the
 * managed dependencies of a Maven bom. Implementations must be thread-safe.
 *
 * @author Andy Wilkinson
 */
public interface DependencyManagementVersionResolver {

	/**
	 * Resolves the versions in the managed dependencies of the bom identified by the
	 * given {@code groupId}, {@code artifactId}, and {@code version}.
	 * @param groupId bom group ID
	 * @param artifactId bom artifact ID
	 * @param version bom version
	 * @return the managed dependencies as a map of {@code groupId:artifactId} to
	 * {@code version}
	 */
	Map<String, String> resolve(String groupId, String artifactId, String version);

	/**
	 * Creates a new {@code DependencyManagementVersionResolver} that uses the given
	 * {@code location} for its local cache. To avoid multiple instances attempting to
	 * write to the same location cache, callers should ensure that a unique location is
	 * used. The returned resolver can then be used concurrently by multiple threads.
	 * @param location cache location
	 * @return the resolver
	 */
	static DependencyManagementVersionResolver withCacheLocation(Path location) {
		return new MavenResolverDependencyManagementVersionResolver(location);
	}

}
