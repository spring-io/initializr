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

package io.spring.initializr.generator.spring.container.dockercompose;

/**
 * Helper class for {@link DockerComposeService}.
 *
 * @author Moritz Halbritter
 */
final class DockerComposeServiceHelper {

	private DockerComposeServiceHelper() {
	}

	/**
	 * Creates a new {@link DockerComposeService}.
	 * @return a new {@link DockerComposeService}
	 */
	static DockerComposeService service() {
		return service(1);
	}

	/**
	 * Creates a new {@link DockerComposeService} with the given suffix.
	 * @param suffix the suffix
	 * @return a new {@link DockerComposeService}
	 */
	static DockerComposeService service(int suffix) {
		return DockerComposeService.withImage("image-" + suffix, "image-tag-" + suffix)
			.name("service-" + suffix)
			.imageWebsite("https://service-" + suffix + ".org/")
			.build();
	}

}
