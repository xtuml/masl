#!/bin/bash
cp -TRf /workspace /build
cd /build
cmake . -DCMAKE_INSTALL_PREFIX=/build
make install
cp -TRf /build /workspace
