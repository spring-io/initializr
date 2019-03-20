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

package io.spring.initializr.generator.packaging;

/**
 * A factory for creating a {@link Packaging}.
 *
 * @author Andy Wilkinson
 */
public interface PackagingFactory {

	/**
	 * Creates and returns a {@link Packaging} for the given id. If the factory does not
	 * recognise the given {@code id}, {@code null} should be returned.
	 * @param id the id of the packaging
	 * @return the packaging or {@code null}
	 */
	Packaging createPackaging(String id);

}
