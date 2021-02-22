#!/bin/bash
LIB_DIR=
if [[ $# > 0 ]]; then
  LIB_DIR=$(dirname $(dirname $1))/lib
fi
stat $LIB_DIR &> /dev/null
if [[ $? == 0 ]]; then
  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$LIB_DIR
fi
$@
