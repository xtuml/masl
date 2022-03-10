#!/bin/bash
WINPTY=`which winpty || echo ""`
MASLMC="$WINPTY docker run --rm -v /$PWD:/workspace levistarrett/masl-compiler -skiptranslator Sqlite"
$MASLMC -domainpath //root/masl/utils -mod PSC_OOA/PSC.mod -test -output src/PSC_OOA && \
$MASLMC -domainpath //root/masl/utils:PSC_OOA -prj PETROL_PROC/PETROL_PROC.prj -output src/PETROL_PROC && \
$WINPTY docker run --rm -it -v /$PWD:/workspace levistarrett/masl-build
