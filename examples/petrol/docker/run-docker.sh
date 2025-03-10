rm -rf staging
docker run --rm  -v conan-cache:/conan-cache -v $PWD:/work levistarrett/masl-dev:latest conan install . --deployer-folder=staging --deployer-package=*
docker build . --tag masl-example-petrol:latest
docker run masl-example-petrol:latest -postinit schedule/run.sch