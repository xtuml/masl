#!/bin/bash

base_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function masl-dev {
  docker compose -f ${base_dir}/docker/docker-compose.yml run --rm -v $PWD:/work command "$@"
}

( cd docker && docker compose build )

( cd conan-helper && masl-dev conan-publish )
( cd core-cpp && masl-dev conan-publish )
( cd core-java && masl-dev conan-publish )
( cd utils && masl-dev conan-publish )
( cd inspector && masl-dev conan-publish )
