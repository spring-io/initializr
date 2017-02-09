package {{packageName}};

import org.junit.Test;
import org.junit.runner.RunWith;
{{testImports}}
{{#newTestInfrastructure}}
@RunWith(SpringRunner.class)
@SpringBootTest
{{/newTestInfrastructure}}
{{^newTestInfrastructure}}
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {{applicationName}}.class)
{{/newTestInfrastructure}}
{{testAnnotations}}public class {{applicationName}}Tests {

	@Test
	public void contextLoads() {
	}

}
