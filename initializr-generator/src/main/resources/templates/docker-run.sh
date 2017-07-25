#!/usr/bin/env bash
#Used to build and run docker image
./docker.sh
if [ $? -eq 0 ]
then
    docker rm -f boot_{{artifactId}}
    docker run -d -it --restart always -p 8080:8080 --name boot_{{artifactId}} org.gr8conf.us/docker/boot_{{artifactId}}
fi