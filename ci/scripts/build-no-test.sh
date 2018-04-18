#!/usr/bin/env bash
set -e

cd initializr-service/initializr-service
../mvnw package -DskipTests
mv target/*.jar ../release
