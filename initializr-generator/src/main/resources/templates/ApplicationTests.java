package {{packageName}};

{{^jupiterAvailable}}
import org.junit.Test;
import org.junit.runner.RunWith;
{{/jupiterAvailable}}
{{testImports}}
{{#newTestInfrastructure}}
{{^jupiterAvailable}}
@RunWith(SpringRunner.class)
{{/jupiterAvailable}}
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
