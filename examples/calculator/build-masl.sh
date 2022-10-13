#!/bin/bash
WINPTY=`which winpty || echo ""`
MASLMC="$WINPTY docker run --rm -v /$PWD:/root levistarrett/masl-compiler -skiptranslator Sqlite"
$MASLMC -domainpath //opt/masl/lib/masl -mod ALU_OOA/ALU.mod -test -output src/ALU_OOA && \
$MASLMC -domainpath //opt/masl/lib/masl:ALU_OOA -prj calculator_proc/calculator.prj -output src/calculator_proc && \
$WINPTY docker run --rm -it -v /$PWD:/root levistarrett/masl-build
