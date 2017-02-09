package {{packageName}}

import org.springframework.boot.builder.SpringApplicationBuilder
{{#newServletInitializer}}
import org.springframework.boot.web.support.SpringBootServletInitializer
{{/newServletInitializer}}
{{^newServletInitializer}}
import org.springframework.boot.context.web.SpringBootServletInitializer
{{/newServletInitializer}}

class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.sources({{applicationName}})
	}

}
