# MASL Process Inspector

## Build with Maven

```
mvn install
```

## Run inspector

```
java -jar $HOME/.m2/repository/org/xtuml/inspector/1.0.0/inspector-1.0.0-jar-with-dependencies.jar
```

### Using Inspector with MASL executables running in a Docker container

Inpsector uses 3 TCP ports to communicate with an executing process. The port
given to the command line is a base port number. Offsets of 20000, 30000, and
40000 are added to the base port number to produce the 3 communication ports.
For example, if you started inpsector with `-p 1234`, ports `21234`, `31234`,
and `41234` would be opened. In order to make these connections, the docker
container must expose these ports. Since the default port for the inspector
client is "0", it is recommended to expose `20000`, `30000`, and `40000` so the
client can be run with default configuration. Be sure to include the command
line arguments `-util Inspector -inspector-port 0` when launching the compiled
executable:

```
docker run -it -p 20000:20000 -p 30000:30000 -p 40000:40000 -v $PWD:/workspace levistarrett/masl-exe <executable_program> -util Inspector -inspector-port 0
```

This launch can be wrapped in a script for convenience.
