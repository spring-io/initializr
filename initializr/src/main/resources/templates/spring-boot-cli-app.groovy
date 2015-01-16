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
<% resolvedDependencies.each {
	if (it?.springBootCliAnnotations?.size() > 0) {} else {%>
@Grab('${it.artifactId}')<%
	}
}
resolvedDependencies.each {
	if (it?.springBootCliAnnotations?.size() > 0) {
		it.springBootCliAnnotations.each {%>
${it}<%
		}
	}
} %>
class App {
	<% resolvedDependencies.each {
		it.springBootCliAppAttrs.each {%>
	${it}<%
		}
	} %>

}