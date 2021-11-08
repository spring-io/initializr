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

package io.spring.initializr.generator.spring.scm.git;

/**
 * Default configurations for gitignore file.
 * @author Yves Galvão
 */
public enum GitEnum {

	/**
	 * STS value of GitignoreSection.
	 */
	STS("STS", new String[] { ".apt_generated", ".classpath", ".factorypath", ".project", ".settings", ".springBeans",
			".sts4-cache" }),
	/**
	 * Intellij IDEA value of GitignoreSection.
	 */
	INTELLIJ_IDEA("IntelliJ IDEA", new String[] { ".idea", "*.iws", "*.iml", "*.ipr" }),
	/**
	 * Netbeans value of GitignoreSection.
	 */
	NET_BEANS("NetBeans", new String[] { "/nbproject/private/", "/nbbuild/", "/dist/", "/nbdist/", "/.nb-gradle/" }),
	/**
	 * VS Code value of GitignoreSection.
	 */
	VS_CODE("VS Code", new String[] { ".vscode/" }),
	/**
	 * General value of GitignoreSection.
	 */
	GENERAL(null, null);

	/**
	 * List of ignore values.
	 */
	String[] ignores;

	/**
	 * Description name of gitignore section.
	 */
	String name;

	GitEnum(String name, String[] asList) {
		this.ignores = asList;
		this.name = name;
	}

	public String[] getIgnores() {
		return this.ignores;
	}

	public String getName() {
		return this.name;
	}

}
