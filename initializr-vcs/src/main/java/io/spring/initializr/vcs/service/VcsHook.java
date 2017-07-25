package io.spring.initializr.vcs.service;

import io.spring.initializr.generator.BasicProjectRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public interface VcsHook<T> {

    Log log = LogFactory.getLog(VcsHook.class);

    default void addHook(RestTemplate restTemplate, String repoUrl, BasicProjectRequest request) {
        log.info("No override doing nothing");
    }

    default boolean validateResponse(ResponseEntity<T> responseEntity, Object request) {
        if (responseEntity != null && validateResponseStatus().equals(responseEntity.getStatusCode())) {
            log.info("Status Code: " + responseEntity.getStatusCode());
            log.info("Response Body: " + responseEntity.getBody());
            log.info("Added hook with" + request.toString());
        }
        return responseEntity != null && validateResponseStatus().equals(responseEntity.getStatusCode());
    }

    default HttpStatus validateResponseStatus() {
        return HttpStatus.OK;
    }

}
