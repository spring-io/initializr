package io.spring.initializr.vcs.service;

import io.spring.initializr.generator.BasicProjectRequest;

public interface VcsHookService {
    void createHooks(BasicProjectRequest request, String vcsUrl);

}
