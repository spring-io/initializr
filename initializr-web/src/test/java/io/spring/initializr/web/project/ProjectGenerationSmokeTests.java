/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.web.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.spring.initializr.test.generator.ProjectAssert;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Dave Syer
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class ProjectGenerationSmokeTests
		extends AbstractFullStackInitializrIntegrationTests {

	private File downloadDir;

	private WebDriver driver;

	private Action enterAction;

	@Before
	public void setup() throws IOException {
		Assume.assumeTrue("Smoke tests disabled (set System property 'smoke.test')",
				Boolean.getBoolean("smoke.test"));
		this.downloadDir = this.folder.newFolder();
		FirefoxProfile fxProfile = new FirefoxProfile();
		fxProfile.setPreference("browser.download.folderList", 2);
		fxProfile.setPreference("browser.download.manager.showWhenStarting", false);
		fxProfile.setPreference("browser.download.dir",
				this.downloadDir.getAbsolutePath());
		fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/zip,application/x-compress,application/octet-stream");
		FirefoxOptions options = new FirefoxOptions().setProfile(fxProfile);
		this.driver = new FirefoxDriver(options);
		((JavascriptExecutor) this.driver).executeScript("window.focus();");

		Actions actions = new Actions(this.driver);
		this.enterAction = actions.sendKeys(Keys.ENTER).build();
	}

	@After
	public void destroy() {
		if (this.driver != null) {
			this.driver.close();
		}
	}

	@Test
	public void createSimpleProject() throws Exception {
		HomePage page = toHome();
		page.submit();
		assertSimpleProject().isMavenProject().pomAssert().hasDependenciesCount(2)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest();
	}

	@Test
	public void createSimpleProjectWithGradle() throws Exception {
		HomePage page = toHome();
		page.type("gradle-project");
		page.submit();
		assertSimpleProject().isGradleProject().gradleBuildAssert()
				.contains("compile('org.springframework.boot:spring-boot-starter')")
				.contains(
						"testCompile('org.springframework.boot:spring-boot-starter-test')");
	}

	@Test
	public void createSimpleProjectWithDifferentBootVersion() throws Exception {
		HomePage page = toHome();
		page.bootVersion("1.0.2.RELEASE");
		page.submit();
		assertSimpleProject().isMavenProject().pomAssert()
				.hasSpringBootParent("1.0.2.RELEASE").hasDependenciesCount(2)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest();

	}

	@Test
	public void createSimpleProjectWithDependencies() throws Exception {
		HomePage page = toHome();
		selectDependency(page, "Data JPA");
		selectDependency(page, "Security");
		page.submit();
		assertSimpleProject().isMavenProject().pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterDependency("data-jpa")
				.hasSpringBootStarterDependency("security").hasSpringBootStarterTest();
	}

	@Test
	public void selectDependencyTwiceRemovesIt() throws Exception {
		HomePage page = toHome();
		selectDependency(page, "Data JPA");
		selectDependency(page, "Security");
		selectDependency(page, "Security"); // remove
		page.submit();
		assertSimpleProject().isMavenProject().pomAssert().hasDependenciesCount(2)
				.hasSpringBootStarterDependency("data-jpa").hasSpringBootStarterTest();
	}

	@Test
	public void selectDependencyAndChangeToIncompatibleVersionRemovesIt()
			throws Exception {
		HomePage page = toHome();
		selectDependency(page, "Data JPA");
		selectDependency(page, "org.acme:bur");
		page.bootVersion("1.0.2.RELEASE"); // Bur isn't available anymore
		page.submit();
		assertSimpleProject().isMavenProject().pomAssert()
				.hasSpringBootParent("1.0.2.RELEASE").hasDependenciesCount(2)
				.hasSpringBootStarterDependency("data-jpa").hasSpringBootStarterTest();
	}

	@Test
	public void customArtifactIdUpdateNameAutomatically() throws Exception {
		HomePage page = toHome();
		page.groupId("org.foo");
		page.submit();
		zipProjectAssert(from("demo.zip")).hasBaseDir("demo")
				.isJavaProject("org.foo.demo", "DemoApplication");
	}

	@Test
	public void customGroupIdIdUpdatePackageAutomatically() throws Exception {
		HomePage page = toHome();
		page.artifactId("my-project");
		page.submit();
		zipProjectAssert(from("my-project.zip")).hasBaseDir("my-project")
				.isJavaProject("com.example.myproject", "MyProjectApplication");
	}

	@Test
	public void customArtifactIdWithInvalidPackageNameIsHandled() throws Exception {
		HomePage page = toHome();
		page.artifactId("42my-project");
		page.submit();
		zipProjectAssert(from("42my-project.zip")).hasBaseDir("42my-project")
				.isJavaProject("com.example.myproject", "Application");
	}

	@Test
	public void createGroovyProject() throws Exception {
		HomePage page = toHome();
		page.language("groovy");
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("demo.zip"));
		projectAssert.hasBaseDir("demo").isMavenProject().isGroovyProject()
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
				.hasDependency("org.codehaus.groovy", "groovy");
	}

	@Test
	public void createKotlinProject() throws Exception {
		HomePage page = toHome();
		page.language("kotlin");
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("demo.zip"));
		projectAssert.hasBaseDir("demo").isMavenProject().isKotlinProject()
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(4)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
				.hasDependency("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
				.hasDependency("org.jetbrains.kotlin", "kotlin-reflect");
	}

	@Test
	public void createWarProject() throws Exception {
		HomePage page = toHome();
		page.advanced();
		page.packaging("war");
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("demo.zip"));
		projectAssert.hasBaseDir("demo").isMavenProject().isJavaWarProject().pomAssert()
				.hasPackaging("war").hasDependenciesCount(3)
				.hasSpringBootStarterDependency("web") // Added with war packaging
				.hasSpringBootStarterTomcat().hasSpringBootStarterTest();
	}

	@Test
	public void createJavaProjectWithCustomDefaults() throws Exception {
		HomePage page = toHome();
		page.groupId("com.acme");
		page.artifactId("foo-bar");
		page.advanced();
		page.name("My project");
		page.description("A description for my project");
		page.packageName("com.example.foo");
		page.dependency("web").click();
		page.dependency("data-jpa").click();
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("foo-bar.zip"));
		projectAssert.hasBaseDir("foo-bar").isMavenProject()
				.isJavaProject("com.example.foo", "MyProjectApplication")
				.hasStaticAndTemplatesResources(true).pomAssert().hasGroupId("com.acme")
				.hasArtifactId("foo-bar").hasName("My project")
				.hasDescription("A description for my project")
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa").hasSpringBootStarterTest();
	}

	@Test
	public void createKotlinProjectWithCustomDefaults() throws Exception {
		HomePage page = toHome();
		page.groupId("com.acme");
		page.artifactId("foo-bar");
		page.language("kotlin");
		page.advanced();
		page.name("My project");
		page.description("A description for my Kotlin project");
		page.packageName("com.example.foo");
		page.dependency("web").click();
		page.dependency("data-jpa").click();
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("foo-bar.zip"));
		projectAssert.hasBaseDir("foo-bar").isMavenProject()
				.isKotlinProject("com.example.foo", "MyProjectApplication")
				.hasStaticAndTemplatesResources(true).pomAssert().hasGroupId("com.acme")
				.hasArtifactId("foo-bar").hasName("My project")
				.hasDescription("A description for my Kotlin project")
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa").hasSpringBootStarterTest();
	}

	@Test
	public void createGroovyProjectWithCustomDefaults() throws Exception {
		HomePage page = toHome();
		page.groupId("com.acme");
		page.artifactId("foo-bar");
		page.language("groovy");
		page.advanced();
		page.name("My project");
		page.description("A description for my Groovy project");
		page.packageName("com.example.foo");
		page.dependency("web").click();
		page.dependency("data-jpa").click();
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("foo-bar.zip"));
		projectAssert.hasBaseDir("foo-bar").isMavenProject()
				.isGroovyProject("com.example.foo", "MyProjectApplication")
				.hasStaticAndTemplatesResources(true).pomAssert().hasGroupId("com.acme")
				.hasArtifactId("foo-bar").hasName("My project")
				.hasDescription("A description for my Groovy project")
				.hasSpringBootStarterDependency("web")
				.hasSpringBootStarterDependency("data-jpa").hasSpringBootStarterTest();
	}

	@Test
	public void dependencyHiddenAccordingToRange() throws Exception {
		HomePage page = toHome(); // bur: [1.1.4.RELEASE,1.2.0.BUILD-SNAPSHOT)
		page.advanced();
		assertThat(page.dependency("org.acme:bur").isEnabled()).isTrue();
		page.bootVersion("1.0.2.RELEASE");
		assertThat(page.dependency("org.acme:bur").isEnabled()).isFalse();
		assertThat(page.dependency("org.acme:biz").isEnabled()).isFalse();
		page.bootVersion("1.1.4.RELEASE");
		assertThat(page.dependency("org.acme:bur").isEnabled()).isTrue();
		assertThat(page.dependency("org.acme:biz").isEnabled()).isFalse();
		page.bootVersion("Latest SNAPSHOT");
		assertThat(page.dependency("org.acme:bur").isEnabled()).isFalse();
		assertThat(page.dependency("org.acme:biz").isEnabled()).isTrue();
	}

	@Test
	public void dependencyUncheckedWhenHidden() throws Exception {
		HomePage page = toHome(); // bur: [1.1.4.RELEASE,1.2.0.BUILD-SNAPSHOT)
		page.advanced();
		page.dependency("org.acme:bur").click();
		assertThat(page.dependency("org.acme:bur").isSelected()).isTrue();
		page.bootVersion("1.0.2.RELEASE");
		assertThat(page.dependency("org.acme:bur").isEnabled()).isFalse();
		page.bootVersion("1.1.4.RELEASE");
		assertThat(page.dependency("org.acme:bur").isEnabled()).isTrue();
		assertThat(page.dependency("org.acme:bur").isSelected()).isFalse();
	}

	@Test
	public void customizationShowsUpInDefaultView() throws Exception {
		HomePage page = toHome("/#!language=groovy&packageName=com.example.acme");
		assertThat(page.value("language")).isEqualTo("groovy");
		assertThat(page.value("packageName")).isEqualTo("com.example.acme");
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("demo.zip"));
		projectAssert.hasBaseDir("demo").isMavenProject()
				.isGroovyProject("com.example.acme",
						ProjectAssert.DEFAULT_APPLICATION_NAME)
				.hasStaticAndTemplatesResources(false).pomAssert().hasDependenciesCount(3)
				.hasSpringBootStarterRootDependency().hasSpringBootStarterTest()
				.hasDependency("org.codehaus.groovy", "groovy");
	}

	@Test
	public void customizationsShowsUpWhenViewIsSwitched() throws Exception {
		HomePage page = toHome("/#!packaging=war&javaVersion=1.7");
		assertThat(page.value("packaging")).isEqualTo("war");
		assertThat(page.value("javaVersion")).isEqualTo("1.7");
		page.advanced();
		assertThat(page.value("packaging")).isEqualTo("war");
		assertThat(page.value("javaVersion")).isEqualTo("1.7");
		page.simple();
		assertThat(page.value("packaging")).isEqualTo("war");
		assertThat(page.value("javaVersion")).isEqualTo("1.7");
	}

	@Test
	public void customizationsOnGroupIdAndArtifactId() throws Exception {
		HomePage page = toHome("/#!groupId=com.example.acme&artifactId=my-project");
		page.submit();
		ProjectAssert projectAssert = zipProjectAssert(from("my-project.zip"));
		projectAssert.hasBaseDir("my-project").isMavenProject()
				.isJavaProject("com.example.acme.myproject", "MyProjectApplication")
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasGroupId("com.example.acme").hasArtifactId("my-project")
				.hasDependenciesCount(2).hasSpringBootStarterRootDependency()
				.hasSpringBootStarterTest();
	}

	private HomePage toHome() {
		return toHome("/");
	}

	private HomePage toHome(String path) {
		this.driver.get("http://localhost:" + this.port + path);
		return new HomePage(this.driver);
	}

	private ProjectAssert assertSimpleProject() throws Exception {
		return zipProjectAssert(from("demo.zip")).hasBaseDir("demo").isJavaProject()
				.hasStaticAndTemplatesResources(false);
	}

	private void selectDependency(HomePage page, String text) {
		page.autocomplete(text);
		this.enterAction.perform();
	}

	private byte[] from(String fileName) throws Exception {
		return StreamUtils.copyToByteArray(new FileInputStream(getArchive(fileName)));
	}

	private File getArchive(String fileName) {
		File archive = new File(this.downloadDir, fileName);
		assertThat(archive).exists();
		return archive;
	}

}
