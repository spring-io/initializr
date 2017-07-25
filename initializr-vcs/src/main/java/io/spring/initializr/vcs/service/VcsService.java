package io.spring.initializr.vcs.service;

import groovy.util.logging.Slf4j;
import io.spring.initializr.generator.BasicProjectRequest;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;

@Slf4j
public interface VcsService {

    Logger LOGGER = LoggerFactory.getLogger(VcsService.class);

    default void createGitFiles(File dir, BasicProjectRequest request) {
        Git git = null;
        try {
            // git init, add and commit
            git = Git.init().setDirectory(dir).call();
            git.add().addFilepattern(".").call();
            git.commit().setAll(true).setMessage("Refactor: Initial commit by Initializr").call();
            StoredConfig config = git.getRepository().getConfig();
            config.setString(ConfigConstants.CONFIG_REMOTE_SECTION, "origin", "url", getRepoUrl(request, true));
            config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", "remote", "origin");
            config.setString(ConfigConstants.CONFIG_BRANCH_SECTION, "master", "merge", "refs/heads/master");
            config.save();
        } catch (Exception ex) {
            LOGGER.error("Could not create git files", ex);
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    default void pushCode(File dir, BasicProjectRequest request, CredentialsProvider credentialsProvider, String token) {
        try {
            Git.open(dir).push().setPushAll().setCredentialsProvider(credentialsProvider).setRemote(getRepoHttpsUrl(request, token, true)).call();
        } catch (GitAPIException e) {
            LOGGER.error("Could not push code", e);
        } catch (IOException e) {
            LOGGER.error("Could not open local repo", e);
        } catch(Exception e){
            LOGGER.error("Could not push code", e);
        }
    }

    default void createJenkinsJob(BasicProjectRequest request, RestTemplate restTemplate, String jenkinsBuildWithParametersUrl) {
        LOGGER.info("create jenkins job here via API");
    }

    void createHooks(BasicProjectRequest request);

    void createRepo(File dir, BasicProjectRequest request);

    default String getRepoUrl(BasicProjectRequest request, boolean includeCredentials) {
        return getRepoUrl(request, includeCredentials, true);
    }

    default String getRepoHttpsUrl(BasicProjectRequest request, String token) {
        return getRepoHttpsUrl(request, token, true);
    }

    String getRepoUrl(BasicProjectRequest request, boolean includeCredentials, boolean includeGitExtension);

    String getRepoHttpsUrl(BasicProjectRequest request, String token, boolean includeGitExtension);

}