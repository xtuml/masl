#!/bin/bash
VERSION=v1
ARCH=amd64
#ARCH=arm64

# build images
docker compose -f docker/docker-compose.yml build conan-server
docker compose -f docker/docker-compose.yml build masl-dev
docker compose -f docker/docker-compose.yml build masl-populate

docker tag levistarrett/conan-server:latest levistarrett/conan-server:$VERSION
docker tag levistarrett/masl-dev:latest levistarrett/masl-dev:$VERSION
docker tag levistarrett/masl-populate:latest levistarrett/masl-populate:$VERSION

exit

# push images
docker push levistarrett/conan-server:$VERSION
docker push levistarrett/masl-dev:$VERSION
docker push levistarrett/masl-populate:$VERSION
docker push levistarrett/conan-server:latest
docker push levistarrett/masl-dev:latest
docker push levistarrett/masl-populate:latest
