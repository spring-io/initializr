/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

/**
 * A representation of something that can be annotated.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public interface Annotatable {

	/**
	 * Return the {@link AnnotationContainer} to use to configure the annotations of this
	 * element.
	 * @return the annotation container
	 */
	AnnotationContainer annotations();

}
