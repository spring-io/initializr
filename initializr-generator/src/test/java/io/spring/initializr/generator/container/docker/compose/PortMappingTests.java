/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.container.docker.compose;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PortMapping}.
 *
 * @author Eduardo Rangel
 */
class PortMappingTests {

	@Test
	void randomPortMapping() {
		PortMapping portMapping = PortMapping.random(8080);
		assertThat(portMapping.getContainerPort()).isEqualTo(8080);
		assertThat(portMapping.getHostPort()).isNull();
		assertThat(portMapping.isFixed()).isFalse();
		assertThat(portMapping.toString()).isEqualTo("8080");
	}

	@Test
	void fixedPortMapping() {
		PortMapping portMapping = PortMapping.fixed(3000, 3000);
		assertThat(portMapping.getContainerPort()).isEqualTo(3000);
		assertThat(portMapping.getHostPort()).isEqualTo(3000);
		assertThat(portMapping.isFixed()).isTrue();
		assertThat(portMapping.toString()).isEqualTo("3000:3000");
	}

	@Test
	void fixedPortMappingWithDifferentPorts() {
		PortMapping portMapping = PortMapping.fixed(8080, 80);
		assertThat(portMapping.getContainerPort()).isEqualTo(80);
		assertThat(portMapping.getHostPort()).isEqualTo(8080);
		assertThat(portMapping.isFixed()).isTrue();
		assertThat(portMapping.toString()).isEqualTo("8080:80");
	}

	@Test
	void compareToSortsByContainerPortFirst() {
		PortMapping port80 = PortMapping.random(80);
		PortMapping port8080 = PortMapping.random(8080);
		assertThat(port80.compareTo(port8080)).isLessThan(0);
		assertThat(port8080.compareTo(port80)).isGreaterThan(0);
	}

	@Test
	void compareToSortsRandomBeforeFixedWhenSameContainerPort() {
		PortMapping random = PortMapping.random(8080);
		PortMapping fixed = PortMapping.fixed(8080, 8080);
		assertThat(random.compareTo(fixed)).isLessThan(0);
		assertThat(fixed.compareTo(random)).isGreaterThan(0);
	}

	@Test
	void equalsSameRandomPorts() {
		PortMapping port1 = PortMapping.random(8080);
		PortMapping port2 = PortMapping.random(8080);
		assertThat(port1).isEqualTo(port2);
		assertThat(port1.hashCode()).isEqualTo(port2.hashCode());
	}

	@Test
	void equalsSameFixedPorts() {
		PortMapping port1 = PortMapping.fixed(3000, 3000);
		PortMapping port2 = PortMapping.fixed(3000, 3000);
		assertThat(port1).isEqualTo(port2);
		assertThat(port1.hashCode()).isEqualTo(port2.hashCode());
	}

	@Test
	void notEqualsRandomVsFixed() {
		PortMapping random = PortMapping.random(8080);
		PortMapping fixed = PortMapping.fixed(8080, 8080);
		assertThat(random).isNotEqualTo(fixed);
	}

	@Test
	void notEqualsDifferentContainerPorts() {
		PortMapping port80 = PortMapping.random(80);
		PortMapping port8080 = PortMapping.random(8080);
		assertThat(port80).isNotEqualTo(port8080);
	}

	@Test
	void notEqualsDifferentHostPorts() {
		PortMapping port1 = PortMapping.fixed(3000, 3000);
		PortMapping port2 = PortMapping.fixed(8080, 3000);
		assertThat(port1).isNotEqualTo(port2);
	}

}
