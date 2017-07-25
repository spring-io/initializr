package io.spring.initializr.vcs.service.github.hooks

import groovy.util.logging.Slf4j
import io.spring.initializr.generator.BasicProjectRequest
import io.spring.initializr.vcs.data.github.CreateRepoResponse
import io.spring.initializr.vcs.service.VcsHook
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate

@Slf4j
class ProtectedBranchHook implements VcsHook<CreateRepoResponse> {

    @Override
    void addHook(RestTemplate restTemplate, String repoUrl, BasicProjectRequest request) {
        try {
//            validateResponse(restTemplate.exchange(repoUrl + "/api/v3/repos/Grails-Plugin-Consortium/$request.artifactId/branches/master/protection", HttpMethod.PUT, getBranchProtectionRequest(), CreateRepoResponse), request)
            validateResponse(restTemplate.exchange(repoUrl + "/repos/Grails-Plugin-Consortium/$request.artifactId/branches/master/protection", HttpMethod.PUT, getBranchProtectionRequest(), CreateRepoResponse), request)
        } catch (Exception e) {
            log.error("Could not add protected branch hook", e)
        }
    }

    private static HttpEntity<Map> getBranchProtectionRequest() {
        HttpHeaders headers = new HttpHeaders()
        headers.setAccept(Arrays.asList(new MediaType("application", "vnd.github.loki-preview+json")))
        new HttpEntity<Map>([required_status_checks: [strict: true, contexts: ["continuous-integration/jenkins"]], "enforce_admins": true, "restrictions": null], headers)
    }

}
