/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.kotlin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;
import org.jspecify.annotations.Nullable;

/**
 * Commons settings for Kotlin projects.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
public class SimpleKotlinProjectSettings implements KotlinProjectSettings {

	private static final VersionRange KOTLIN_2_2_AND_2_3 = VersionParser.DEFAULT.parseRange("[2.2.0,2.4.0)");

	private final String version;

	private final String jvmTarget;

	private final List<KotlinCompilerArgsCustomizer> compilerArgsCustomizers;

	private @Nullable List<String> compilerArgs;

	/**
	 * Create an instance with the kotlin version to use.
	 * @param version the kotlin version to use
	 */
	public SimpleKotlinProjectSettings(String version) {
		this(version, Language.DEFAULT_JVM_VERSION);
	}

	/**
	 * Create an instance with the kotlin version and the target version of the generated
	 * JVM bytecode.
	 * @param version the kotlin version to use
	 * @param jvmTarget the target version of the generated JVM bytecode
	 */
	public SimpleKotlinProjectSettings(String version, String jvmTarget) {
		this(version, jvmTarget, Collections.emptyList());
	}

	/**
	 * Create an instance with the kotlin version, the target version of the generated JVM
	 * bytecode and the customizers to apply to the compiler arguments.
	 * @param version the kotlin version to use
	 * @param jvmTarget the target version of the generated JVM bytecode
	 * @param compilerArgsCustomizers the customizers to apply to the compiler arguments,
	 * in order
	 */
	public SimpleKotlinProjectSettings(String version, String jvmTarget,
			List<KotlinCompilerArgsCustomizer> compilerArgsCustomizers) {
		this.version = version;
		this.jvmTarget = jvmTarget;
		this.compilerArgsCustomizers = List.copyOf(compilerArgsCustomizers);
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public String getJvmTarget() {
		return this.jvmTarget;
	}

	@Override
	public List<String> getCompilerArgs() {
		if (this.compilerArgs == null) {
			this.compilerArgs = computeCompilerArgs();
		}
		return this.compilerArgs;
	}

	private List<String> computeCompilerArgs() {
		List<String> result = new ArrayList<>(KotlinProjectSettings.super.getCompilerArgs());
		Version kotlinVersion = Version.parse(this.version);
		if (KOTLIN_2_2_AND_2_3.match(kotlinVersion)) {
			result.add("-Xannotation-default-target=param-property");
		}
		this.compilerArgsCustomizers.forEach((customizer) -> customizer.customize(this.version, result));
		return List.copyOf(result);
	}

}
