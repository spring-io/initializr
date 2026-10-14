/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.support;

import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link DefaultInitializrMetadataProvider}.
 *
 * @author Stephane Nicoll
 */
class DefaultInitializrMetadataProviderTests {

	@Test
	void strategyIsInvokedOnGet() {
		InitializrMetadata metadata = mock(InitializrMetadata.class);
		InitializrMetadata updatedMetadata = mock(InitializrMetadata.class);
		InitializrMetadataUpdateStrategy updateStrategy = mock(InitializrMetadataUpdateStrategy.class);
		given(updateStrategy.update(metadata)).willReturn(updatedMetadata);
		DefaultInitializrMetadataProvider provider = new DefaultInitializrMetadataProvider(metadata, updateStrategy);
		assertThat(provider.get()).isEqualTo(updatedMetadata);
		verify(updateStrategy).update(metadata);
	}

}
