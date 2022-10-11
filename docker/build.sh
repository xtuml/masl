#!/bin/bash
VERSION=v1
ARCH=amd64
#ARCH=arm64

# build images
docker build -t levistarrett/masl-base:latest -f docker/Dockerfile.masl-base . --build-arg TARGETARCH=$ARCH && \
docker build -t levistarrett/masl-build:latest -f docker/Dockerfile.masl-build . && \
docker build -t levistarrett/masl-compiler:latest -f docker/Dockerfile.masl-compiler . && \
docker build -t levistarrett/masl-exe:latest -f docker/Dockerfile.masl-exe .

# tag latest
docker tag levistarrett/masl-base:latest levistarrett/masl-base:$VERSION
docker tag levistarrett/masl-build:latest levistarrett/masl-build:$VERSION
docker tag levistarrett/masl-compiler:latest levistarrett/masl-compiler:$VERSION
docker tag levistarrett/masl-exe:latest levistarrett/masl-exe:$VERSION

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
