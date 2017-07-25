package io.spring.initializr.vcs.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.spring.initializr.vcs.service")
public class VcsServiceAutoConfiguration {

}
