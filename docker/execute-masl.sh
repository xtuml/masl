#!/bin/bash
# Add the executable directory and its sibling "lib" directory to the path
if [[ $# > 0 ]]; then
  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$(dirname $1):$(dirname $(dirname $1))/lib
fi
$@
