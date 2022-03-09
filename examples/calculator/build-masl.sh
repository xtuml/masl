#!/bin/bash
WINPTY=`which winpty || echo ""`
MASLMC="$WINPTY docker run -v /$PWD:/workspace levistarrett/masl-compiler -skiptranslator Sqlite"
$MASLMC -domainpath //root/masl/utils -mod ALU_OOA/ALU.mod -test -output src/ALU_OOA && \
$MASLMC -domainpath //root/masl/utils:ALU_OOA -prj calculator_proc/calculator.prj -output src/calculator_proc && \
$WINPTY docker run -it -v /$PWD:/workspace levistarrett/masl-build
