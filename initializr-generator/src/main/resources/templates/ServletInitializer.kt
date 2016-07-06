package ${packageName}

import org.springframework.boot.builder.SpringApplicationBuilder<% if (newServletInitializer) { %>
import org.springframework.boot.web.support.SpringBootServletInitializer<% } else { %>
import org.springframework.boot.context.web.SpringBootServletInitializer<% } %>

class ServletInitializer : SpringBootServletInitializer() {

	override fun configure(application: SpringApplicationBuilder) : SpringApplicationBuilder {
		return application.sources(${applicationName}::class.java)
	}

}
