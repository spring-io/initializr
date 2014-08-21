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

package io.spring.initializr.web.support

import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.html.*
import io.spring.initializr.support.ProjectAssert

/**
 * Represent the home page of the service.
 *
 * @author Stephane Nicoll
 */
class HomePage {

	String groupId
	String artifactId
	String name
	String description
	String packageName
	String type
	String packaging
	List<String> dependencies = []

	private final HtmlPage page

	HomePage(HtmlPage page) {
		this.page = page
	}

	/**
	 * Generate a project using the specified temporary directory. Return
	 * the {@link ProjectAssert} instance.
	 * @see org.junit.rules.TemporaryFolder
	 */
	byte[] generateProject() {
		setup()
		HtmlButton submit = page.getElementByName('generate-project')
		Page newMessagePage = submit.click();
		newMessagePage.webResponse.contentAsStream.bytes
	}

	/**
	 * Setup the {@link HtmlPage} with the customization of this
	 * instance. Only applied when a non-null value is set
	 */
	private void setup() {
		setTextValue('groupId', groupId)
		setTextValue('artifactId', artifactId)
		setTextValue('name', name)
		setTextValue('description', description)
		setTextValue('packageName', packageName)
		select('type', type)
		select('packaging', packaging)
		selectDependencies(dependencies)
	}

	private void setTextValue(String elementId, String value) {
		if (value != null) {
			HtmlTextInput input = page.getHtmlElementById(elementId)
			input.setValueAttribute(value)
		}
	}

	private void select(String selectId, String value) {
		if (value != null) {
			HtmlSelect input = page.getHtmlElementById(selectId)
			input.setSelectedAttribute(value, true)
		}
	}

	private void selectDependencies(List<String> dependencies) {
		List<DomElement> styles = page.getElementsByName("style")
		Map<String, HtmlCheckBoxInput> allStyles = new HashMap<>()
		for (HtmlCheckBoxInput checkBoxInput : styles) {
			allStyles.put(checkBoxInput.getValueAttribute(), checkBoxInput)
		}
		for (String dependency : dependencies) {
			HtmlCheckBoxInput checkBox = allStyles.get(dependency)
			if (checkBox != null) {
				checkBox.checked = true
			} else {
				throw new IllegalArgumentException('No dependency with name '
						+ dependency + ' was found amongst ' + allStyles.keySet());
			}
		}
	}

}
