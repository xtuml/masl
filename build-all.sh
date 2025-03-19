#!/bin/bash
remote=$1
username=$2
token=$3

shift 3

set -e

( cd architecture/logging && conan create . --update --build=missing --test-missing "$@")
( cd architecture/amqp-client && conan create . --update --build=missing --test-missing "$@") 
( cd architecture/swa && conan create . --update --build=missing --test-missing "$@" )
( cd architecture/asn1 && conan create . --update --build=missing --test-missing "$@")
( cd architecture/sockets && conan create . --update --build=missing --test-missing "$@")
( cd architecture/transient && conan create . --update --build=missing --test-missing "$@")
( cd architecture/sql && conan create . --update --build=missing --test-missing "$@")
( cd architecture/sqlite && conan create . --update --build=missing --test-missing "$@")

( cd plugins/metadata && conan create . --update --build=missing --test-missing "$@")
( cd plugins/inspectorServer && conan create . --update --build=missing --test-missing "$@")
( cd plugins/threadtimer && conan create . --update --build=missing --test-missing "$@")
( cd plugins/codecoverage && conan create . --update --build=missing --test-missing "$@")
( cd plugins/backlogMonitor && conan create . --update --build=missing --test-missing "$@")
( cd plugins/trace && conan create . --update --build=missing --test-missing "$@")
( cd plugins/kafka && conan create . --update --build=missing --test-missing )

(cd masl-conan && conan create . --update --build=missing --test-missing "$@")
(cd masl-codegen && conan create . --update --build=missing --test-missing "$@")

(cd utils/assertions && conan create . --update --build=missing --test-missing "$@")
(cd utils/binary-io && conan create . --update --build=missing --test-missing "$@")
(cd utils/command-line && conan create . --update --build=missing --test-missing "$@")
(cd utils/environment && conan create . --update --build=missing --test-missing "$@")
(cd utils/host && conan create . --update --build=missing --test-missing "$@")
(cd utils/filesystem && conan create . --update --build=missing --test-missing "$@")
(cd utils/format && conan create . --update --build=missing --test-missing "$@")
(cd utils/hash && conan create . --update --build=missing --test-missing "$@")
(cd utils/json && conan create . --update --build=missing --test-missing "$@")
(cd utils/math && conan create . --update --build=missing --test-missing "$@")
(cd utils/strings && conan create . --update --build=missing --test-missing "$@")
(cd utils/uuid && conan create . --update --build=missing --test-missing "$@")
(cd utils/regex && conan create . --update --build=missing --test-missing "$@")
(cd utils/json-validation && conan create . --update --build=missing --test-missing "$@")
(cd utils/logger && conan create . --update --build=missing --test-missing "$@")
(cd utils/schedule && conan create . --update --build=missing --test-missing "$@")
(cd utils/test && conan create . --update --build=missing --test-missing "$@")

(cd examples/petrol/masl &&  conan create . --update --build=missing --test-missing "$@")
(cd examples/calculator/masl &&  conan create . --update --build=missing --test-missing "$@")

(cd inspector && conan create . --update --build=missing --test-missing "$@")

(
    cd all
    installed=$(mktemp)
    conan install .  --lockfile-out conan.lockfile --update --build=missing --format=json --out-file ${installed}
    if [ -n "${remote}" ]
    then
        pkglist=$(mktemp)
        conan list --graph=${installed} --format=json --out-file ${pkglist}
        if [[ -n "${token}" && -n "${username}" ]]
        then
            conan remote login -p $token $remote $username
        fi
        conan upload --list  ${pkglist} -r $remote -c
        rm -f ${pkglist}
    fi
    rm -f ${install}
  
)

