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
package io.spring.initializr.generator.spring.code;

import io.spring.initializr.generator.language.TypeDeclaration;
import org.springframework.core.Ordered;

/**
 * Callback for configuring the generated project's servlet initializer. Invoked with an
 * {@link Ordered order} of {@code 0} by default, considering overriding
 * {@link #getOrder()} to customize this behaviour.
 *
 * @param <T> type declaration that this customizer can handle
 * @author Andy Wilkinson
 */
@FunctionalInterface
public interface ServletInitializerCustomizer<T extends TypeDeclaration> extends Ordered {

    void customize(T typeDeclaration);

    @Override
    default int getOrder() {
        return 0;
    }
}
