package ${packageName};

import org.junit.Test;
import org.junit.runner.RunWith;
${testImports}import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
${testAnnotations}public class ApplicationTests {

	@Test
	public void contextLoads() {
	}

}
