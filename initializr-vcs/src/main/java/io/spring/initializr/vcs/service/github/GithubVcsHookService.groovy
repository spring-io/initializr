package io.spring.initializr.vcs.service.github

import io.spring.initializr.vcs.service.VcsHookService
import io.spring.initializr.vcs.service.github.hooks.ProtectedBranchHook
import io.spring.initializr.generator.BasicProjectRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GithubVcsHookService implements VcsHookService {

    @Autowired(required = false)
    private RestTemplate githubRestTemplate

    @Value('${github.url:}')
    private String githubUrl

    void createHooks(BasicProjectRequest request, String vcsUrl) {
        new ProtectedBranchHook().addHook(githubRestTemplate, githubUrl, request)
    }
}
