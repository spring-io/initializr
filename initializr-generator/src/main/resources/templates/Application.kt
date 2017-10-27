package {{packageName}}

{{^kotlinSupport}}
import org.springframework.boot.SpringApplication
{{/kotlinSupport}}
{{applicationImports}}
{{#kotlinSupport}}
import org.springframework.boot.runApplication
{{/kotlinSupport}}

{{applicationAnnotations}}
class {{applicationName}}

fun main(args: Array<String>) {
{{^kotlinSupport}}
    SpringApplication.run({{applicationName}}::class.java, *args)
{{/kotlinSupport}}
{{#kotlinSupport}}
    runApplication<{{applicationName}}>(*args)
{{/kotlinSupport}}
}
