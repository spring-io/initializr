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

package io.spring.initializr.generator.condition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.spring.initializr.generator.project.ResolvedProjectDescription;

import org.springframework.context.annotation.Conditional;

/**
 * Condition that matches when a {@link ResolvedProjectDescription} defines a particular
 * dependency. A generated project may ultimately define a different set of dependencies
 * according to the contributors that have been executed. To contribute to the project
 * according to the real set, prefer querying the model itself rather than using this
 * condition.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnRequestedDependencyCondition.class)
public @interface ConditionalOnRequestedDependency {

	/**
	 * The identifier of the dependency.
	 * @return the dependency ID
	 */
	String value();

}
