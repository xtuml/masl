#!/bin/bash

repo=${1:=testing}

docker run --rm  -v conan-cache:/conan-cache -v $PWD:/work levistarrett/masl-dev:latest ./build-all.sh ${repo} ${ARTIFACTORY_USERNAME} ${ARTIFACTORY_TOKEN}

