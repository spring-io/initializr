package ${packageName}

/**
 * ${name}
 * ${description}
 *
 * Artifact: ${artifactId}
 * Group: ${groupId}
 *
 * Built with the following features: <% resolvedDependencies.each { %>
 * - ${it.name}<% } %>
 *
 * @author Spring Initializr
 */
<% shortcutLibraries.each { %>
@Grab('${it}')<% } %><% externalLibraries.each { %>
@Grab('${it.artifactId}')<% } %><% annotations.each { %>
${it}<% } %>
class App {
	<% applicationAttributes.each { %>
	${it}<% } %>

}