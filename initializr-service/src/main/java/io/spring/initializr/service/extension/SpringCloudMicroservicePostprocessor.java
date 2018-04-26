package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SpringCloudMicroservicePostprocessor extends AbstractProjectRequestPostProcessor implements ProjectRequestPostProcessor {
    private static final Map<String, List<String>> serviceToDependencies = new HashMap<>();

    static {
        serviceToDependencies.put("config", Arrays.asList("cloud-config-server", "cloud-eureka"));
        serviceToDependencies.put("gateway", Arrays.asList("cloud-gateway"));
        serviceToDependencies.put("discovery", Arrays.asList("cloud-eureka-server"));
    }

    @Override
    public void postProcessAfterResolution(ProjectRequest request,
                                           InitializrMetadata metadata) {
        for(String service: request.getServices()){
            if(!serviceToDependencies.containsKey(service)){
                throw new RuntimeException("Unsupported service type");
            }

            ProjectRequest subModule = new ProjectRequest(request);
            subModule.setName(service);
            subModule.setApplicationName(service);
            subModule.setArtifactId(request.getArtifactId() + "." + service);
            subModule.setDependencies(serviceToDependencies.get(service));
            subModule.setBaseDir(service);
            subModule.resolve(metadata);
            request.addModule(subModule);
        }
    }
}
