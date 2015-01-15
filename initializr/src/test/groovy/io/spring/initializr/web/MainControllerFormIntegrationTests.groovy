/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr.web

import com.gargoylesoftware.htmlunit.WebResponse
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.spring.initializr.InitializrMetadata
import io.spring.initializr.test.ProjectAssert
import io.spring.initializr.web.support.DefaultHomePage
import io.spring.initializr.web.support.HomePage

/**
 * Form based tests for the "regular" home page.
 *
 * @author Stephane Nicoll
 */
class MainControllerFormIntegrationTests extends AbstractInitializerControllerFormIntegrationTests {

	@Override
	void createSimpleGradleProject() {
		createSimpleGradleProject('gradle-project')
	}

	@Override
	protected String homeContext() {
		'/'
	}

	@Override
	protected HomePage createHomePage(HtmlPage home) {
		new DefaultHomePage(home)
	}

	@Override
	protected ProjectAssert zipProjectAssert(HomePage page, WebResponse webResponse) {
		ProjectAssert projectAssert = super.zipProjectAssert(page, webResponse)
		// we require self contained archive by default
		String dirName = page.artifactId ?: InitializrMetadata.Defaults.DEFAULT_NAME
		projectAssert.hasBaseDir(dirName)
	}

}
