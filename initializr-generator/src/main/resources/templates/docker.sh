#!/usr/bin/env bash

#Used to build docker image
mvn package
if [ $? -eq 1 ]
then
    exit 1
else
    docker build -t org.gr8conf.us/docker/boot_{{artifactId}} .
fi