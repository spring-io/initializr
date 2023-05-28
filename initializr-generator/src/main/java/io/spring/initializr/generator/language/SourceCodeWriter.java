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
package io.spring.initializr.generator.language;

import java.io.IOException;

/**
 * A writer for some {@link SourceCode}.
 *
 * @param <S> the type of source code that can be written by this writer
 * @author Andy Wilkinson
 */
public interface SourceCodeWriter<S extends SourceCode<?, ?>> {

    /**
     * Write, to the given {@code structure}, the given {@code sourceCode}.
     * @param structure the {@link SourceStructure} beneath which the source code is
     * written
     * @param sourceCode the source code to write
     * @throws IOException if writing fails
     */
    void writeTo(SourceStructure structure, S sourceCode) throws IOException;
}
