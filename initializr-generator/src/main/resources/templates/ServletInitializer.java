package {{packageName}};

import org.springframework.boot.builder.SpringApplicationBuilder;
{{#newServletInitializer}}
import org.springframework.boot.web.support.SpringBootServletInitializer;
{{/newServletInitializer}}
{{^newServletInitializer}}
import org.springframework.boot.context.web.SpringBootServletInitializer;
{{/newServletInitializer}}

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources({{applicationName}}.class);
	}

}
