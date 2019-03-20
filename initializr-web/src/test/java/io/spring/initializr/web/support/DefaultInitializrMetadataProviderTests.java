/*
 * Copyright 2012-2019 the original author or authors.
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
		InitializrMetadataUpdateStrategy updateStrategy = mock(
				InitializrMetadataUpdateStrategy.class);
		given(updateStrategy.update(metadata)).willReturn(updatedMetadata);
		DefaultInitializrMetadataProvider provider = new DefaultInitializrMetadataProvider(
				metadata, updateStrategy);
		assertThat(provider.get()).isEqualTo(updatedMetadata);
		verify(updateStrategy).update(metadata);
	}

}
