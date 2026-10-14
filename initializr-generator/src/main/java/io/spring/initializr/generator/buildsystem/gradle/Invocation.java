/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.List;

/**
 * An invocation of a method.
 *
 * @author Moritz Halbritter
 * @author Stephane Nicoll
 */
public class Invocation {

	private final String target;

	private final List<String> arguments;

	/**
	 * Creates a new instance.
	 * @param target the target
	 * @param arguments the arguments
	 */
	public Invocation(String target, List<String> arguments) {
		this.target = target;
		this.arguments = List.copyOf(arguments);
	}

	/**
	 * Return the name of the method.
	 * @return the method name
	 */
	public String getTarget() {
		return this.target;
	}

	/**
	 * Return the arguments (can be empty).
	 * @return the method arguments
	 */
	public List<String> getArguments() {
		return this.arguments;
	}

}
