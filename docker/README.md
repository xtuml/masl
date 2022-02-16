# MASL Docker Images

## Build

```
docker build -t levistarrett/masl-base     -f docker/Dockerfile.masl-base     .
docker build -t levistarrett/masl-compiler -f docker/Dockerfile.masl-compiler .
docker build -t levistarrett/masl-build    -f docker/Dockerfile.masl-build    .
docker build -t levistarrett/masl-exe      -f docker/Dockerfile.masl-exe      .
```
