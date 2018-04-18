#!/usr/bin/env bash
set -e

cd initializr-service
./mvnw install
cd initializr-service
../mvnw package -DskipTests
mv target/*.jar ../../release
