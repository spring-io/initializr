#!/bin/bash
set -e

source $(dirname $0)/common.sh
repository=$(pwd)/distribution-repository
if [ -z "$(ls -A maven)" ]; then
   echo "Maven cache not available."
else
   echo "Maven cache found."
fi
pushd git-repo > /dev/null
./mvnw clean deploy -U -Pfull -DaltDeploymentRepository=distribution::default::file://${repository}
popd > /dev/null
