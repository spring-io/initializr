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
package io.spring.initializr.generator.language.kotlin;

import io.spring.initializr.generator.language.AbstractLanguage;
import io.spring.initializr.generator.language.Language;

/**
 * Kotlin {@link Language}.
 *
 * @author Stephane Nicoll
 */
public final class KotlinLanguage extends AbstractLanguage {

    /**
     * Kotlin {@link Language} identifier.
     */
    public static final String ID = "kotlin";

    public KotlinLanguage() {
        this(DEFAULT_JVM_VERSION);
    }

    public KotlinLanguage(String jvmVersion) {
        super(ID, jvmVersion, "kt");
    }
}
