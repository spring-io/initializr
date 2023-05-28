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
package io.spring.initializr.web.test;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Dave Syer
 */
public final class MockMvcClientHttpRequestFactoryTestExecutionListener extends AbstractTestExecutionListener {

    private MockMvcClientHttpRequestFactory factory;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) testContext.getApplicationContext().getAutowireCapableBeanFactory();
        if (!beanFactory.containsBean("mockMvcClientHttpRequestFactory")) {
            this.factory = new MockMvcClientHttpRequestFactory(beanFactory.getBean(MockMvc.class));
            beanFactory.registerSingleton("mockMvcClientHttpRequestFactory", this.factory);
        } else {
            this.factory = beanFactory.getBean("mockMvcClientHttpRequestFactory", MockMvcClientHttpRequestFactory.class);
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (this.factory != null) {
            this.factory.setTest(testContext.getTestClass(), testContext.getTestMethod());
        }
    }
}
