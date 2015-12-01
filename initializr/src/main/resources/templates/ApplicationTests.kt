package ${packageName}

import org.junit.Test
import org.junit.runner.RunWith
${testImports}import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringApplicationConfiguration(classes = arrayOf(${applicationName}::class))
${testAnnotations}class ${applicationName}Tests {

	@Test
	fun contextLoads() {
	}

}
