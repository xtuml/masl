# MASL Docker Images

## Without docker compose

Run a command in the dev container. This will mount the current working directory into the /work directory in the container, and run the command in that directory.

    docker run --rm --network masl-dev -v conan-cache:/conan-cache -v ${PWD}/work levistarrett/masl-dev:latest <command>

It is recommended that you set a shell alias for the above command.

    alias masl-dev='run --rm --network masl-dev -v conan-cache:/conan-cache -v ${PWD}/work levistarrett/masl-dev:latest'

### With `docker compose`

Run a command in the dev container. This will mount the current working directory into the /work directory in the container, and run the command in that directory.

    docker compose -f docker/docker-compose.yml -v ${PWD}:/work run masl-dev <command>

It is recommended that you set a shell alias for the above command (where <root> is the repository root)

    alias masl-dev='docker compose -f <root>/docker/docker-compose.yml run -v $PWD:/work masl-dev'

### Creating a new Masl domain package

    masl-dev conan new masl-domain -d name="MyDomain"
    masl-dev conan install . --build=missing
    masl-dev conan build . --build=missing

### Creating a new Masl domain & project package

    masl-dev conan new masl-pair -d name="MyDomain"
    masl-dev conan install . --build=missing
    masl-dev conan build . --build=missing

### Convert an existing MASL domain or project to use conan

    masl-dev conan new masl -d name="MyDomain"
    masl-dev conan install . --build=missing
    masl-dev conan build . --build=missing

### Publish a package to the server for later use

    masl-dev conan-publish

Note: The `ARTIFACTORY_USERNAME` and `ARTIFACTORY_TOKEN` environment variables must be set to publish artifacts to the remote server.

### Getting a bash shell in the container to run multiple commands
    masl-dev
    [root@bcb0444272f6 work]# pwd
    /work
