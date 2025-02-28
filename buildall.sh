#!/bin/bash

docker run --rm  -v conan-cache:/conan-cache -v $PWD:/work levistarrett/masl-dev:latest ./build-all.sh xtuml ${ARTIFACTORY_USERNAME} ${ARTIFACTORY_TOKEN}

