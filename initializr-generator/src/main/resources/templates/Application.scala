package {{packageName}};

import org.springframework.boot.SpringApplication;
{{applicationImports}}

{{applicationAnnotations}}
class {{applicationName}} {
{{#scalaJackson}}
  @Bean
  def jacksonScala: DefaultScalaModule = DefaultScalaModule
{{/scalaJackson}}
}

object {{applicationName}} extends App {
  SpringApplication.run(classOf[{{applicationName}}], args: _*);
}
