/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.generator;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.spring.initializr.InitializrException;
import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.MetadataElement;
import io.spring.initializr.util.TemplateRenderer;
import io.spring.initializr.util.Version;
import io.spring.initializr.util.VersionProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.Assert;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

/**
 * Generate a project based on the configured metadata.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @author Andy Wilkinson
 */
public class ProjectGenerator {

	private static final Logger log = LoggerFactory.getLogger(ProjectGenerator.class);

	private static final Version VERSION_1_2_0_RC1 = Version.parse("1.2.0.RC1");

	private static final Version VERSION_1_3_0_M1 = Version.parse("1.3.0.M1");

	private static final Version VERSION_1_4_0_M2 = Version.parse("1.4.0.M2");

	private static final Version VERSION_1_4_0_M3 = Version.parse("1.4.0.M3");

	private static final Version VERSION_1_4_2_M1 = Version.parse("1.4.2.M1");

	private static final Version VERSION_1_5_0_M1 = Version.parse("1.5.0.M1");

	private static final Version VERSION_2_0_0_M1 = Version.parse("2.0.0.M1");

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private InitializrMetadataProvider metadataProvider;

	@Autowired
	private ProjectRequestResolver requestResolver;

	@Autowired
	private TemplateRenderer templateRenderer = new TemplateRenderer();

	@Autowired
	private ProjectResourceLocator projectResourceLocator = new ProjectResourceLocator();

	@Value("${TMPDIR:.}/initializr")
	private String tmpdir;

	private File temporaryDirectory;
	private transient Map<String, List<File>> temporaryFiles = new LinkedHashMap<>();

	public InitializrMetadataProvider getMetadataProvider() {
		return metadataProvider;
	}

	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setMetadataProvider(InitializrMetadataProvider metadataProvider) {
		this.metadataProvider = metadataProvider;
	}

	public void setRequestResolver(ProjectRequestResolver requestResolver) {
		this.requestResolver = requestResolver;
	}

