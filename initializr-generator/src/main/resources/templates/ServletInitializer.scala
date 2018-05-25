package {{packageName}};

import org.springframework.boot.builder.SpringApplicationBuilder;
{{servletInitializrImport}}

class ServletInitializer extends SpringBootServletInitializer {

  override def configure(SpringApplicationBuilder application): SpringApplicationBuilder = {
    application.sources(classOf[{{applicationName}}]);
  }

}
