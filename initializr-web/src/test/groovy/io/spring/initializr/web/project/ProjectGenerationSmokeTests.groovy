/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.web.project

import geb.Browser
import io.spring.initializr.test.generator.ProjectAssert
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests
import io.spring.initializr.web.project.test.HomePage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.interactions.Actions

import org.springframework.test.context.ActiveProfiles

import static org.junit.Assert.assertEquals
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
	private Actions actions

	private def enterAction

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
		actions = new Actions(driver)
		browser = new Browser()
		browser.driver = driver

		enterAction = actions.sendKeys(Keys.ENTER).build()
	}

	@After
	void destroy() {
		if (driver != null) {
			driver.close();
		}
	}

	@Test
	void createSimpleProject() {
		toHome {
			page.generateProject.click()
			at HomePage
			assertSimpleProject()
					.isMavenProject()
					.pomAssert()
					.hasDependenciesCount(2)
					.hasSpringBootStarterRootDependency()
					.hasSpringBootStarterTest()
		}
	}

	@Test
	void createSimpleProjectWithGradle() {
		toHome {
			page.type = 'gradle-project'
			page.generateProject.click()
			at HomePage
			assertSimpleProject()
					.isGradleProject()
					.gradleBuildAssert()
					.contains("compile('org.springframework.boot:spring-boot-starter')")
					.contains("testCompile('org.springframework.boot:spring-boot-starter-test')")
		}
	}

	@Test
	void createSimpleProjectWithDifferentBootVersion() {
		toHome {
			page.bootVersion = '1.0.2.RELEASE'
			page.generateProject.click()
			at HomePage
			assertSimpleProject()
					.isMavenProject()
					.pomAssert()
					.hasBootVersion('1.0.2.RELEASE')
					.hasDependenciesCount(2)
					.hasSpringBootStarterRootDependency()
					.hasSpringBootStarterTest()

		}
	}

	@Test
	void createSimpleProjectWithDependencies() {
		toHome {
			selectDependency(page, 'Data JPA')
			selectDependency(page, 'Security')
			page.generateProject.click()
			at HomePage
			assertSimpleProject()
					.isMavenProject()
					.pomAssert()
					.hasDependenciesCount(3)
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterDependency('security')
					.hasSpringBootStarterTest()
		}
	}

	@Test
	void selectDependencyTwiceRemovesIt() {
		toHome {
			selectDependency(page, 'Data JPA')
			selectDependency(page, 'Security')
			selectDependency(page, 'Security') // remove
			page.generateProject.click()
			at HomePage
			assertSimpleProject()
					.isMavenProject()
					.pomAssert()
					.hasDependenciesCount(2)
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterTest()
		}
	}

	ProjectAssert assertSimpleProject() {
		zipProjectAssert(from('demo.zip'))
				.hasBaseDir("demo")
				.isJavaProject()
				.hasStaticAndTemplatesResources(false)
	}

	@Test
	void customArtifactIdUpdateNameAutomatically() {
		toHome {
			page.groupId = 'org.foo'
			page.generateProject.click()
			at HomePage
			zipProjectAssert(from('demo.zip'))
					.hasBaseDir("demo")
					.isJavaProject('org.foo', 'DemoApplication')
		}
	}

	@Test
	void customGroupIdIdUpdatePackageAutomatically() {
		toHome {
			page.artifactId = 'my-project'
			page.generateProject.click()
			at HomePage
			zipProjectAssert(from('my-project.zip'))
					.hasBaseDir("my-project")
					.isJavaProject('com.example', 'MyProjectApplication')
		}
	}

	@Test
	void createGroovyProject() {
		toHome {
			page.advanced.click()
			page.language = 'groovy'
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir('demo')
					.isMavenProject()
					.isGroovyProject()
					.hasStaticAndTemplatesResources(false)
					.pomAssert().hasDependenciesCount(3)
					.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
					.hasDependency('org.codehaus.groovy', 'groovy')
		}
	}

	@Test
	void createKotlinProject() {
		toHome {
			page.advanced.click()
			page.language = 'kotlin'
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir('demo').isMavenProject().isKotlinProject()
					.hasStaticAndTemplatesResources(false)
					.pomAssert().hasDependenciesCount(3)
					.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
					.hasDependency('org.jetbrains.kotlin', 'kotlin-stdlib')
		}
	}

	@Test
	void createWarProject() {
		toHome {
			page.advanced.click()
			page.packaging = 'war'
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir("demo")
					.isMavenProject()
					.isJavaWarProject()
					.pomAssert().hasPackaging('war').hasDependenciesCount(3)
					.hasSpringBootStarterDependency('web') // Added with war packaging
					.hasSpringBootStarterTomcat()
					.hasSpringBootStarterTest()
		}
	}

	@Test
	void createJavaProjectWithCustomDefaults() {
		toHome {
			page.groupId = 'com.acme'
			page.artifactId = 'foo-bar'
			page.advanced.click()
			page.name = 'My project'
			page.description = 'A description for my project'
			page.packageName = 'com.example.foo'
			page.dependency('web').click()
			page.dependency('data-jpa').click()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('foo-bar.zip'))
			projectAssert.hasBaseDir("foo-bar")
					.isMavenProject()
					.isJavaProject('com.example.foo', 'MyProjectApplication')
					.hasStaticAndTemplatesResources(true)
					.pomAssert()
					.hasGroupId('com.acme')
					.hasArtifactId('foo-bar')
					.hasName('My project')
					.hasDescription('A description for my project')
					.hasSpringBootStarterDependency('web')
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterTest()
		}
	}

	@Test
	void createGroovyProjectWithCustomDefaults() {
		toHome {
			page.groupId = 'org.biz'
			page.artifactId = 'groovy-project'
			page.advanced.click()
			page.language = 'groovy'
			page.name = 'My Groovy project'
			page.description = 'A description for my Groovy project'
			page.packageName = 'com.example.biz'
			page.dependency('web').click()
			page.dependency('data-jpa').click()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('groovy-project.zip'))
			projectAssert.hasBaseDir("groovy-project")
					.isMavenProject()
					.isGroovyProject('com.example.biz', 'MyGroovyProjectApplication')
					.hasStaticAndTemplatesResources(true)
					.pomAssert()
					.hasGroupId('org.biz')
					.hasArtifactId('groovy-project')
					.hasName('My Groovy project')
					.hasDescription('A description for my Groovy project')
					.hasSpringBootStarterDependency('web')
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterTest()
					.hasDependency('org.codehaus.groovy', 'groovy')
		}
	}

	@Test
	void createKotlinProjectWithCustomDefaults() {
		toHome {
			page.groupId = 'org.biz'
			page.artifactId = 'kotlin-project'
			page.advanced.click()
			page.language = 'kotlin'
			page.name = 'My Kotlin project'
			page.description = 'A description for my Kotlin project'
			page.packageName = 'com.example.biz'
			page.dependency('web').click()
			page.dependency('data-jpa').click()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('kotlin-project.zip'))
			projectAssert.hasBaseDir("kotlin-project").isMavenProject()
					.isKotlinProject('com.example.biz', 'MyKotlinProjectApplication')
					.hasStaticAndTemplatesResources(true)
					.pomAssert().hasGroupId('org.biz').hasArtifactId('kotlin-project')
					.hasName('My Kotlin project').hasDescription('A description for my Kotlin project')
					.hasSpringBootStarterDependency('web')
					.hasSpringBootStarterDependency('data-jpa')
					.hasSpringBootStarterTest()
					.hasDependency('org.jetbrains.kotlin', 'kotlin-stdlib')
		}
	}

	@Test
	void dependencyHiddenAccordingToRange() {
		toHome { // bur: [1.1.4.RELEASE,1.2.0.BUILD-SNAPSHOT)
			page.advanced.click()
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
			page.advanced.click()
			page.dependency('org.acme:bur').value() == 'org.acme:bur'
			page.bootVersion = '1.0.2.RELEASE'
			page.dependency('org.acme:bur').displayed == false
			page.bootVersion = '1.1.4.RELEASE'
			page.dependency('org.acme:bur').displayed == true
			page.dependency('org.acme:bur').value() == false
		}
	}

	@Test
	void customizationShowsUpInDefaultView() {
		toHome('/#!language=groovy&packageName=com.example.acme') {
			assertEquals 'groovy', page.language.value()
			assertEquals 'com.example.acme', page.packageName.value()
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('demo.zip'))
			projectAssert.hasBaseDir('demo')
					.isMavenProject()
					.isGroovyProject('com.example.acme', ProjectAssert.DEFAULT_APPLICATION_NAME )
					.hasStaticAndTemplatesResources(false)
					.pomAssert().hasDependenciesCount(3)
					.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
					.hasDependency('org.codehaus.groovy', 'groovy')

		}
	}

	@Test
	void customizationsShowsUpWhenViewIsSwitched() {
		toHome('/#!packaging=war&javaVersion=1.7') {
			assertEquals 'war', page.packaging.value()
			assertEquals '1.7', page.javaVersion.value()
			page.advanced().click()
			assertEquals 'war', page.packaging.value()
			assertEquals '1.7', page.javaVersion.value()
			page.simple().click()
			assertEquals 'war', page.packaging.value()
			assertEquals '1.7', page.javaVersion.value()
		}
	}

	@Test
	void customizationsOnGroupIdAndArtifactId() {
		toHome('/#!groupId=com.example.acme&artifactId=my-project') {
			page.generateProject.click()
			at HomePage
			def projectAssert = zipProjectAssert(from('my-project.zip'))
			projectAssert.hasBaseDir('my-project')
					.isMavenProject()
					.isJavaProject('com.example.acme', 'MyProjectApplication' )
					.hasStaticAndTemplatesResources(false)
					.pomAssert()
					.hasGroupId('com.example.acme')
					.hasArtifactId('my-project')
					.hasDependenciesCount(2)
					.hasSpringBootStarterRootDependency()
					.hasSpringBootStarterTest()
		}
	}

	private Browser toHome(Closure script) {
		toHome('/', script)
	}

	private Browser toHome(String uri, Closure script) {
		browser.go("http://localhost:$port$uri")
		browser.at HomePage
		script.delegate = browser
		script()
		browser
	}

	private selectDependency(def page, String text) {
		page.autocomplete = text
		enterAction.perform()
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
