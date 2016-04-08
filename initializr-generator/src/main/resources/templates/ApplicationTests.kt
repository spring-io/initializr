package ${packageName}

import org.junit.Test
import org.junit.runner.RunWith
${testImports}<% if (newTestInfrastructure) { %>
@RunWith(SpringRunner::class)
@SpringBootTest<% } else { %>
@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(${applicationName}::class))<% } %>
${testAnnotations}class ${applicationName}Tests {

	@Test
	fun contextLoads() {
	}

}
