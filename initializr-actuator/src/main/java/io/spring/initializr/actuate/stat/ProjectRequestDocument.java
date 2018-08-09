/*
 * Copyright 2012-2018 the original author or authors.
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
		return this.generationTimestamp;
	}

	public void setGenerationTimestamp(long generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

	public String getRequestIp() {
		return this.requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	public String getRequestIpv4() {
		return this.requestIpv4;
	}

	public void setRequestIpv4(String requestIpv4) {
		this.requestIpv4 = requestIpv4;
	}

	public String getRequestCountry() {
		return this.requestCountry;
	}

	public void setRequestCountry(String requestCountry) {
		this.requestCountry = requestCountry;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientVersion() {
		return this.clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getBootVersion() {
		return this.bootVersion;
	}

	public void setBootVersion(String bootVersion) {
		this.bootVersion = bootVersion;
	}

	public String getJavaVersion() {
		return this.javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPackaging() {
		return this.packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isInvalid() {
		return this.invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isInvalidJavaVersion() {
		return this.invalidJavaVersion;
	}

	public void setInvalidJavaVersion(boolean invalidJavaVersion) {
		this.invalidJavaVersion = invalidJavaVersion;
	}

	public boolean isInvalidLanguage() {
		return this.invalidLanguage;
	}

	public void setInvalidLanguage(boolean invalidLanguage) {
		this.invalidLanguage = invalidLanguage;
	}

	public boolean isInvalidPackaging() {
		return this.invalidPackaging;
	}

	public void setInvalidPackaging(boolean invalidPackaging) {
		this.invalidPackaging = invalidPackaging;
	}

	public boolean isInvalidType() {
		return this.invalidType;
	}

	public void setInvalidType(boolean invalidType) {
		this.invalidType = invalidType;
	}

	public List<String> getDependencies() {
		return this.dependencies;
	}

	public List<String> getInvalidDependencies() {
		return this.invalidDependencies;
	}

	@Override
	public String toString() {
		return "ProjectRequestDocument [generationTimestamp=" + this.generationTimestamp
				+ ", "
				+ (this.requestIp != null ? "requestIp=" + this.requestIp + ", " : "")
				+ (this.requestIpv4 != null ? "requestIpv4=" + this.requestIpv4 + ", "
						: "")
				+ (this.requestCountry != null
						? "requestCountry=" + this.requestCountry + ", " : "")
				+ (this.clientId != null ? "clientId=" + this.clientId + ", " : "")
				+ (this.clientVersion != null
						? "clientVersion=" + this.clientVersion + ", " : "")
				+ (this.groupId != null ? "groupId=" + this.groupId + ", " : "")
				+ (this.artifactId != null ? "artifactId=" + this.artifactId + ", " : "")
				+ (this.packageName != null ? "packageName=" + this.packageName + ", "
						: "")
				+ (this.bootVersion != null ? "bootVersion=" + this.bootVersion + ", "
						: "")
				+ (this.javaVersion != null ? "javaVersion=" + this.javaVersion + ", "
						: "")
				+ (this.language != null ? "language=" + this.language + ", " : "")
				+ (this.packaging != null ? "packaging=" + this.packaging + ", " : "")
				+ (this.type != null ? "type=" + this.type + ", " : "")
				+ (this.dependencies != null ? "dependencies=" + this.dependencies + ", "
						: "")
				+ (this.errorMessage != null ? "errorMessage=" + this.errorMessage + ", "
						: "")
				+ "invalid=" + this.invalid + ", invalidJavaVersion="
				+ this.invalidJavaVersion + ", invalidLanguage=" + this.invalidLanguage
				+ ", invalidPackaging=" + this.invalidPackaging + ", invalidType="
				+ this.invalidType + ", " + (this.invalidDependencies != null
						? "invalidDependencies=" + this.invalidDependencies : "")
				+ "]";
	}

}
