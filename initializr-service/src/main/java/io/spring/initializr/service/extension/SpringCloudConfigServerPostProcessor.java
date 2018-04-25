package io.spring.initializr.service.extension;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.stereotype.Component;

@Component
public class SpringCloudConfigServerPostProcessor extends AbstractProjectRequestPostProcessor {

    private static final Dependency SPRING_BOOT_STARTER_WEB = Dependency.withId("spring-boot-starter-web",
            "org.springframework.boot", "spring-boot-starter-web");

    static final Dependency SPRING_CLOUD_CONFIG_SERVER = Dependency.withId(
            "spring-cloud-config-server", "org.springframework.cloud",
            "spring-cloud-config-server");

    @Override
    public void postProcessAfterResolution(ProjectRequest request,
                                           InitializrMetadata metadata) {
        if (!hasDependency(request, "spring-boot-starter-web")
                && hasDependency(request, "azure-config-server")) {
            request.getResolvedDependencies().add(SPRING_BOOT_STARTER_WEB);
        }
        if (!hasDependency(request, "spring-cloud-config-server")
                && hasDependency(request, "azure-config-server")) {
            request.getResolvedDependencies().add(SPRING_CLOUD_CONFIG_SERVER);
        }
        request.removeDependency("azure-config-server");
    }

}
