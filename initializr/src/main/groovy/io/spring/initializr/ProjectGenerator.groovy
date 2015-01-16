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

package io.spring.initializr

import groovy.util.logging.Slf4j
import io.spring.initializr.support.Version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.util.Assert

import static io.spring.initializr.support.GroovyTemplate.template

/**
 * Generate a project based on the configured metadata.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @since 1.0
 */
@Slf4j
class ProjectGenerator {

	private static final VERSION_1_2_0_RC1 = Version.parse('1.2.0.RC1')

	@Autowired
	InitializrMetadata metadata

	@Value('${TMPDIR:.}')
	String tmpdir

	final Set<ProjectGenerationListener> listeners = []

	private transient Map<String, List<File>> temporaryFiles = [:]

	/**
	 * Generate a Maven pom for the specified {@link ProjectRequest}.
	 */
	byte[] generateMavenPom(ProjectRequest request) {
		def model = initializeModel(request)
		def content = doGenerateMavenPom(model)
		invokeListeners(request)
		content
	}

	/**
	 * Generate a Gradle build file for the specified {@link ProjectRequest}.
	 */
	byte[] generateGradleBuild(ProjectRequest request) {
		def model = initializeModel(request)
		def content = doGenerateGradleBuild(model)
		invokeListeners(request)
		content
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns
	 * a directory containing the project.
	 */
	File generateProjectStructure(ProjectRequest request) {
		def model = initializeModel(request)

		def dir = File.createTempFile('tmp', '', new File(tmpdir))
		addTempFile(dir.name, dir)
		dir.delete()
		dir.mkdirs()

		if ('spring-boot-cli-project'.equals(request.type)) {
			doGenerateSpringBootCliStructure(dir, model, request)
		} else {
			doGenerateProjectStructure(dir, model, request)
		}

		invokeListeners(request)
		dir

	}

	private File doGenerateProjectStructure(File dir, def model, ProjectRequest request) {
		if ('gradle'.equals(request.build)) {
			def gradle = new String(doGenerateGradleBuild(model))
			new File(dir, 'build.gradle').write(gradle)
		} else {
			def pom = new String(doGenerateMavenPom(model))
			new File(dir, 'pom.xml').write(pom)
		}

		def applicationName = request.applicationName
		def language = request.language

		def src = new File(new File(dir, "src/main/$language"), request.packageName.replace('.', '/'))
		src.mkdirs()
		write(new File(src, "${applicationName}.${language}"), "Application.$language", model)

		if (request.packaging == 'war') {
			def fileName = "ServletInitializer.$language"
			write(new File(src, fileName), fileName, model)
		}

		def test = new File(new File(dir, "src/test/$language"), request.packageName.replace('.', '/'))
		test.mkdirs()
		if (request.hasWebFacet()) {
			model.testAnnotations = '@WebAppConfiguration\n'
			model.testImports = 'import org.springframework.test.context.web.WebAppConfiguration;\n'
		} else {
			model.testAnnotations = ''
			model.testImports = ''
		}
		write(new File(test, "${applicationName}Tests.${language}"), "ApplicationTests.$language", model)

		def resources = new File(dir, 'src/main/resources')
		resources.mkdirs()
		new File(resources, 'application.properties').write('')

		def readme = template 'README.adoc', model
		new File(dir, 'README.adoc').write(readme)

		if (request.hasWebFacet()) {
			new File(dir, 'src/main/resources/templates').mkdirs()
			new File(dir, 'src/main/resources/static').mkdirs()
		}
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns
	 * a directory containing the project.
	 */
	private File doGenerateSpringBootCliStructure(File dir, def model, ProjectRequest request) {
		boolean thymeleaf = false
		boolean springSecurity = false
		boolean springDataRest = false
		boolean springDataJpa = false
		boolean springDataMongo = false
		boolean springDataRedis = false
		boolean springDataGemfire = false
		boolean springDataSolr = false
		boolean springDataElasticsearch = false

		model.resolvedDependencies.each {
			if (it.name.equals('Security')) {
				springSecurity = true
			} else if (it.name.equals('Thymeleaf')) {
				thymeleaf = true
			} else if (it.name.equals('JPA')) {
				springDataJpa = true
			} else if (it.name.equals('MongoDB')) {
				springDataMongo = true
			} else if (it.name.equals('Redis')) {
				springDataRedis = true
			} else if (it.name.equals('Gemfire')) {
				springDataGemfire = true
			} else if (it.name.equals('Solr')) {
				springDataSolr = true
			} else if (it.name.equals('Elasticsearch')) {
				springDataElasticsearch = true
			} else if (it.name.equals('Rest Repositories')) {
				springDataRest = true
			}
		}

		// Synergistic combinations
		if (thymeleaf && springSecurity) {
			// Figure out how to add [groupId: "org.thymeleaf.extras", artifactId: "thymeleaf-extras-springsecurity3"]
		}
		if (springDataRest) {
			def restRepositoriesDeps = model.resolvedDependencies.find{it.name == "Rest Repositories"}
			if (springDataJpa) {
				restRepositoriesDeps.refdocs << "http://spring.io/guides/gs/accessing-data-rest/[Accessing JPA Data with REST]"
			}
			if (springDataMongo) {
				restRepositoriesDeps.refdocs << "http://spring.io/guides/gs/accessing-mongodb-data-rest/[Accessing MongoDB Data with REST]"
			}
			if (springDataGemfire) {
				restRepositoriesDeps.refdocs << "http://spring.io/guides/gs/accessing-gemfire-data-rest/[Accessing GemFire Data with REST]"
			}
		}


		def readme = template 'spring-boot-cli-README.adoc', model
		new File(dir, 'README.adoc').write(readme)

		def app = template 'spring-boot-cli-app.groovy', model
		new File(dir, 'app.groovy').write(app)
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

	private void invokeListeners(ProjectRequest request) {
		listeners.each {
			it.onGeneratedProject(request)
		}
	}

	private Map initializeModel(ProjectRequest request) {
		Assert.notNull request.bootVersion, 'boot version must not be null'
		def model = [:]
		request.resolve(metadata)

		// request resolved so we can log what has been requested
		def dependencies = request.resolvedDependencies.collect { it.id }
		log.info("Processing request{type=$request.type, dependencies=$dependencies}")

		request.properties.each { model[it.key] = it.value }

		// @SpringBootApplication available as from 1.2.0.RC1
		model['useSpringBootApplication'] = VERSION_1_2_0_RC1
				.compareTo(Version.safeParse(request.bootVersion)) <= 0
		model
	}

	private byte[] doGenerateMavenPom(Map model) {
		template 'starter-pom.xml', model
	}


	private byte[] doGenerateGradleBuild(Map model) {
		template 'starter-build.gradle', model
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

}
