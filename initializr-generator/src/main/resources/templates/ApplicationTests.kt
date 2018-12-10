package {{packageName}}

import org.junit.Test
import org.junit.runner.RunWith
{{testImports}}
@RunWith(SpringRunner::class)
@SpringBootTest
{{testAnnotations}}class {{applicationName}}Tests {

	@Test
	fun contextLoads() {
	}

}

