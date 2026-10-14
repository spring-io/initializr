/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.kotlin;

/**
 * Basic modifiers for Kotlin.
 *
 * @author Stephane Nicoll
 */
public enum KotlinModifier {

	/**
	 * Visible to anyone.
	 */
	PUBLIC,

	/**
	 * Visible inside that class and any subclass.
	 */
	PROTECTED,

	/**
	 * Visible inside this class only.
	 */
	PRIVATE,

	/**
	 * Final modifier.
	 */
	FINAL,

	/**
	 * Allow to override a member.
	 */
	OPEN,

	/**
	 * Declare a member without an implementation (infers open).
	 */
	ABSTRACT,

	/**
	 * Override a member.
	 */
	OVERRIDE,

	/**
	 * Declare a late-initialized property.
	 */
	LATEINIT

}
