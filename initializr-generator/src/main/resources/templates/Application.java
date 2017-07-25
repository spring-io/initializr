package {{packageName}};

import org.springframework.boot.SpringApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RestController("/")
public class {{applicationName}} {

	public static void main(String[] args) {
		SpringApplication.run({{applicationName}}.class, args);
	}

	@RequestMapping
	public ResponseEntity<String> index(){
		return ResponseEntity.ok("Hello World");
	}
}
