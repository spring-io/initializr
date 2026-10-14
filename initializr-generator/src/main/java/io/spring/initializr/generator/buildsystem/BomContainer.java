/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

/**
 * A {@link BuildItemContainer} implementation for boms.
 *
 * @author Stephane Nicoll
 */
public class BomContainer extends BuildItemContainer<String, BillOfMaterials> {

	/**
	 * Create an instance with the specified {@code itemResolver}.
	 * @param itemResolver the function that returns a {@link BillOfMaterials} based on an
	 * identifier.
	 */
	public BomContainer(Function<String, @Nullable BillOfMaterials> itemResolver) {
		super(new LinkedHashMap<>(), itemResolver);
	}

	/**
	 * Register a {@link BillOfMaterials} with the specified {@code id} and a
	 * {@linkplain BillOfMaterials.Builder state}.
	 * @param id the id of the bom
	 * @param builder the state of the bom
	 */
	public void add(String id, BillOfMaterials.Builder builder) {
		add(id, builder.build());
	}

}
