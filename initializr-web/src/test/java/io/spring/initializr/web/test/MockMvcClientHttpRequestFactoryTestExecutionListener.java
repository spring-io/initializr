/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.test;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Dave Syer
 */
public final class MockMvcClientHttpRequestFactoryTestExecutionListener extends AbstractTestExecutionListener {

	private @Nullable MockMvcClientHttpRequestFactory factory;

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
		ConfigurableBeanFactory beanFactory = (ConfigurableBeanFactory) testContext.getApplicationContext()
			.getAutowireCapableBeanFactory();
		if (!beanFactory.containsBean("mockMvcClientHttpRequestFactory")) {
			this.factory = new MockMvcClientHttpRequestFactory(beanFactory.getBean(MockMvc.class));
			beanFactory.registerSingleton("mockMvcClientHttpRequestFactory", this.factory);
		}
		else {
			this.factory = beanFactory.getBean("mockMvcClientHttpRequestFactory",
					MockMvcClientHttpRequestFactory.class);
		}
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
		if (this.factory != null) {
			this.factory.setTest(testContext.getTestClass(), testContext.getTestMethod());
		}
	}

}
