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

package io.spring.initializr.web

import geb.Browser
import io.spring.initializr.web.test.HomePage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile

import org.springframework.test.context.ActiveProfiles

import static org.junit.Assert.assertTrue

/**
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class ProjectGenerationSmokeTests extends AbstractInitializrControllerIntegrationTests {

	private File downloadDir
	private WebDriver driver
	private Browser browser

	@Before
	void setup() {
		downloadDir = folder.newFolder()
		FirefoxProfile fxProfile = new FirefoxProfile();

		fxProfile.setPreference("browser.download.folderList", 2);
		fxProfile.setPreference("browser.download.manager.showWhenStarting", false);
		fxProfile.setPreference("browser.download.dir", downloadDir.getAbsolutePath());
		fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/zip,application/x-compress,application/octet-stream");

		driver = new FirefoxDriver(fxProfile);
		browser = new Browser()
		browser.driver = driver
	}

	@After
	void destroy() {
		if (driver != null) {
			driver.close();
		}
	}

	@Test
	void createDefaultJavaProject() {
		toHome {
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir("demo").isMavenProject().isJavaProject()
					.hasStaticAndTemplatesResources(false)
					.pomAssert().hasDependenciesCount(2)
					.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
		}
	}

	@Test
	void createDefaultGroovyProject() {
		toHome {
			page.language = 'groovy'
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir('demo').isMavenProject().isGroovyProject()
					.hasStaticAndTemplatesResources(false)
					.pomAssert().hasDependenciesCount(3)
					.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
					.hasDependency('org.codehaus.groovy', 'groovy')
		}
	}


	@Test
	void createJavaProjectWithCustomDefaults() {
		toHome {
			page.groupId = 'com.acme'
			page.artifactId = 'foo-bar'
			page.name = 'My project'
			page.description = 'A description for my project'
			page.dependency('web').click()
			page.dependency('data-jpa').click()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('foo-bar.zip'))
			projectAssert.hasBaseDir("foo-bar").isMavenProject()
					.isJavaProject('MyProjectApplication')
					.hasStaticAndTemplatesResources(true)
					.pomAssert().hasGroupId('com.acme').hasArtifactId('foo-bar')
					.hasName('My project').hasDescription('A description for my project')
					.hasSpringBootStarterDependency('web')
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterTest()
		}
	}

	@Test
	void createGroovyProjectWithCustomDefaults() {
		toHome {
			page.language = 'groovy'
			page.groupId = 'org.biz'
			page.artifactId = 'groovy-project'
			page.name = 'My Groovy project'
			page.description = 'A description for my Groovy project'
			page.dependency('web').click()
			page.dependency('data-jpa').click()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('groovy-project.zip'))
			projectAssert.hasBaseDir("groovy-project").isMavenProject()
					.isGroovyProject('MyGroovyProjectApplication')
					.hasStaticAndTemplatesResources(true)
					.pomAssert().hasGroupId('org.biz').hasArtifactId('groovy-project')
					.hasName('My Groovy project').hasDescription('A description for my Groovy project')
					.hasSpringBootStarterDependency('web')
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterTest()
					.hasDependency('org.codehaus.groovy', 'groovy')
		}
	}

	@Test
	void createSimpleGradleProject() {
		toHome {
			page.type = 'gradle-project'
			page.dependency('data-jpa').click()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir("demo").isGradleProject()
					.isJavaProject()
					.hasStaticAndTemplatesResources(false)
		}
	}

	@Test
	void createWarProject() {
		toHome {
			page.packaging = 'war'
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir("demo").isMavenProject()
					.isJavaWarProject()
					.pomAssert().hasPackaging('war').hasDependenciesCount(3)
					.hasSpringBootStarterDependency('web') // Added with war packaging
					.hasSpringBootStarterTomcat()
					.hasSpringBootStarterTest()
		}
	}

	@Test
	void dependencyHiddenAccordingToRange() {
		toHome { // bur: [1.1.4.RELEASE,1.2.0.BUILD-SNAPSHOT)
			page.dependency('org.acme:bur').displayed == true

			page.bootVersion = '1.0.2.RELEASE'
			page.dependency('org.acme:bur').displayed == false
			page.dependency('org.acme:biz').displayed == false
			page.bootVersion = '1.1.4.RELEASE'
			page.dependency('org.acme:bur').displayed == true
			page.dependency('org.acme:biz').displayed == false
			page.bootVersion = '1.2.0.BUILD-SNAPSHOT'
			page.dependency('org.acme:bur').displayed == false
			page.dependency('org.acme:biz').displayed == true
		}
	}

	@Test
	void dependencyUncheckedWhenHidden() {
		toHome {
			page.dependency('org.acme:bur').value() == 'org.acme:bur'
			page.bootVersion = '1.0.2.RELEASE'
			page.dependency('org.acme:bur').displayed == false
			page.bootVersion = '1.1.4.RELEASE'
			page.dependency('org.acme:bur').displayed == true
			page.dependency('org.acme:bur').value() == false
		}
	}

	private Browser toHome(Closure script) {
		browser.go("http://localhost:" + port + "/")
		browser.at HomePage
		script.delegate = browser
		script()
		browser
	}

	private byte[] from(String fileName) {
		getArchive(fileName).bytes
	}

	private File getArchive(String fileName) {
		File archive = new File(downloadDir, fileName)
		assertTrue "Expected content with name $fileName", archive.exists()
		archive
	}

}
