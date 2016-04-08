package ${packageName};

import org.junit.Test;
import org.junit.runner.RunWith;
${testImports}<% if (newTestInfrastructure) { %>
@RunWith(SpringRunner.class)
@SpringBootTest<% } else { %>
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ${applicationName}.class)<% } %>
${testAnnotations}public class ${applicationName}Tests {

	@Test
	public void contextLoads() {
	}

}
