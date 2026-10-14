/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.autoconfigure;

import java.io.Serial;

import io.spring.initializr.generator.version.VersionProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * A {@link SimpleModule} that registers custom serializers.
 *
 * @author Stephane Nicoll
 */
class InitializrModule extends SimpleModule {

	@Serial
	private static final long serialVersionUID = 1L;

	InitializrModule() {
		super("initializr");
		addSerializer(new VersionPropertySerializer());
	}

	private static class VersionPropertySerializer extends StdSerializer<VersionProperty> {

		VersionPropertySerializer() {
			super(VersionProperty.class);
		}

		@Override
		public void serialize(VersionProperty value, JsonGenerator gen, SerializationContext provider)
				throws JacksonException {
			gen.writeString(value.toStandardFormat());
		}

	}

}
