# MASL Docker Images

## Build

```
docker buildx build --push --platform linux/arm64/v8,linux/amd64 -f docker/Dockerfile.masl-base --tag levistarrett/masl-base:latest .
docker buildx build --push --platform linux/arm64/v8,linux/amd64 -f docker/Dockerfile.masl-base --tag levistarrett/masl-compiler:latest .
docker buildx build --push --platform linux/arm64/v8,linux/amd64 -f docker/Dockerfile.masl-base --tag levistarrett/masl-exe:latest .
```
