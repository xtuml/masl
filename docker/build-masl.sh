#!/bin/bash
cp -TRpf /root /build
cd /build
cmake . -DCMAKE_INSTALL_PREFIX=/build
make install
cp -TRpf /build /root
