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

package io.spring.initializr

import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
class ProjectGenerator {

	private static final Logger logger = LoggerFactory.getLogger(ProjectGenerator)

	@Autowired
	InitializrMetadata metadata

	@Value('${TMPDIR:.}')
	String tmpdir

	final Set<ProjectGenerationListener> listeners = new LinkedHashSet<>()

	private transient Map<String, List<File>> temporaryFiles = new HashMap<>()

	/**
	 * Generate a Maven pom for the specified {@link ProjectRequest}.
	 */
	byte[] generateMavenPom(ProjectRequest request) {
		Map model = initializeModel(request)
		byte[] content = doGenerateMavenPom(model)
		invokeListeners(request)
		content
	}

	/**
	 * Generate a Gradle build file for the specified {@link ProjectRequest}.
	 */
	byte[] generateGradleBuild(ProjectRequest request) {
		Map model = initializeModel(request)
		byte[] content = doGenerateGradleBuild(model)
		invokeListeners(request)
		content
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns
	 * a directory containing the project.
	 */
	File generateProjectStructure(ProjectRequest request) {
		def model = initializeModel(request)

		File dir = File.createTempFile('tmp', '', new File(tmpdir))
		addTempFile(dir.name, dir)
		dir.delete()
		dir.mkdirs()

		if (request.type.contains('gradle')) {
			String gradle = new String(doGenerateGradleBuild(model))
			new File(dir, 'build.gradle').write(gradle)
		} else {
			String pom = new String(doGenerateMavenPom(model))
			new File(dir, 'pom.xml').write(pom)
		}

		String language = request.language

		File src = new File(new File(dir, 'src/main/' + language), request.packageName.replace('.', '/'))
		src.mkdirs()
		write(src, 'Application.' + language, model)

		if (request.packaging == 'war') {
			write(src, 'ServletInitializer.' + language, model)
		}

		File test = new File(new File(dir, 'src/test/' + language), request.packageName.replace('.', '/'))
		test.mkdirs()
		if (request.hasWebFacet()) {
			model.testAnnotations = '@WebAppConfiguration\n'
			model.testImports = 'import org.springframework.test.context.web.WebAppConfiguration;\n'
		} else {
			model.testAnnotations = ''
			model.testImports = ''
		}
		write(test, 'ApplicationTests.' + language, model)

		File resources = new File(dir, 'src/main/resources')
		resources.mkdirs()
		new File(resources, 'application.properties').write('')

		if (request.hasWebFacet()) {
			new File(dir, 'src/main/resources/templates').mkdirs()
			new File(dir, 'src/main/resources/static').mkdirs()
		}
		invokeListeners(request)
		dir

	}

	/**
	 * Create a distribution file for the specified project structure
	 * directory and extension
	 */
	File createDistributionFile(File dir, String extension) {
		File download = new File(tmpdir, dir.name + extension)
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
		if (tempFiles != null) {
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
		logger.info('Processing request{type=' + request.type + ', ' +
				'dependencies=' + request.dependencies.collect {it.id}+ '}')

		request.properties.each { model[it.key] = it.value }
		model
	}

	private byte[] doGenerateMavenPom(Map model) {
		template 'starter-pom.xml', model
	}


	private byte[] doGenerateGradleBuild(Map model) {
		template 'starter-build.gradle', model
	}

	def write(File src, String name, def model) {
		String tmpl = name.endsWith('.groovy') ? name + '.tmpl' : name
		def body = template tmpl, model
		new File(src, name).write(body)
	}

	private void addTempFile(String group, File file) {
		def content = temporaryFiles.get(group)
		if (content == null) {
			content = new ArrayList<File>()
			temporaryFiles.put(group, content)
		}
		content.add(file)
	}

}
