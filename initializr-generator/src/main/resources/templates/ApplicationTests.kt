package {{packageName}}

{{^jupiterAvailable}}
import org.junit.Test
import org.junit.runner.RunWith
{{/jupiterAvailable}}
{{testImports}}
{{#newTestInfrastructure}}
{{^jupiterAvailable}}
@RunWith(SpringRunner::class)
{{/jupiterAvailable}}
@SpringBootTest
{{/newTestInfrastructure}}
{{^newTestInfrastructure}}
@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf({{applicationName}}::class))
{{/newTestInfrastructure}}
{{testAnnotations}}class {{applicationName}}Tests {

	@Test
	fun contextLoads() {
	}

}
