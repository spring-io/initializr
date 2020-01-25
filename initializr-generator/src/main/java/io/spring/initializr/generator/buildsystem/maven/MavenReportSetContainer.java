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

package io.spring.initializr.generator.buildsystem.maven;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MavenReportSetContainer {

	private final Map<String, MavenReportSet.Builder> reportSets = new LinkedHashMap<>();

	public boolean isEmpty() {
		return this.reportSets.isEmpty();
	}

	public Stream<MavenReportSet> values() {
		return this.reportSets.values().stream().map(MavenReportSet.Builder::build);
	}

	public MavenReportSetContainer add(String id) {
		createReportSetBuilder(id);
		return this;
	}

	public boolean has(String id) {
		return this.reportSets.containsKey(id);
	}

	public MavenReportSetContainer add(String id, Consumer<MavenReportSet.Builder> profileBuilder) {
		profileBuilder.accept(createReportSetBuilder(id));
		return this;
	}

	public boolean remove(String id) {
		return this.reportSets.remove(id) != null;
	}

	private MavenReportSet.Builder createReportSetBuilder(String id) {
		MavenReportSet.Builder reportSetBuilder = this.reportSets.get(id);
		if (reportSetBuilder == null) {
			MavenReportSet.Builder builder = new MavenReportSet.Builder(id);
			this.reportSets.put(id, builder);
			return builder;
		}
		else {
			return reportSetBuilder;
		}
	}

}
