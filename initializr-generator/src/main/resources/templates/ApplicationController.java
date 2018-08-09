package {{packageName}};

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/test")
@RefreshScope
public class {{applicationName}}Controller {

	@RequestMapping(method=RequestMethod.GET)
	public String getSalutation() {
		return "Hello world!";
	}

}
