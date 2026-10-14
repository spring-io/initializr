/*
 * Copyright 2012 - present the original author or authors.
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

	/**
	 * Customize the given build.
	 * @param build the build to customize
	 */
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
