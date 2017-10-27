package {{packageName}};

import org.springframework.boot.SpringApplication;
{{applicationImports}}

{{applicationAnnotations}}
public class {{applicationName}} {

	public static void main(String[] args) {
		SpringApplication.run({{applicationName}}.class, args);
	}
}
