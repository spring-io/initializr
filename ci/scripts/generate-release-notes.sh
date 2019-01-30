#!/bin/bash
set -e

version=$( cat version/version )

milestone=${version%.RELEASE}

java -jar /github-release-notes-generator.jar \
  --releasenotes.github.username=${GITHUB_USERNAME} \
  --releasenotes.github.password=${GITHUB_TOKEN} \
  --releasenotes.github.organization=${GITHUB_ORGANIZATION} \
  --releasenotes.github.repository=${GITHUB_REPO}  \
  ${milestone} generated-release-notes/release-notes.md

echo ${version} > generated-release-notes/version
echo v${version} > generated-release-notes/tag
