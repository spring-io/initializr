package {{packageName}}

import org.springframework.boot.SpringApplication
{{applicationImports}}

{{applicationAnnotations}}
class {{applicationName}}

fun main(args: Array<String>) {
    SpringApplication.run({{applicationName}}::class.java, *args)
}
