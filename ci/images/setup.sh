#!/bin/bash
set -ex

###########################################################
# UTILS
###########################################################

apt-get update
apt-get install --no-install-recommends -y ca-certificates net-tools libxml2-utils git curl libudev1 libxml2-utils iptables iproute2 jq
rm -rf /var/lib/apt/lists/*

curl https://raw.githubusercontent.com/spring-io/concourse-java-scripts/v0.0.2/concourse-java.sh > /opt/concourse-java.sh


###########################################################
# JAVA
###########################################################
JDK_URL=https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u202-b08/OpenJDK8U-jdk_x64_linux_hotspot_8u202b08.tar.gz

mkdir -p /opt/openjdk
cd /opt/openjdk
curl -L ${JDK_URL} | tar zx --strip-components=1
test -f /opt/openjdk/bin/java
test -f /opt/openjdk/bin/javac