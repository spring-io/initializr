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

package io.spring.initializr.generator

import groovy.util.logging.Slf4j
import io.spring.initializr.InitializrException
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadataProvider
import io.spring.initializr.util.Version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.util.Assert

import static io.spring.initializr.util.GroovyTemplate.template

/**
 * Generate a project based on the configured metadata.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @since 1.0
 */
@Slf4j
class ProjectGenerator {

	private static final VERSION_1_2_0_RC1 = Version.parse('1.2.0.RC1')

	private static final VERSION_1_3_0_M1 = Version.parse('1.3.0.M1')

	private static final VERSION_1_4_0_M2 = Version.parse('1.4.0.M2')

	@Autowired
	ApplicationEventPublisher eventPublisher

	@Autowired
	InitializrMetadataProvider metadataProvider

	@Autowired
	ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator()

	@Value('${TMPDIR:.}')
	String tmpdir

	private transient Map<String, List<File>> temporaryFiles = [:]

	/**
	 * Generate a Maven pom for the specified {@link ProjectRequest}.
	 */
	byte[] generateMavenPom(ProjectRequest request) {
		try {
			def model = initializeModel(request)
			def content = doGenerateMavenPom(model)
			publishProjectGeneratedEvent(request)
			content
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * Generate a Gradle build file for the specified {@link ProjectRequest}.
	 */
	byte[] generateGradleBuild(ProjectRequest request) {
		try {
			def model = initializeModel(request)
			def content = doGenerateGradleBuild(model)
			publishProjectGeneratedEvent(request)
			content
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns
	 * a directory containing the project.
	 */
	File generateProjectStructure(ProjectRequest request) {
		try {
			doGenerateProjectStructure(request)
		} catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex)
			throw ex
		}
	}

	protected File doGenerateProjectStructure(ProjectRequest request) {
		def model = initializeModel(request)

		def rootDir = File.createTempFile('tmp', '', new File(tmpdir))
		addTempFile(rootDir.name, rootDir)
		rootDir.delete()
		rootDir.mkdirs()

		def dir = initializerProjectDir(rootDir, request)

		boolean gradleBuild = 'gradle'.equals(request.build)
		if (gradleBuild) {
			def gradle = new String(doGenerateGradleBuild(model))
			new File(dir, 'build.gradle').write(gradle)
			writeGradleWrapper(dir)
		} else {
			def pom = new String(doGenerateMavenPom(model))
			new File(dir, 'pom.xml').write(pom)
			writeMavenWrapper(dir)
		}

		def applicationName = request.applicationName
		def language = request.language

		String codeLocation = language
		def src = new File(new File(dir, "src/main/$codeLocation"), request.packageName.replace('.', '/'))
		src.mkdirs()
		def extension = (language.equals('kotlin') ? 'kt' : language)
		write(new File(src, "${applicationName}.${extension}"), "Application.$extension", model)

		if (request.packaging == 'war') {
			def fileName = "ServletInitializer.$extension"
			write(new File(src, fileName), fileName, model)
		}

		def test = new File(new File(dir, "src/test/$codeLocation"), request.packageName.replace('.', '/'))
		test.mkdirs()
		setupTestModel(request, model)
		write(new File(test, "${applicationName}Tests.${extension}"), "ApplicationTests.$extension", model)

		def resources = new File(dir, 'src/main/resources')
		resources.mkdirs()
		new File(resources, 'application.properties').write('')

		if (request.hasWebFacet()) {
			new File(dir, 'src/main/resources/templates').mkdirs()
			new File(dir, 'src/main/resources/static').mkdirs()
		}
		publishProjectGeneratedEvent(request)
		rootDir

	}

	/**
	 * Create a distribution file for the specified project structure
	 * directory and extension
	 */
	File createDistributionFile(File dir, String extension) {
		def download = new File(tmpdir, dir.name + extension)
		addTempFile(dir.name, download)
		download
	}

	/**
	 * Clean all the temporary files that are related to this root
	 * directory.
	 * @see #createDistributionFile
	 */
	void cleanTempFiles(File dir) {
		def tempFiles = temporaryFiles.remove(dir.name)
		if (tempFiles) {
			tempFiles.each { File file ->
				if (file.directory) {
					file.deleteDir()
				} else {
					file.delete()
				}
			}
		}
	}

	private void publishProjectGeneratedEvent(ProjectRequest request) {
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request)
		eventPublisher.publishEvent(event)
	}

	private void publishProjectFailedEvent(ProjectRequest request, Exception cause) {
		ProjectFailedEvent event = new ProjectFailedEvent(request, cause)
		eventPublisher.publishEvent(event)
	}

	protected Map initializeModel(ProjectRequest request) {
		Assert.notNull request.bootVersion, 'boot version must not be null'
		def model = [:]
		def metadata = metadataProvider.get()

		request.resolve(metadata)

		// request resolved so we can log what has been requested
		def dependencies = request.resolvedDependencies
		def dependencyIds = dependencies.collect { it.id }
		log.info("Processing request{type=$request.type, dependencies=$dependencyIds}")

		request.properties.each { model[it.key] = it.value }

		model['compileDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_COMPILE)
		model['runtimeDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_RUNTIME)
		model['providedDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_PROVIDED)
		model['testDependencies'] = filterDependencies(dependencies, Dependency.SCOPE_TEST)

		// Add kotlinVersion
		model['kotlinVersion'] = metadata.configuration.env.kotlin.version

		// @SpringBootApplication available as from 1.2.0.RC1
		model['useSpringBootApplication'] = VERSION_1_2_0_RC1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0

		// Gradle plugin has changed as from 1.3.0
		model['bootOneThreeAvailable'] = VERSION_1_3_0_M1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0

		// New testing stuff
		model['newTestInfrastructure'] = isNewTestInfrastructureAvailable(request)

		model
	}

	protected void setupTestModel(ProjectRequest request, Map<String, Object> model) {
		String imports = ''
		String testAnnotations = ''
		def newTestInfrastructure = isNewTestInfrastructureAvailable(request)
		if (newTestInfrastructure) {
			imports += String.format(generateImport('org.springframework.boot.test.context.SpringBootTest',
					request.language) + "%n")
			imports += String.format(generateImport('org.springframework.test.context.junit4.SpringRunner',
					request.language) + "%n")
		} else {
			imports += String.format(generateImport('org.springframework.boot.test.SpringApplicationConfiguration',
					request.language) + "%n")
			imports += String.format(generateImport('org.springframework.test.context.junit4.SpringJUnit4ClassRunner',
					request.language) + "%n")
		}
		if (request.hasWebFacet() && !newTestInfrastructure) {
			imports += String.format(generateImport('org.springframework.test.context.web.WebAppConfiguration',
					request.language) + "%n")
			testAnnotations = String.format('@WebAppConfiguration%n')
		}
		model.testImports = imports
		model.testAnnotations = testAnnotations
	}

	protected String generateImport(String type, String language) {
		String end = (language.equals("groovy") || language.equals("kotlin")) ? '' : ';'
		"import $type$end"
	}

	private static boolean isNewTestInfrastructureAvailable(ProjectRequest request) {
		VERSION_1_4_0_M2
				.compareTo(Version.safeParse(request.bootVersion)) <= 0
	}

	private byte[] doGenerateMavenPom(Map model) {
		template 'starter-pom.xml', model
	}


	private byte[] doGenerateGradleBuild(Map model) {
		template 'starter-build.gradle', model
	}

	private void writeGradleWrapper(File dir) {
		writeTextResource(dir, 'gradlew.bat', 'gradle/gradlew.bat')
		writeTextResource(dir, 'gradlew', 'gradle/gradlew')

		def wrapperDir = new File(dir, 'gradle/wrapper')
		wrapperDir.mkdirs()
		writeTextResource(wrapperDir, 'gradle-wrapper.properties',
				'gradle/gradle/wrapper/gradle-wrapper.properties')
		writeBinaryResource(wrapperDir, 'gradle-wrapper.jar',
				'gradle/gradle/wrapper/gradle-wrapper.jar')
	}

	private void writeMavenWrapper(File dir) {
		writeTextResource(dir, 'mvnw.cmd', 'maven/mvnw.cmd')
		writeTextResource(dir, 'mvnw', 'maven/mvnw')

		def wrapperDir = new File(dir, '.mvn/wrapper')
		wrapperDir.mkdirs()
		writeTextResource(wrapperDir, 'maven-wrapper.properties',
				'maven/wrapper/maven-wrapper.properties')
		writeBinaryResource(wrapperDir, 'maven-wrapper.jar',
				'maven/wrapper/maven-wrapper.jar')
	}

	private File writeBinaryResource(File dir, String name, String location) {
		doWriteProjectResource(dir, name, location, true)
	}

	private File writeTextResource(File dir, String name, String location) {
		doWriteProjectResource(dir, name, location, false)
	}

	private File doWriteProjectResource(File dir, String name, String location, boolean binary) {
		def target = new File(dir, name)
		if (binary) {
			target << projectResourceLocator.getBinaryResource("classpath:project/$location")
		}
		else {
			target.write(projectResourceLocator.getTextResource("classpath:project/$location"))
		}
		target
	}

	private File initializerProjectDir(File rootDir, ProjectRequest request) {
		if (request.baseDir) {
			File dir = new File(rootDir, request.baseDir)
			dir.mkdirs()
			return dir
		} else {
			return rootDir
		}
	}

	def write(File target, String templateName, def model) {
		def tmpl = templateName.endsWith('.groovy') ? templateName + '.tmpl' : templateName
		def body = template tmpl, model
		target.write(body)
	}

	private void addTempFile(String group, File file) {
		def content = temporaryFiles[group]
		if (!content) {
			content = []
			temporaryFiles[group] = content
		}
		content << file
	}

	private static def filterDependencies(def dependencies, String scope) {
		dependencies.findAll { dep -> scope.equals(dep.scope) }.sort { a, b -> a.id <=> b.id }
	}

}