	public void setTemplateRenderer(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	public void setProjectResourceLocator(ProjectResourceLocator projectResourceLocator) {
		this.projectResourceLocator = projectResourceLocator;
	}

	public void setTmpdir(String tmpdir) {
		this.tmpdir = tmpdir;
	}

	public void setTemporaryDirectory(File temporaryDirectory) {
		this.temporaryDirectory = temporaryDirectory;
	}

	public void setTemporaryFiles(Map<String, List<File>> temporaryFiles) {
		this.temporaryFiles = temporaryFiles;
	}

	/**
	 * Generate a Maven pom for the specified {@link ProjectRequest}.
	 */
	public byte[] generateMavenPom(ProjectRequest request) {
		try {
			Map<String, Object> model = resolveModel(request);
			if (!isMavenBuild(request)) {
				throw new InvalidProjectRequestException("Could not generate Maven pom, "
						+ "invalid project type " + request.getType());
			}
			byte[] content = doGenerateMavenPom(model);
			publishProjectGeneratedEvent(request);
			return content;
		}
		catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex);
			throw ex;
		}
	}

	/**
	 * Generate a Gradle build file for the specified {@link ProjectRequest}.
	 */
	public byte[] generateGradleBuild(ProjectRequest request) {
		try {
			Map<String, Object> model = resolveModel(request);
			if (!isGradleBuild(request)) {
				throw new InvalidProjectRequestException(
						"Could not generate Gradle build, " + "invalid project type "
								+ request.getType());
			}
			byte[] content = doGenerateGradleBuild(model);
			publishProjectGeneratedEvent(request);
			return content;
		}
		catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex);
			throw ex;
		}
	}

	/**
	 * Generate a project structure for the specified {@link ProjectRequest}. Returns a
	 * directory containing the project.
	 */
	public File generateProjectStructure(ProjectRequest request) {
		try {
			return doGenerateProjectStructure(request);
		}
		catch (InitializrException ex) {
			publishProjectFailedEvent(request, ex);
			throw ex;
		}
	}

	protected File doGenerateProjectStructure(ProjectRequest request) {
		Map<String, Object> model = resolveModel(request);

		File rootDir;
		try {
			rootDir = File.createTempFile("tmp", "", getTemporaryDirectory());
		}
		catch (IOException e) {
			throw new IllegalStateException("Cannot create temp dir", e);
		}
		addTempFile(rootDir.getName(), rootDir);
		rootDir.delete();
		rootDir.mkdirs();

		File dir = initializerProjectDir(rootDir, request);

		if (isGradleBuild(request)) {
			String gradle = new String(doGenerateGradleBuild(model));
			writeText(new File(dir, "build.gradle"), gradle);
			writeGradleWrapper(dir, Version.safeParse(request.getBootVersion()));
		}
		else {
			String pom = new String(doGenerateMavenPom(model));
			writeText(new File(dir, "pom.xml"), pom);
			writeMavenWrapper(dir);
		}

		generateGitIgnore(dir, request);

		String applicationName = request.getApplicationName();
		String language = request.getLanguage();

		String codeLocation = language;
		File src = new File(new File(dir, "src/main/" + codeLocation),
				request.getPackageName().replace(".", "/"));
		src.mkdirs();
		String extension = ("kotlin".equals(language) ? "kt" : language);
		write(new File(src, applicationName + "." + extension),
				"Application." + extension, model);

		if ("war".equals(request.getPackaging())) {
			String fileName = "ServletInitializer." + extension;
			write(new File(src, fileName), fileName, model);
		}

		File test = new File(new File(dir, "src/test/" + codeLocation),
				request.getPackageName().replace(".", "/"));
		test.mkdirs();
		setupTestModel(request, model);
		write(new File(test, applicationName + "Tests." + extension),
				"ApplicationTests." + extension, model);

		File resources = new File(dir, "src/main/resources");
		resources.mkdirs();
		writeText(new File(resources, "application.properties"), "");

		if (request.hasWebFacet()) {
			new File(dir, "src/main/resources/templates").mkdirs();
			new File(dir, "src/main/resources/static").mkdirs();
		}
		publishProjectGeneratedEvent(request);
		return rootDir;

	}

	/**
	 * Create a distribution file for the specified project structure directory and
	 * extension
	 */
	public File createDistributionFile(File dir, String extension) {
		File download = new File(getTemporaryDirectory(), dir.getName() + extension);
		addTempFile(dir.getName(), download);
		return download;
	}

	private File getTemporaryDirectory() {
		if (temporaryDirectory == null) {
			temporaryDirectory = new File(tmpdir, "initializr");
			temporaryDirectory.mkdirs();
		}
		return temporaryDirectory;
	}

	/**
	 * Clean all the temporary files that are related to this root directory.
	 * @see #createDistributionFile
	 */
	public void cleanTempFiles(File dir) {
		List<File> tempFiles = temporaryFiles.remove(dir.getName());
		if (!tempFiles.isEmpty()) {
			tempFiles.forEach((File file) -> {
				if (file.isDirectory()) {
					FileSystemUtils.deleteRecursively(file);
				}
				else if (file.exists()) {
					file.delete();
				}
			});
		}
	}

	private void publishProjectGeneratedEvent(ProjectRequest request) {
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		eventPublisher.publishEvent(event);
	}

	private void publishProjectFailedEvent(ProjectRequest request, Exception cause) {
		ProjectFailedEvent event = new ProjectFailedEvent(request, cause);
		eventPublisher.publishEvent(event);
	}

	/**
	 * Generate a {@code .gitignore} file for the specified {@link ProjectRequest}
	 * @param dir the root directory of the project
	 * @param request the request to handle
	 */
	protected void generateGitIgnore(File dir, ProjectRequest request) {
		Map<String, Object> model = new LinkedHashMap<>();
		if (isMavenBuild(request)) {
			model.put("build", "maven");
			model.put("mavenBuild", true);
		}
		else {
			model.put("build", "gradle");
		}
		write(new File(dir, ".gitignore"), "gitignore.tmpl", model);
	}

	/**
	 * Resolve the specified {@link ProjectRequest} and return the model to use to
	 * generate the project
	 * @param originalRequest the request to handle
	 * @return a model for that request
	 */
	protected Map<String, Object> resolveModel(ProjectRequest originalRequest) {
		Assert.notNull(originalRequest.getBootVersion(), "boot version must not be null");
		Map<String, Object> model = new LinkedHashMap<>();
		InitializrMetadata metadata = metadataProvider.get();

		ProjectRequest request = requestResolver.resolve(originalRequest, metadata);

		// request resolved so we can log what has been requested
		List<Dependency> dependencies = request.getResolvedDependencies();
		List<String> dependencyIds = dependencies.stream().map(Dependency::getId)
				.collect(Collectors.toList());
		log.info("Processing request{type=" + request.getType() + ", dependencies="
				+ dependencyIds);

		if (isWar(request)) {
			model.put("war", true);
		}

		if (isMavenBuild(request)) {
			model.put("mavenBuild", true);
			ParentPom parentPom = metadata.getConfiguration().getEnv().getMaven()
					.resolveParentPom(request.getBootVersion());
			if (parentPom.isIncludeSpringBootBom()
					&& !request.getBoms().containsKey("spring-boot")) {
				request.getBoms().put("spring-boot", metadata.createSpringBootBom(
						request.getBootVersion(), "spring-boot.version"));
			}

			model.put("mavenParentGroupId", parentPom.getGroupId());
			model.put("mavenParentArtifactId", parentPom.getArtifactId());
			model.put("mavenParentVersion", parentPom.getVersion());
			model.put("includeSpringBootBom", parentPom.isIncludeSpringBootBom());
		}

		model.put("repositoryValues", request.getRepositories().entrySet());
		if (!request.getRepositories().isEmpty()) {
			model.put("hasRepositories", true);
		}

		List<Map<String,String>> resolvedBoms = buildResolvedBoms(request);
		model.put("resolvedBoms", resolvedBoms);
		ArrayList<Map<String,String>> reversedBoms = new ArrayList<>(resolvedBoms);
		Collections.reverse(reversedBoms);
		model.put("reversedBoms", reversedBoms);

		model.put("compileDependencies",
				filterDependencies(dependencies, Dependency.SCOPE_COMPILE));
		model.put("runtimeDependencies",
				filterDependencies(dependencies, Dependency.SCOPE_RUNTIME));
		model.put("compileOnlyDependencies",
				filterDependencies(dependencies, Dependency.SCOPE_COMPILE_ONLY));
		model.put("providedDependencies",
				filterDependencies(dependencies, Dependency.SCOPE_PROVIDED));
		model.put("testDependencies",
				filterDependencies(dependencies, Dependency.SCOPE_TEST));

		request.getBoms().forEach((k, v) -> {
			if (v.getVersionProperty() != null) {
				request.getBuildProperties().getVersions().computeIfAbsent(
						v.getVersionProperty(), key -> v::getVersion);
			}
		});

		Map<String, String> versions = new LinkedHashMap<>();
		model.put("buildPropertiesVersions", versions.entrySet());
		request.getBuildProperties().getVersions().forEach((k, v) ->
				versions.put(computeVersionProperty(request,k), v.get()));
		Map<String, String> gradle = new LinkedHashMap<>();
		model.put("buildPropertiesGradle", gradle.entrySet());
		request.getBuildProperties().getGradle().forEach((k, v) ->
				gradle.put(k, v.get()));
		Map<String, String> maven = new LinkedHashMap<>();
		model.put("buildPropertiesMaven", maven.entrySet());
		request.getBuildProperties().getMaven().forEach((k, v) -> maven.put(k, v.get()));

		// Add various versions
		model.put("dependencyManagementPluginVersion", metadata.getConfiguration()
				.getEnv().getGradle().getDependencyManagementPluginVersion());
		model.put("kotlinVersion",
				metadata.getConfiguration().getEnv().getKotlin().getVersion());
		if ("kotlin".equals(request.getLanguage())) {
			model.put("kotlin", true);
		}
		if ("groovy".equals(request.getLanguage())) {
			model.put("groovy", true);
		}

		model.put("isRelease", request.getBootVersion().contains("RELEASE"));
		// @SpringBootApplication available as from 1.2.0.RC1
		model.put("useSpringBootApplication", VERSION_1_2_0_RC1
				.compareTo(Version.safeParse(request.getBootVersion())) <= 0);

		// Gradle plugin has changed as from 1.3.0
		model.put("bootOneThreeAvailable", VERSION_1_3_0_M1
				.compareTo(Version.safeParse(request.getBootVersion())) <= 0);

		model.put("bootTwoZeroAvailable", VERSION_2_0_0_M1
				.compareTo(Version.safeParse(request.getBootVersion())) <= 0);

		// Gradle plugin has changed again as from 1.4.2
		model.put("springBootPluginName",
				(VERSION_1_4_2_M1
						.compareTo(Version.safeParse(request.getBootVersion())) <= 0
						? "org.springframework.boot" : "spring-boot"));

		// New testing stuff
		model.put("newTestInfrastructure", isNewTestInfrastructureAvailable(request));

		// New Servlet Initializer location
		model.put("newServletInitializer", isNewServletInitializerAvailable(request));

		// Java versions
		model.put("isJava6", isJavaVersion(request, "1.6"));
		model.put("isJava7", isJavaVersion(request, "1.7"));
		model.put("isJava8", isJavaVersion(request, "1.8"));

		// Append the project request to the model
		BeanWrapperImpl bean = new BeanWrapperImpl(request);
		for (PropertyDescriptor descriptor : bean.getPropertyDescriptors()) {
			if (bean.isReadableProperty(descriptor.getName())) {
				model.put(descriptor.getName(),
						bean.getPropertyValue(descriptor.getName()));
			}
		}
		if (!request.getBoms().isEmpty()) {
			model.put("hasBoms", true);
		}

		return model;
	}

	private List<Map<String,String>> buildResolvedBoms(ProjectRequest request) {
		return request.getBoms().values().stream()
				.sorted(Comparator.comparing(BillOfMaterials::getOrder))
				.map(bom -> toBomModel(request, bom))
				.collect(Collectors.toList());
	}

	private Map<String,String> toBomModel(ProjectRequest request, BillOfMaterials bom) {
		Map<String, String> model = new HashMap<>();
		model.put("groupId", bom.getGroupId());
		model.put("artifactId", bom.getArtifactId());
		model.put("versionToken", (bom.getVersionProperty() != null
				? "${" + computeVersionProperty(request, bom.getVersionProperty()) + "}"
				: bom.getVersion()));
		return model;
	}

	private String computeVersionProperty(ProjectRequest request,
			VersionProperty property) {
		if (isGradleBuild(request)) {
			return property.toCamelCaseFormat();
		}
		return property.toStandardFormat();
	}

	protected void setupTestModel(ProjectRequest request, Map<String, Object> model) {
		String imports = "";
		String testAnnotations = "";
		boolean newTestInfrastructure = isNewTestInfrastructureAvailable(request);
		if (newTestInfrastructure) {
			imports += String.format(
					generateImport("org.springframework.boot.test.context.SpringBootTest",
							request.getLanguage()) + "%n");
			imports += String.format(
					generateImport("org.springframework.test.context.junit4.SpringRunner",
							request.getLanguage()) + "%n");
		}
		else {
			imports += String.format(generateImport(
					"org.springframework.boot.test.SpringApplicationConfiguration",
					request.getLanguage()) + "%n");
			imports += String.format(generateImport(
					"org.springframework.test.context.junit4.SpringJUnit4ClassRunner",
					request.getLanguage()) + "%n");
		}
		if (request.hasWebFacet() && !newTestInfrastructure) {
			imports += String.format(generateImport(
					"org.springframework.test.context.web.WebAppConfiguration",
					request.getLanguage()) + "%n");
			testAnnotations = String.format("@WebAppConfiguration%n");
		}
		model.put("testImports", imports);
		model.put("testAnnotations", testAnnotations);
	}

	protected String generateImport(String type, String language) {
		String end = ("groovy".equals(language) || "kotlin".equals(language)) ? "" : ";";
		return "import " + type + end;
	}

	private static boolean isGradleBuild(ProjectRequest request) {
		return "gradle".equals(request.getBuild());
	}

	private static boolean isMavenBuild(ProjectRequest request) {
		return "maven".equals(request.getBuild());
	}

	private static boolean isWar(ProjectRequest request) {
		return "war".equals(request.getPackaging());
	}

	private static boolean isNewTestInfrastructureAvailable(ProjectRequest request) {
		return VERSION_1_4_0_M2
				.compareTo(Version.safeParse(request.getBootVersion())) <= 0;
	}

	private static boolean isNewServletInitializerAvailable(ProjectRequest request) {
		return VERSION_1_4_0_M3
				.compareTo(Version.safeParse(request.getBootVersion())) <= 0;
	}

	private static boolean isGradle3Available(Version bootVersion) {
		return VERSION_1_5_0_M1.compareTo(bootVersion) <= 0;
	}

	private static boolean isJavaVersion(ProjectRequest request, String version) {
		return request.getJavaVersion().equals(version);
	}

	private byte[] doGenerateMavenPom(Map<String, Object> model) {
		return templateRenderer.process("starter-pom.xml", model).getBytes();
	}

	private byte[] doGenerateGradleBuild(Map<String, Object> model) {
		return templateRenderer.process("starter-build.gradle", model).getBytes();
	}

	private void writeGradleWrapper(File dir, Version bootVersion) {
		String gradlePrefix = isGradle3Available(bootVersion) ? "gradle3" : "gradle";
		writeTextResource(dir, "gradlew.bat", gradlePrefix + "/gradlew.bat");
		writeTextResource(dir, "gradlew", gradlePrefix + "/gradlew");

		File wrapperDir = new File(dir, "gradle/wrapper");
		wrapperDir.mkdirs();
		writeTextResource(wrapperDir, "gradle-wrapper.properties",
				gradlePrefix + "/gradle/wrapper/gradle-wrapper.properties");
		writeBinaryResource(wrapperDir, "gradle-wrapper.jar",
				gradlePrefix + "/gradle/wrapper/gradle-wrapper.jar");
	}

	private void writeMavenWrapper(File dir) {
		writeTextResource(dir, "mvnw.cmd", "maven/mvnw.cmd");
		writeTextResource(dir, "mvnw", "maven/mvnw");

		File wrapperDir = new File(dir, ".mvn/wrapper");
		wrapperDir.mkdirs();
		writeTextResource(wrapperDir, "maven-wrapper.properties",
				"maven/wrapper/maven-wrapper.properties");
		writeBinaryResource(wrapperDir, "maven-wrapper.jar",
				"maven/wrapper/maven-wrapper.jar");
	}

	private File writeBinaryResource(File dir, String name, String location) {
		return doWriteProjectResource(dir, name, location, true);
	}

	private File writeTextResource(File dir, String name, String location) {
		return doWriteProjectResource(dir, name, location, false);
	}

	private File doWriteProjectResource(File dir, String name, String location,
			boolean binary) {
		File target = new File(dir, name);
		if (binary) {
			writeBinary(target, projectResourceLocator
					.getBinaryResource("classpath:project/" + location));
		}
		else {
			writeText(target, projectResourceLocator
					.getTextResource("classpath:project/" + location));
		}
		return target;
	}

	private File initializerProjectDir(File rootDir, ProjectRequest request) {
		if (request.getBaseDir() != null) {
			File dir = new File(rootDir, request.getBaseDir());
			dir.mkdirs();
			return dir;
		}
		else {
			return rootDir;
		}
	}

	public void write(File target, String templateName, Map<String, Object> model) {
		String body = templateRenderer.process(templateName, model);
		writeText(target, body);
	}

	private void writeText(File target, String body) {
		try (OutputStream stream = new FileOutputStream(target)) {
			StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}

	private void writeBinary(File target, byte[] body) {
		try (OutputStream stream = new FileOutputStream(target)) {
			StreamUtils.copy(body, stream);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}

	private void addTempFile(String group, File file) {
		temporaryFiles.computeIfAbsent(group, key -> new ArrayList<>()).add(file);
	}

	private static List<Dependency> filterDependencies(List<Dependency> dependencies,
			String scope) {
		return dependencies.stream().filter(dep -> scope.equals(dep.getScope()))
				.sorted(Comparator.comparing(MetadataElement::getId))
				.collect(Collectors.toList());
	}

}
