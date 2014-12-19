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

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.WebRequest
import com.gargoylesoftware.htmlunit.WebResponse
import com.gargoylesoftware.htmlunit.html.HtmlPage
import io.spring.initializr.support.PomAssert
import io.spring.initializr.support.ProjectAssert
import io.spring.initializr.web.support.HomePage
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import static org.junit.Assert.assertEquals

/**
 * Integration tests that are actually using the HTML page to request new
 * projects. Used to test both the default home page and the legacy one
 * used by STS.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
abstract class AbstractInitializerControllerFormIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Autowired
	private WebApplicationContext context

	private WebClient webClient

	@Before
	void setup() {
		def mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build()
		webClient = new WebClient()
		webClient.webConnection = new MockMvcWebConnection(mockMvc, '')
	}

	@After
	void cleanup() {
		this.webClient.closeAllWindows()
	}

	@Test
	void createDefaultJavaProject() {
		def page = home()
		def projectAssert = zipProjectAssert(page.generateProject().contentAsStream.bytes)
		projectAssert.isMavenProject().isJavaProject().hasStaticAndTemplatesResources(false)
				.pomAssert().hasDependenciesCount(2)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterDependency('test')
	}

	@Test
	void createDefaultGroovyProject() {
		def page = home()
		page.language = 'groovy'
		def projectAssert = zipProjectAssert(page.generateProject().contentAsStream.bytes)
		projectAssert.isMavenProject().isGroovyProject().hasStaticAndTemplatesResources(false)
				.pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterDependency('test')
				.hasDependency('org.codehaus.groovy', 'groovy')
	}

	@Test
	void createJavaProjectWithCustomDefaults() {
		def page = createCustomPage()

		WebResponse webResponse = page.generateProject()
		String value = webResponse.getResponseHeaderValue('Content-Disposition')
		assertEquals  'attachment; filename="foo-bar.zip"', value

		def projectAssert = zipProjectAssert(webResponse)
		projectAssert.isMavenProject().isJavaProject('MyProjectApplication')
				.hasStaticAndTemplatesResources(true)

		assertMavenProject(projectAssert.pomAssert())
	}

	@Test
	void createGroovyProjectWithCustomDefaults() {
		def page = createCustomPage()
		page.language = 'groovy'

		WebResponse webResponse = page.generateProject()
		String value = webResponse.getResponseHeaderValue('Content-Disposition')
		assertEquals  'attachment; filename="foo-bar.zip"', value

		def projectAssert = zipProjectAssert(webResponse)
		projectAssert.isMavenProject().isGroovyProject('MyProjectApplication')
				.hasStaticAndTemplatesResources(true)

		assertMavenProject(projectAssert.pomAssert())
				.hasDependency('org.codehaus.groovy', 'groovy')
	}

	private def createCustomPage() {
		def page = home()
		page.groupId = 'com.acme'
		page.artifactId = 'foo-bar'
		page.name = 'My project'
		page.description = 'A description for my project'
		page.dependencies << 'web' << 'data-jpa'
		page
	}

	private PomAssert assertMavenProject(PomAssert pomAssert) {
		pomAssert.hasGroupId('com.acme').hasArtifactId('foo-bar')
				.hasName('My project').hasDescription('A description for my project')
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('test')
	}

	@Test
	abstract void createSimpleGradleProject()

	@Test
	void createWarProject() {
		def page = home()
		page.packaging = 'war'
		def projectAssert = zipProjectAssert(page.generateProject())
		projectAssert.isMavenProject().isJavaWarProject()
				.pomAssert().hasPackaging('war').hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web') // Added with war packaging
				.hasSpringBootStarterDependency('tomcat')
				.hasSpringBootStarterDependency('test')
	}

	void createSimpleGradleProject(String projectType) {
		def page = home()
		page.type =  projectType
		page.dependencies << 'data-jpa'
		def projectAssert = zipProjectAssert(page.generateProject())
		projectAssert.isGradleProject().isJavaProject().hasStaticAndTemplatesResources(false)
	}

	HomePage home() {
		def request = new WebRequest(new URL("http://localhost${homeContext()}"), 'text/html')
		def home = webClient.getPage(request)
		createHomePage(home)
	}

	ProjectAssert zipProjectAssert(WebResponse webResponse) {
		zipProjectAssert(webResponse.contentAsStream.bytes)
	}

	/**
	 * Provide the context of the home page
	 */
	protected abstract String homeContext()

	/**
	 * Create a {@link HomePage} instance based on the specified {@link HtmlPage}
	 */
	protected abstract HomePage createHomePage(HtmlPage home)

}
