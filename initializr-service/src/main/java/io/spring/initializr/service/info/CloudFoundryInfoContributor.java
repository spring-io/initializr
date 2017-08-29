/*
 * Copyright 2012-2017 the original author or authors.
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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * An {@link InfoContributor} that exposes the name of the app. Useful in a blue/green
 * deployment scenario as the name of the app provides a hint to that.
 *
 * @author Stephane Nicoll
 */
@Component
public class CloudFoundryInfoContributor implements InfoContributor {

	private final Environment environment;

	public CloudFoundryInfoContributor(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void contribute(Info.Builder builder) {
		String applicationName = this.environment.getProperty("vcap.application.name");
		if (StringUtils.hasText(applicationName)) {
			Map<String, String> details = new LinkedHashMap<>();
			details.put("name", applicationName);
			builder.withDetail("app", details);
		}
	}

}
