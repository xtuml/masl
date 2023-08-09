FROM levistarrett/masl-dev as base

COPY ../conan-helper conan-helper
COPY ../core-cpp core-cpp
COPY ../core-java core-java
COPY ../inspector inspector
COPY ../utils utils
RUN conan remote remove xtuml && \
  conan create conan-helper --format=json --build=missing "$@" > create_helper.json && \
  conan create core-cpp  --format=json --build=missing "$@" > create_core-cpp.json && \
  conan create core-java  --format=json --build=missing "$@" > create_core-java.json && \
  conan create utils  --format=json --build=missing "$@" > create_utils.json && \
  conan create inspector  --format=json --build=missing "$@" > create_inspector.json && \
  conan cache clean "*" && \
  conan list --graph=create_helper.json --graph-binaries=build --format=json > pkglist_helper.json && \
  conan list --graph=create_core-cpp.json --graph-binaries=build --format=json > pkglist_core-cpp.json && \
  conan list --graph=create_core-java.json --graph-binaries=build --format=json > pkglist_core-java.json && \
  conan list --graph=create_utils.json --graph-binaries=build --format=json > pkglist_utils.json && \
  conan list --graph=create_inspector.json --graph-binaries=build --format=json > pkglist_inspector.json

FROM levistarrett/masl-dev as publish
COPY --from=base /conan-cache /conan-cache
COPY --from=base /work/pkglist_*.json .
COPY docker/masl-populate/publish.sh .
ENTRYPOINT ["./publish.sh"]
