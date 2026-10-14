/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import java.util.List;

/**
 * Commons settings for Kotlin projects.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public interface KotlinProjectSettings {

	/**
	 * Return the version of Kotlin to use.
	 * @return the kotlin version
	 */
	String getVersion();

	/**
	 * Return the {@code jvmTarget} to use.
	 * @return the jvm target
	 */
	String getJvmTarget();

	/**
	 * Return the compiler arguments.
	 * @return the compiler arguments
	 */
	default List<String> getCompilerArgs() {
		return List.of("-Xjsr305=strict");
	}

}
