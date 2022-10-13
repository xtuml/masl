#!/bin/bash
WINPTY=`which winpty || echo ""`
MASLMC="$WINPTY docker run --rm -v /$PWD:/root levistarrett/masl-compiler -skiptranslator Sqlite"
$MASLMC -domainpath //opt/masl/lib/masl -mod PSC_OOA/PSC.mod -test -output src/PSC_OOA && \
$MASLMC -domainpath //opt/masl/lib/masl:PSC_OOA -prj PETROL_PROC/PETROL_PROC.prj -output src/PETROL_PROC && \
$WINPTY docker run --rm -it -v /$PWD:/root levistarrett/masl-build
