package {{packageName}}

import org.springframework.boot.builder.SpringApplicationBuilder
{{servletInitializrImport}}

class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.sources({{applicationName}})
	}

}

