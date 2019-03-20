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

package io.spring.initializr.web.autoconfigure;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.spring.initializr.generator.version.VersionProperty;

/**
 * A {@link SimpleModule} that registers custom serializers.
 *
 * @author Stephane Nicoll
 */
class InitializrModule extends SimpleModule {

	InitializrModule() {
		super("initializr");
		addSerializer(new VersionPropertySerializer());
	}

	private static class VersionPropertySerializer
			extends StdSerializer<VersionProperty> {

		VersionPropertySerializer() {
			super(VersionProperty.class);
		}

		@Override
		public void serialize(VersionProperty value, JsonGenerator gen,
				SerializerProvider provider) throws IOException {
			gen.writeString(value.toStandardFormat());
		}

	}

}
