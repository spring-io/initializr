/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.service.info;

import java.util.Map;

import org.junit.Test;

import org.springframework.boot.actuate.info.Info;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Stephane Nicoll
 */
public class CloudFoundryInfoContributorTests {

	private final MockEnvironment environment = new MockEnvironment();

	@Test
	public void noVcap() {
		Info info = getInfo(this.environment);
		assertThat(info.getDetails()).isEmpty();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void applicationName() {
		this.environment.setProperty("vcap.application.name", "foo-bar");
		Info info = getInfo(this.environment);
		assertThat(info.getDetails()).containsOnlyKeys("app");
		Object appDetails = info.getDetails().get("app");
		assertThat(appDetails).isInstanceOf(Map.class);
		assertThat((Map<String, Object>) appDetails)
				.containsOnly(entry("name", "foo-bar"));
	}

	private static Info getInfo(Environment env) {
		Info.Builder builder = new Info.Builder();
		new CloudFoundryInfoContributor(env).contribute(builder);
		return builder.build();
	}

}
