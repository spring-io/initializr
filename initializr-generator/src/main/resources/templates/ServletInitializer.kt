package {{packageName}}

import org.springframework.boot.builder.SpringApplicationBuilder
{{servletInitializrImport}}

class ServletInitializer : SpringBootServletInitializer() {

	override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
		return application.sources({{applicationName}}::class.java)
	}

}

