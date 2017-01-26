/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.actuate.test

import org.junit.Assume
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory

/**
 * A {@link org.junit.rules.TestRule} that validates Redis is available.
 *
 * @author Dave Syer
 * @since 1.0
 */
class RedisRunning extends TestWatcher {

	JedisConnectionFactory connectionFactory;

	@Override
	Statement apply(Statement base, Description description) {
		if (connectionFactory == null) {
			connectionFactory = new JedisConnectionFactory()
			connectionFactory.afterPropertiesSet()
		}
		try {
			connectionFactory.connection
		} catch (Exception e) {
			Assume.assumeNoException('Cannot connect to Redis (so skipping tests)', e)
		}
		super.apply(base, description)
	}

}
