#!/bin/bash
cp -TRpf /workspace /build
cd /build
cmake . -DCMAKE_INSTALL_PREFIX=/build
make install
cp -TRpf /build /workspace
