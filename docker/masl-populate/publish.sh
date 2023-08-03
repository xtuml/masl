#!/bin/bash
conan remote login -p docker xtuml docker &&\
conan upload --list=pkglist_helper.json -r=xtuml -c &&\
conan upload --list=pkglist_core-cpp.json -r=xtuml -c &&\
conan upload --list=pkglist_core-java.json -r=xtuml -c &&\
conan upload --list=pkglist_utils.json -r=xtuml -c &&\
conan upload --list=pkglist_inspector.json -r=xtuml -c
