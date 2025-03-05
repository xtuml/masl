rm -rf staging
docker run --rm  -v conan-cache:/conan-cache -v $PWD:/work levistarrett/masl-dev:latest conan install . --deployer-folder=staging --deployer-package=*
docker build . --tag masl-example-calculator:latest
docker run masl-example-calculator:latest -postinit schedule/test.sch