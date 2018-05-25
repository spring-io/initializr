package {{packageName}};

import org.junit.Test;
import org.junit.runner.RunWith;
{{testImports}}
{{#newTestInfrastructure}}
@RunWith(classOf[SpringRunner])
@SpringBootTest
{{/newTestInfrastructure}}
{{^newTestInfrastructure}}
@RunWith(classOf[SpringJUnit4ClassRunner])
@SpringApplicationConfiguration(classes = Array(classOf[{{applicationName}}]))
{{/newTestInfrastructure}}
{{testAnnotations}}class {{applicationName}}Tests {

	@Test
	def contextLoads(): Unit = {
	}

}
