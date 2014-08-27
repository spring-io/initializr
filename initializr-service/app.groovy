package app

import io.spring.initializr.web.LegacyStsController

@Grab('io.spring.initalizr:initializr:1.0.0.BUILD-SNAPSHOT')
@Grab('spring-boot-starter-security')
class InitializerService {

	@Bean
	@SuppressWarnings("deprecation")
	LegacyStsController legacyStsController() {
		new LegacyStsController()
	}
}