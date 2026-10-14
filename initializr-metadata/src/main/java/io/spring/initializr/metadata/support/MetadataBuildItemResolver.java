/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata.support;

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.jspecify.annotations.Nullable;

/**
 * A {@link BuildItemResolver} that uses the {@link InitializrMetadata} to resolve build
 * items against a given platform {@link Version}.
 *
 * @author Stephane Nicoll
 */
public final class MetadataBuildItemResolver implements BuildItemResolver {

	private final InitializrMetadata metadata;

	private final Version platformVersion;

	/**
	 * Creates an instance for the specified {@link InitializrMetadata} and {@link Version
	 * platform version}.
	 * @param metadata the metadata to use
	 * @param platformVersion the platform version to consider
	 */
	public MetadataBuildItemResolver(InitializrMetadata metadata, Version platformVersion) {
		this.metadata = metadata;
		this.platformVersion = platformVersion;
	}

	@Override
	public @Nullable Dependency resolveDependency(String id) {
		io.spring.initializr.metadata.Dependency dependency = this.metadata.getDependencies().get(id);
		if (dependency != null) {
			return MetadataBuildItemMapper.toDependency(dependency.resolve(this.platformVersion));
		}
		return null;
	}

	@Override
	public @Nullable BillOfMaterials resolveBom(String id) {
		io.spring.initializr.metadata.BillOfMaterials bom = this.metadata.getConfiguration().getEnv().getBoms().get(id);
		if (bom != null) {
			return MetadataBuildItemMapper.toBom(bom.resolve(this.platformVersion));
		}
		return null;
	}

	@Override
	public @Nullable MavenRepository resolveRepository(String id) {
		if (id.equals(MavenRepository.MAVEN_CENTRAL.getId())) {
			return MavenRepository.MAVEN_CENTRAL;
		}
		return MetadataBuildItemMapper.toRepository(id,
				this.metadata.getConfiguration().getEnv().getRepositories().get(id));
	}

}
