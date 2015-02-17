/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.test

import geb.Page

/**
 * A {@link Page} representing the home page of the application.
 *
 * @author Stephane Nicoll
 * @since 1.0
 */
class HomePage extends Page {

	static at = { title == 'Spring Initializr' }
	static content = {
		groupId { $('form').groupId() }
		artifactId { $('form').artifactId() }
		name { $('form').name() }
		description { $('form').description() }
		packageName { $('form').packageName() }
		type { $('form').type() }
		packaging { $('form').packaging() }
		javaVersion { $('form').javaVersion() }
		language { $('form').language() }

		dependency { id ->
			$("form").find('input', type: "checkbox", name: "style", value: id).click()
		}
		generateProject { $('form').find('button', name: 'generate-project') }

	}

}
