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

package io.spring.initializr.actuate.stat;

import java.util.ArrayList;
import java.util.List;

/**
 * Define the statistics of a project generation.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestDocument {

	private long generationTimestamp;

	private String requestIp;
	private String requestIpv4;
	private String requestCountry;
	private String clientId;
	private String clientVersion;

	private String groupId;
	private String artifactId;
	private String packageName;
	private String bootVersion;
	private String javaVersion;
	private String language;
	private String packaging;
	private String type;
	private final List<String> dependencies = new ArrayList<>();

	private String errorMessage;
	private boolean invalid;
	private boolean invalidJavaVersion;
	private boolean invalidLanguage;
	private boolean invalidPackaging;
	private boolean invalidType;
	private final List<String> invalidDependencies = new ArrayList<>();

	public long getGenerationTimestamp() {
		return generationTimestamp;
	}

	public void setGenerationTimestamp(long generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	public String getRequestIpv4() {
		return requestIpv4;
	}

	public void setRequestIpv4(String requestIpv4) {
		this.requestIpv4 = requestIpv4;
	}

	public String getRequestCountry() {
		return requestCountry;
	}

	public void setRequestCountry(String requestCountry) {
		this.requestCountry = requestCountry;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getBootVersion() {
		return bootVersion;
	}

	public void setBootVersion(String bootVersion) {
		this.bootVersion = bootVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isInvalid() {
		return invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isInvalidJavaVersion() {
		return invalidJavaVersion;
	}

	public void setInvalidJavaVersion(boolean invalidJavaVersion) {
		this.invalidJavaVersion = invalidJavaVersion;
	}

	public boolean isInvalidLanguage() {
		return invalidLanguage;
	}

	public void setInvalidLanguage(boolean invalidLanguage) {
		this.invalidLanguage = invalidLanguage;
	}

	public boolean isInvalidPackaging() {
		return invalidPackaging;
	}

	public void setInvalidPackaging(boolean invalidPackaging) {
		this.invalidPackaging = invalidPackaging;
	}

	public boolean isInvalidType() {
		return invalidType;
	}

	public void setInvalidType(boolean invalidType) {
		this.invalidType = invalidType;
	}

	public List<String> getDependencies() {
		return dependencies;
	}

	public List<String> getInvalidDependencies() {
		return invalidDependencies;
	}

	@Override
	public String toString() {
		return "ProjectRequestDocument [generationTimestamp=" + generationTimestamp + ", "
				+ (requestIp != null ? "requestIp=" + requestIp + ", " : "")
				+ (requestIpv4 != null ? "requestIpv4=" + requestIpv4 + ", " : "")
				+ (requestCountry != null ? "requestCountry=" + requestCountry + ", "
						: "")
				+ (clientId != null ? "clientId=" + clientId + ", " : "")
				+ (clientVersion != null ? "clientVersion=" + clientVersion + ", " : "")
				+ (groupId != null ? "groupId=" + groupId + ", " : "")
				+ (artifactId != null ? "artifactId=" + artifactId + ", " : "")
				+ (packageName != null ? "packageName=" + packageName + ", " : "")
				+ (bootVersion != null ? "bootVersion=" + bootVersion + ", " : "")
				+ (javaVersion != null ? "javaVersion=" + javaVersion + ", " : "")
				+ (language != null ? "language=" + language + ", " : "")
				+ (packaging != null ? "packaging=" + packaging + ", " : "")
				+ (type != null ? "type=" + type + ", " : "")
				+ (dependencies != null ? "dependencies=" + dependencies + ", " : "")
				+ (errorMessage != null ? "errorMessage=" + errorMessage + ", " : "")
				+ "invalid=" + invalid + ", invalidJavaVersion=" + invalidJavaVersion
				+ ", invalidLanguage=" + invalidLanguage + ", invalidPackaging="
				+ invalidPackaging + ", invalidType=" + invalidType + ", "
				+ (invalidDependencies != null
						? "invalidDependencies=" + invalidDependencies : "")
				+ "]";
	}

}
