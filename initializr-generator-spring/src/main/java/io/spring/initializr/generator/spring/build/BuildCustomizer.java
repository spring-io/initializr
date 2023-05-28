/*
 * Copyright 2012-2022 the original author or authors.
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
package io.spring.initializr.generator.spring.build;

import java.util.function.Consumer;
import io.spring.initializr.generator.buildsystem.Build;
import org.springframework.core.Ordered;

/**
 * Callback for customizing a project's {@link Build}. Invoked with an {@link Ordered
 * order} of {@code 0} by default, considering overriding {@link #getOrder()} to customize
 * this behaviour.
 *
 * @param <B> {@link Build} type handled by this customizer
 * @author Andy Wilkinson
 */
@FunctionalInterface
public interface BuildCustomizer<B extends Build> extends Ordered {

    void customize(B build);

    @Override
    default int getOrder() {
        return 0;
    }

    /**
     * Create a {@link BuildCustomizer} that is ordered with the specified {@code order}
     * value.
     * @param order the order of the customizer
     * @param customizer the customizer
     * @param <B> the type of the build
     * @return a {@link BuildCustomizer} with the specified order
     */
    static <B extends Build> BuildCustomizer<B> ordered(int order, Consumer<B> customizer) {
        return new OrderedBuildCustomizer<>(order, customizer);
    }

    class OrderedBuildCustomizer<B extends Build> implements BuildCustomizer<B> {

        private final int order;

        private final Consumer<B> customizer;

        public OrderedBuildCustomizer(int order, Consumer<B> customizer) {
            this.order = order;
            this.customizer = customizer;
        }

        @Override
        public void customize(B build) {
            this.customizer.accept(build);
        }

        @Override
        public int getOrder() {
            return this.order;
        }
    }
}
