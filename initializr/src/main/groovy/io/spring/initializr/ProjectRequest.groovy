package io.spring.initializr
/**
 * A request to generate a project.
 *
 * @author Dave Syer
 * @since 1.0
 */
class ProjectRequest {

	def style = []

	String name = 'demo'
	String type = 'starter'
	String description = 'Demo project for Spring Boot'
	String groupId = 'org.test'
	String artifactId
	String version = '0.0.1-SNAPSHOT'
	String bootVersion
	String packaging = 'jar'
	String language = 'java'
	String packageName
	String javaVersion = '1.7'

	String getArtifactId() {
		artifactId == null ? name : artifactId
	}
	String getPackageName() {
		packageName == null ? name.replace('-', '.') : packageName
	}

	boolean isWebStyle() {
		style.any { webStyle(it) }
	}

	private boolean webStyle(String style) {
		style.contains('web') || style.contains('thymeleaf') || style.contains('freemarker') || style.contains('velocity') || style.contains('groovy-template')
	}

}
