package {{packageName}};

import org.springframework.boot.builder.SpringApplicationBuilder;
{{servletInitializrImport}}

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources({{applicationName}}.class);
	}

}

