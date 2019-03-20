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

package io.spring.initializr.metadata;

/**
 * Thrown when the configuration defines invalid metadata.
 *
 * @author Stephane Nicoll
 */
@SuppressWarnings("serial")
public class InvalidInitializrMetadataException extends RuntimeException {

	public InvalidInitializrMetadataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInitializrMetadataException(String message) {
		super(message);
	}

}
