#!/bin/bash

( cd docker/conan-server && docker build -t xtuml/conan-server . )
( cd docker/masl-dev && docker build -t xtuml/masl-dev . )

docker stop conan-server
docker rm conan-server
docker run --detach --name conan-server --network conan-server --mount source=ConanServer,target=/conan-data xtuml/conan-server

docker run --rm --network conan-server --mount source=ConanCache,target=/conan-cache --mount type=bind,target=/work,source=$PWD/conan-helper xtuml/masl-dev conan-publish
docker run --rm --network conan-server --mount source=ConanCache,target=/conan-cache --mount type=bind,target=/work,source=$PWD/core-cpp xtuml/masl-dev conan-publish
docker run --rm --network conan-server --mount source=ConanCache,target=/conan-cache --mount type=bind,target=/work,source=$PWD/core-java xtuml/masl-dev conan-publish
docker run --rm --network conan-server --mount source=ConanCache,target=/conan-cache --mount type=bind,target=/work,source=$PWD/utils xtuml/masl-dev conan-publish
