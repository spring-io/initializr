package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpringCloudMicroservicePostprocessor extends AbstractProjectRequestPostProcessor implements ProjectRequestPostProcessor {
    private static final Map<String, List<String>> serviceToDependencies = new HashMap<>();

    static {
        serviceToDependencies.put("cloud-config-server", Arrays.asList("cloud-config-server"));
        serviceToDependencies.put("cloud-gateway", Arrays.asList("cloud-gateway"));
        serviceToDependencies.put("cloud-eureka-server", Arrays.asList("cloud-eureka-server"));
        serviceToDependencies.put("cloud-hystrix-dashboard", Arrays.asList("cloud-hystrix-dashboard"));
        serviceToDependencies.put("azure-service-bus", Arrays.asList("azure-service-bus"));
    }

    @Override
    public void postProcessAfterResolution(ProjectRequest request,
                                           InitializrMetadata metadata) {
        for(String service: request.getStyle()){
            if(!serviceToDependencies.containsKey(service)){
                throw new IllegalStateException("Unsupported service type " + service);
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
