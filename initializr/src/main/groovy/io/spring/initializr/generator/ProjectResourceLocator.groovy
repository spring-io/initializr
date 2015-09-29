/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.generator

import java.nio.charset.Charset

import org.springframework.cache.annotation.Cacheable
import org.springframework.util.ResourceUtils
import org.springframework.util.StreamUtils

/**
 * Locate project resources.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class ProjectResourceLocator {

	private static final Charset UTF_8 = Charset.forName("UTF-8")

	/**
	 * Return the binary content of the resource at the specified location.
	 * @param location a resource location
	 * @return the content of the resource
	 */
	@Cacheable("project-resources")
	byte[] getBinaryResource(String location)  {
		InputStream stream = getInputStream(location)
		try {
			return StreamUtils.copyToByteArray(stream)
		} finally {
			stream.close()
		}
	}

	/**
	 * Return the textual content of the resource at the specified location.
	 * @param location a resource location
	 * @return the content of the resource
	 */
	@Cacheable("project-resources")
	String getTextResource(String location)  {
		InputStream stream = getInputStream(location)
		try {
			return StreamUtils.copyToString(stream, UTF_8)
		} finally {
			stream.close()
		}
	}

	private InputStream getInputStream(String location) {
		def url = ResourceUtils.getURL(location)
		def stream = url.openStream()
		stream
	}

}
