# Getting Started

## Prerequisites

1. [Docker](https://docs.docker.com/get-docker/)
2. OpenJDK 8 or higher

### `masl-dev` docker image

MASL builds use a publicly available docker image for building. Use the
following command to run this image:

  ```
  docker compose -f docker/docker-compose.yml run -v ${PWD}:/work -p 20000:20000 -p 30000:30000 -p 40000:40000 masl-dev <command>
  ```

It is recommended to alias this command in your shell to make usage easier:

  ```
  alias masl-dev='docker compose -f <root_of_repository>/docker/docker-compose.yml run -v ${PWD}:/work -p 20000:20000 -p 30000:30000 -p 40000:40000 masl-dev'
  ```

NOTE: Be sure to replace `<root_of_repository>` with the absolute path to the
location of your cloned `masl` repository to ensure the alias works from any
working directory.

NOTE: The rest of this document will assume this alias has been set up.

### Additional Windows requirements

1. [Git for Windows](https://gitforwindows.org/)

## Building the calculator example project

1. Open a shell (git-bash on Windows) and change directories to the
   `examples/calculator/masl` directory. 
2. Build project with:
   ```
   masl-dev conan build . --options test=True
   ```

## Running the calculator example project

1. Open a shell (git-bash on Windows) and change directories to the
   `examples/calculator/masl` directory.
2. To launch the compiled executable, execute the following command to create a
   new shell inside a `masl-dev` container:
   ```
   masl-dev
   ```

3. Run the following commands inside the container:
   ```
   source build/<target_architecture_directory>/Release/generators/conanrun.sh
   ./build/<target_architecture_directory>/Release/bin/calculator_transient -postinit schedule/test.sch
   ```

The process will launch and execute a series of predefined test scenarios
before exiting.

### Launching with inspector

1. Open a shell (git-bash on Windows) and change directories to the
   `examples/calculator/` directory. 

2. To launch the compiled executable with inspector enabled, execute the
   following command to create a new shell inside a `masl-dev` container:
   ```
   masl-dev
   ```

3. Run the following commands inside the container:
   ```
   source build/<target_architecture_directory>/Release/generators/conanrun.sh 
   ./build/<target_architecture_directory>/Release/bin/calculator_transient -util Inspector -inspector-port 0
   ```

The `-util Inspector` flag tells the generated executable to dynamically load
the inspector libraries and the `-inspector-port 0` tells the executable to
listen on the base ports (20000, 30000, and 40000) with an offset of 0.

### Running inspector

1. Download the inspector Java application: [Inspector JAR](https://1f-outgoing.s3.amazonaws.com/inspector/inspector-1.0.0-jar-with-dependencies.jar)
2. With the MASL executable application already running, launch inspector by
   double clicking on the JAR file or running fom a command line with:
   ```
   java -jar inspector-1.0.0-jar-with-dependencies.jar
   ```

The inspector will attach to your running process and you can start using it to
interrogate the instance population, execute scenarios, single step through
actions and more.

In the calculator model, try executing some of the "testcase" scenarios from
the "scenarios" tab. Observe the output that is printed in the same shell where
you launched inspector.

## Using the built in MASL templates

The `masl-dev` container has the capability to create new MASL projects from templates

### Creating a new MASL standalone domain

  ```
  masl-dev conan new masl-domain -d name="MyDomain"
  masl-dev conan build .
  ```

### Creating a new MASL domain & project pair

  ```
  masl-dev conan new masl-pair -d name="MyApp"
  masl-dev conan build .
  ```

### Publish a package to the server for later use

  ```
  masl-dev conan-publish
  ```

NOTE: The `ARTIFACTORY_USERNAME` and `ARTIFACTORY_TOKEN` environment variables
must be set to publish artifacts to the remote server.

## Building the compiler

### Rebuilding the `masl-dev` image

If build environment dependencies change, or changes are made to the Conan
templates, it is necessary to rebuild the `masl-dev` image itself. Use the
following command:

  ```
  docker compose -f docker/docker-compose.yml build masl-dev
  ```

### Building the compiler and software architecture

To rebuild the code generator and software architecture,
launch a shell within a `masl-dev` container with the following command:

  ```
  masl-dev
  ```

Then simply run the following command from the root of the repository within
the container:

  ```
  ./build-all.sh
  ```

This will rebuild and publish all the projects in the repository. During the
build you will see an error message that looks like this:

  ```
  ERROR: Exiting with code: 2
  Could not log in to remote server. Check that ARTIFACTORY_USERNAME and ARTIFACTORY_TOKEN are set.
  ```

This message is normal and simply indicates that the build is not authenticated
to be published to the public artifact server. The artifacts are still
published to the local cache.

To build a single component of the repository independently, navigate to the
corresponding source folder and run the following command:

  ```
  cd core-cpp/
  masl-dev conan-publish --version=$(git describe --tags)
  ```

When iteratively testing changes to the code generator or software
architecture, be sure your `conanfile.py` references the latest versions of the
dependencies or you will not see the effects of your changes.

## MASL langauge reference

The MASL reference manuals can be found here:
- [Current (2016)](https://raw.githubusercontent.com/xtuml/bridgepoint/master/src/org.xtuml.bp.doc/Reference/MASL/LanguageReference/current/maslrefman.pdf)
- [Legacy (2007)](https://raw.githubusercontent.com/xtuml/bridgepoint/master/src/org.xtuml.bp.doc/Reference/MASL/LanguageReference/legacy/maslrefman.pdf)
