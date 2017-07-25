package io.spring.initializr.vcs.config

import io.spring.initializr.vcs.service.VcsService
import io.spring.initializr.vcs.service.github.GithubVcsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan('io.spring.initializr.vcs.service')
class VcsServiceAutoConfiguration {

    @Bean
    VcsService vcsService() {
        return new GithubVcsService()
    }
}
