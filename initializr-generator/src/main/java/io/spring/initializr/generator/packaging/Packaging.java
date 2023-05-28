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
package io.spring.initializr.generator.packaging;

import java.util.Objects;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * Application packaging, such as a jar file or a war file.
 *
 * @author Andy Wilkinson
 */
public interface Packaging {

    String id();

    static Packaging forId(String id) {
        return SpringFactoriesLoader.loadFactories(PackagingFactory.class, Packaging.class.getClassLoader()).stream().map((factory) -> factory.createPackaging(id)).filter(Objects::nonNull).findFirst().orElseThrow(() -> new IllegalStateException("Unrecognized packaging id '" + id + "'"));
    }
}
