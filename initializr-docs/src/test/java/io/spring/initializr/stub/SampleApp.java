package io.spring.initializr.stub;

import io.spring.initializr.actuate.autoconfigure.InitializrActuatorEndpointsAutoConfiguration;
import io.spring.initializr.web.autoconfigure.InitializrAutoConfiguration;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A sample app where the Initializr auto-configuration has been disabled.
 *
 * @author Stephane Nicoll
 */
@SpringBootApplication(exclude = {InitializrAutoConfiguration.class,
		InitializrActuatorEndpointsAutoConfiguration.class})
public class SampleApp {
}
