package io.spring.initializr.vcs.service.github

import groovy.util.logging.Slf4j
import io.spring.initializr.generator.BasicProjectRequest
import io.spring.initializr.vcs.data.CreateRepoRequest
import io.spring.initializr.vcs.data.github.CreateRepoResponse
import io.spring.initializr.vcs.service.VcsService
import org.eclipse.jgit.transport.CredentialsProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.util.concurrent.Executors

@Slf4j
@Service
class GithubVcsService implements VcsService {

    @Autowired(required = false)
    private CredentialsProvider githubCredentialsProvider

    @Autowired(required = false)
    private RestTemplate githubRestTemplate

    @Autowired(required = false)
    private RestTemplate jenkinsRestTemplate

    @Value('${github.url:}')
    private String githubUrl

    @Value('${github.password:}')
    private String token


    @Value('${jenkins.buildWithParameters.url:}')
    private String jenkinsBuildWithParametersUrl

    @Autowired
    GithubVcsHookService githubVcsHookService

    @Override
    void createRepo(File dir, BasicProjectRequest request) {

        createGitFiles(dir, request)

        CreateRepoRequest createRepoRequest = new CreateRepoRequest(name: request.artifactId, description: request.description)
        try {
            githubRestTemplate?.postForObject(githubUrl + "/orgs/Grails-Plugin-Consortium/repos", createRepoRequest, CreateRepoResponse.class)
            createJenkinsJob(request, jenkinsRestTemplate, jenkinsBuildWithParametersUrl)
            pushCode(dir, request, githubCredentialsProvider, token)
            createHooks(request)
        } catch (Exception e) {
            log.error("Could not create remote repo ${createRepoRequest.toString()}", e)
        }
    }

    void createHooks(BasicProjectRequest request) {
        createHooksAsync({ githubVcsHookService.createHooks(request, getRepoUrl(request, false)) })
    }

    @Override
    String getRepoUrl(BasicProjectRequest request, boolean includeCredentials = true, boolean includeGitExtension = true) {
        return "git@github.com:Grails-Plugin-Consortium/${request.getArtifactId()}${(includeGitExtension ? ".git" : "")}"
    }

    @Override
    String getRepoHttpsUrl(BasicProjectRequest request, String token, boolean includeGitExtension = true) {
        return "https://$token@github.com/Grails-Plugin-Consortium/${request.getArtifactId()}${(includeGitExtension ? ".git" : "")}"
    }

    private static void createHooksAsync(Runnable runnable) {
        Executors.newSingleThreadExecutor().submit(runnable)
    }
}
