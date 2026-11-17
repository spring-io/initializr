/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
