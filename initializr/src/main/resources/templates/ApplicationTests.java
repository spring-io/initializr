package ${packageName};

import org.junit.Test;
import org.junit.runner.RunWith;
${testImports}import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ${applicationName}.class)
${testAnnotations}public class ${applicationName}Tests {

	@Test
	public void contextLoads() {
	}

}
