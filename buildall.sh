#!/bin/bash

set -e
base_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

function masl-dev {
	docker compose -f ${base_dir}/docker/docker-compose.yml run -e ARTIFACTORY_USERNAME=${ARTIFACTORY_USERNAME} -e ARTIFACTORY_TOKEN=${ARTIFACTORY_TOKEN} --rm -v $PWD:/work masl-dev "$@"
}

( cd architecture/cmake-helpers && masl-dev conan-publish )
( cd architecture/logging && masl-dev conan-publish )
( cd architecture/amqp-client && masl-dev conan-publish )
( cd architecture/swa && masl-dev conan-publish )
( cd architecture/asn1 && masl-dev conan-publish )
( cd architecture/sockets && masl-dev conan-publish )
( cd architecture/transient && masl-dev conan-publish )
( cd architecture/sql && masl-dev conan-publish )
( cd architecture/sqlite && masl-dev conan-publish )
( cd architecture/kafka && masl-dev conan-publish )

( cd plugins/metadata && masl-dev conan-publish )
( cd plugins/inspectorServer && masl-dev conan-publish )
( cd plugins/threadtimer && masl-dev conan-publish )
( cd plugins/codecoverage && masl-dev conan-publish )
( cd plugins/backlogMonitor && masl-dev conan-publish )
( cd plugins/trace && masl-dev conan-publish )

(cd masl-conan && masl-dev conan-publish)
(cd masl-codegen && masl-dev conan-publish)
(cd utils/assertions && masl-dev conan-publish)
(cd utils/binary-io && masl-dev conan-publish)
(cd utils/command-line && masl-dev conan-publish)
(cd utils/environment && masl-dev conan-publish)
(cd utils/host && masl-dev conan-publish)
(cd utils/filesystem && masl-dev conan-publish)
(cd utils/format && masl-dev conan-publish)
(cd utils/hash && masl-dev conan-publish)
(cd utils/json && masl-dev conan-publish)
(cd utils/math && masl-dev conan-publish)
(cd utils/strings && masl-dev conan-publish)
(cd utils/uuid && masl-dev conan-publish)
(cd utils/regex && masl-dev conan-publish)
(cd utils/json-validation && masl-dev conan-publish)
(cd utils/logger && masl-dev conan-publish)
(cd utils/schedule && masl-dev conan-publish)
(cd utils/test && masl-dev conan-publish)

(cd examples/petrol && masl-dev  conan-publish)
(cd examples/calculator && masl-dev  conan-publish)

(cd inspector && masl-dev conan-publish)

(cd all && masl-dev conan install .  --lockfile-out conan.lockfile --build=missing)
