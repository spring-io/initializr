/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.initializr.generator.condition;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.project.ProjectDescription;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link ProjectGenerationCondition Condition} implementation for
 * {@link ConditionalOnLanguage}.
 *
 * @author Andy Wilkinson
 */
class OnLanguageCondition extends ProjectGenerationCondition {

    @Override
    protected boolean matches(ProjectDescription description, ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (description.getLanguage() == null) {
            return false;
        }
        String languageId = (String) metadata.getAllAnnotationAttributes(ConditionalOnLanguage.class.getName()).getFirst("value");
        Language language = Language.forId(languageId, null);
        return description.getLanguage().id().equals(language.id());
    }
}
