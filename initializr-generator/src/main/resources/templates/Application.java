package {{packageName}};

import org.springframework.boot.SpringApplication;
{{#useSpringBootApplication}}
import org.springframework.boot.autoconfigure.SpringBootApplication;
{{/useSpringBootApplication}}
{{^useSpringBootApplication}}
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
{{/useSpringBootApplication}}

{{#useSpringBootApplication}}
@SpringBootApplication
{{/useSpringBootApplication}}
{{^useSpringBootApplication}}
@Configuration
@ComponentScan
@EnableAutoConfiguration
{{/useSpringBootApplication}}
public class {{applicationName}} {

	public static void main(String[] args) {
		SpringApplication.run({{applicationName}}.class, args);
	}
}
