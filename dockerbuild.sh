#!/bin/bash
docker run -it --rm --name maven-build -v ${HOME}/.m2:/root/.m2 -v ${PWD}:/usr/local/src -w /usr/local/src maven:3.6.2-jdk-11 mvn clean package