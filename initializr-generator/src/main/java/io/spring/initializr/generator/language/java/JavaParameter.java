package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.language.Annotatable;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Declaration of a method parameter written in Java.
 *
 * @author iMinusMinus
 */
public final class JavaParameter extends Parameter implements Annotatable {

    private final List<Annotation> annotations = new ArrayList<>();

    public JavaParameter(String type, String name) {
        super(type, name);
    }

    @Override
    public void annotate(Annotation annotation) {
        this.annotations.add(annotation);
    }

    @Override
    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(this.annotations);
    }
}
