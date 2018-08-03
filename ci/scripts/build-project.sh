#!/bin/bash
set -e

repository=$(pwd)/distribution-repository

pushd git-repo > /dev/null
./mvnw clean deploy -U -Pfull -DaltDeploymentRepository=distribution::default::file://${repository}
popd > /dev/null
