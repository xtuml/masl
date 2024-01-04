#!/bin/bash

set -e
base_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

function masl-dev {
	docker compose -f ${base_dir}/docker/docker-compose.yml run -e ARTIFACTORY_USERNAME=${ARTIFACTORY_USERNAME} -e ARTIFACTORY_TOKEN=${ARTIFACTORY_TOKEN} --rm -v $PWD:/work masl-dev "$@"
}

(cd conan-helper && masl-dev conan-publish)
(cd core-cpp && masl-dev conan-publish)
(cd core-java && masl-dev conan-publish)
(cd utils && masl-dev conan-publish)
(cd inspector && masl-dev conan-publish)
# (cd examples/petrol && masl-dev conan-publish) LPS not working
(cd examples/calculator && masl-dev conan-publish)
