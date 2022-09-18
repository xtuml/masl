#!/bin/bash
VERSION=v1
#ARCH=linux-x64
ARCH=linux-aarch64

# build images
docker build -t levistarrett/masl-base:$VERSION -f docker/Dockerfile.masl-base . --build-arg ARCH=$ARCH && \
docker build -t levistarrett/masl-build:$VERSION -f docker/Dockerfile.masl-build . && \
docker build -t levistarrett/masl-compiler:$VERSION -f docker/Dockerfile.masl-compiler . && \
docker build -t levistarrett/masl-exe:$VERSION -f docker/Dockerfile.masl-exe .

# tag latest
docker tag levistarrett/masl-base:$VERSION levistarrett/masl-base:latest
docker tag levistarrett/masl-build:$VERSION levistarrett/masl-build:latest
docker tag levistarrett/masl-compiler:$VERSION levistarrett/masl-compiler:latest
docker tag levistarrett/masl-exe:$VERSION levistarrett/masl-exe:latest

exit

# push images
docker push levistarrett/masl-base:$VERSION
docker push levistarrett/masl-build:$VERSION
docker push levistarrett/masl-compiler:$VERSION
docker push levistarrett/masl-exe:$VERSION 
docker push levistarrett/masl-base:latest
docker push levistarrett/masl-build:latest
docker push levistarrett/masl-compiler:latest
docker push levistarrett/masl-exe:latest 
