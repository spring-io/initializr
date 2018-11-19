#!/bin/bash
set -e

source $(dirname $0)/common.sh
repository=$(pwd)/distribution-repository

pushd git-repo > /dev/null
git fetch --tags --all > /dev/null
popd > /dev/null

git clone git-repo stage-git-repo > /dev/null

pushd stage-git-repo > /dev/null

snapshotVersion=$( get_revision_from_pom )
stageVersion=$( get_next_release $snapshotVersion)
nextVersion=$( bump_version_number $snapshotVersion)
echo "Staging $stageVersion (next version will be $nextVersion)"

set_revision_to_pom "$stageVersion"
git config user.name "Spring Buildmaster" > /dev/null
git config user.email "buildmaster@springframework.org" > /dev/null
git add pom.xml > /dev/null
git commit -m"Release v$stageVersion" > /dev/null
git tag -a "v$stageVersion" -m"Release v$stageVersion" > /dev/null

./mvnw clean deploy -U -Pfull -DaltDeploymentRepository=distribution::default::file://${repository}

git reset --hard HEAD^ > /dev/null
if [[ $nextVersion != $snapshotVersion ]]; then
	echo "Setting next development version (v$nextVersion)"
	set_revision_to_pom "$nextVersion"
	git add pom.xml > /dev/null
	git commit -m"Next development version (v$nextVersion)" > /dev/null
fi;

echo "DONE"

popd > /dev/null
