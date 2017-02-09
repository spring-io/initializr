package {{packageName}}

import org.junit.Test
import org.junit.runner.RunWith
{{testImports}}
{{#newTestInfrastructure}}
@RunWith(SpringRunner)
@SpringBootTest
{{/newTestInfrastructure}}
{{^newTestInfrastructure}}
@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = {{applicationName}})
{{/newTestInfrastructure}}
{{testAnnotations}}class {{applicationName}}Tests {

	@Test
	void contextLoads() {
	}

}
