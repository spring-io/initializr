package {{packageName}}

import org.springframework.boot.SpringApplication
{{applicationImports}}

{{applicationAnnotations}}
class {{applicationName}} {

	static void main(String[] args) {
		SpringApplication.run {{applicationName}}, args
	}
}
