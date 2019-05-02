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

import io.spring.initializr.generator.buildsystem.BuildSystem;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;

/**
 * Condition that matches when a generated project will use a particular
 * {@link BuildSystem}.
 *
 * @author Andy Wilkinson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnBuildSystemCondition.class)
public @interface ConditionalOnBuildSystem {

	/**
	 * The ID of the {@link BuildSystem} that should be checked.
	 * @return the build system to check. An alias for {@link #id()}.
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * The ID of the {@link BuildSystem} that should be checked.
	 * @return the build system to check
	 */
	@AliasFor("value")
	String id() default "";

	/**
	 * The dialect of the {@link BuildSystem} that should be checked. When not specified,
	 * any dialect will be matched.
	 * @return the dialect to check, if any
	 */
	String dialect() default "";

}
