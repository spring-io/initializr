package io.spring.initializr.generator;

import io.spring.initializr.test.generator.ProjectAssert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;

public class CustomProjectGeneratorTests extends AbstractProjectGeneratorTests {

    public CustomProjectGeneratorTests() {
        super(new MyProjectGenerator());
    }

    @Test
    public void jenkinsfileMaven() {
        ProjectRequest request = createProjectRequest();
        request.setType("maven-project");
        ProjectAssert project = generateProject(request);
        project.sourceCodeAssert("Jenkinsfile")
                .equalsTo(new ClassPathResource("project/maven/Jenkinsfile"));
    }

    @Test
    public void jenkinsfileGradle() {
        ProjectRequest request = createProjectRequest();
        request.setType("gradle-build");
        ProjectAssert project = generateProject(request);
        project.hasNoFile("Jenkinsfile");
    }

    @Test
    public void projectGenerationEventAfterGeneratingAllFiles() throws Exception {
        ProjectRequest request = createProjectRequest("web");
        generateProject(request);
        verifyProjectSuccessfulEventFor(request);

        Runnable jenkinsfileGenerated = ((MyProjectGenerator) projectGenerator).jenkinsfileGenerated;
        InOrder inOrder = Mockito.inOrder(eventPublisher, jenkinsfileGenerated);

        inOrder.verify(jenkinsfileGenerated, times(1)).run();
        inOrder.verify(eventPublisher, times(1)).publishEvent(argThat(new ProjectGeneratedEventMatcher(request)));
    }

    private static class MyProjectGenerator extends ProjectGenerator {
        protected final Runnable jenkinsfileGenerated = Mockito.mock(Runnable.class);

        @Override
        protected File doGenerateProjectStructure(ProjectRequest request) {
            File dir = super.doGenerateProjectStructure(request);
            if (isMavenBuild(request)) {
                write(new File(dir, "Jenkinsfile"), "Jenkinsfile.tmpl", resolveModel(request));
                jenkinsfileGenerated.run();
            }
            return dir;
        }
    }
}
