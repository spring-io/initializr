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

import java.util.Objects;

import org.jspecify.annotations.Nullable;

/**
 * A port mapping for a Docker Compose service. Can represent a fixed mapping
 * ({@code host:container}) or a random host port mapping ({@code container} only).
 *
 * @author Eduardo Rangel
 */
public final class PortMapping implements Comparable<PortMapping> {

	/**
	 * The host port, or null for random port mapping.
	 */
	private final @Nullable Integer hostPort;

	/**
	 * The container port.
	 */
	private final int containerPort;

	private PortMapping(@Nullable Integer host, int container) {
		this.hostPort = host;
		this.containerPort = container;
	}

	/**
	 * Returns the host port, or null if random port mapping.
	 * @return the host port or null
	 */
	public @Nullable Integer getHostPort() {
		return this.hostPort;
	}

	/**
	 * Returns the container port.
	 * @return the container port
	 */
	public int getContainerPort() {
		return this.containerPort;
	}

	/**
	 * Returns whether this is a fixed port mapping.
	 * @return true if this is a fixed port mapping
	 */
	public boolean isFixed() {
		return this.hostPort != null;
	}

	@Override
	public int compareTo(PortMapping other) {
		// Sort by container port first, then by host port
		int containerComparison = Integer.compare(this.containerPort, other.containerPort);
		if (containerComparison != 0) {
			return containerComparison;
		}
		// If both are random or both are fixed with same host port,
		// they're equal
		if (this.hostPort == null && other.hostPort == null) {
			return 0;
		}
		if (this.hostPort == null) {
			return -1;
		}
		if (other.hostPort == null) {
			return 1;
		}
		return Integer.compare(this.hostPort, other.hostPort);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PortMapping that = (PortMapping) o;
		return this.containerPort == that.containerPort && Objects.equals(this.hostPort, that.hostPort);
	}

	@Override
	public int hashCode() {
		int result = (this.hostPort != null) ? this.hostPort.hashCode() : 0;
		result = 31 * result + this.containerPort;
		return result;
	}

	@Override
	public String toString() {
		return this.isFixed() ? this.hostPort + ":" + this.containerPort : String.valueOf(this.containerPort);
	}

	/**
	 * Creates a port mapping with a random host port.
	 * @param containerPort the container port
	 * @return a new PortMapping instance
	 */
	public static PortMapping random(int containerPort) {
		return new PortMapping(null, containerPort);
	}

	/**
	 * Creates a port mapping with a fixed host port.
	 * @param hostPort the host port
	 * @param containerPort the container port
	 * @return a new PortMapping instance
	 */
	public static PortMapping fixed(int hostPort, int containerPort) {
		return new PortMapping(hostPort, containerPort);
	}

}
