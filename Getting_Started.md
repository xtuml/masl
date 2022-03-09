# Getting Started

## Prerequisites

1. [Docker](https://docs.docker.com/get-docker/)
2. OpenJDK 8 or higher

### Additional Windows requirements

1. [Git for Windows](https://gitforwindows.org/)

## Building the example projects

1. Open a shell (git-bash on Windows) and change directories to the
   `examples/calculator/` directory. 
2. Build project with:
   ```
   ./build-masl.sh
   ```

## Running the example projects

1. Open a shell (git-bash on Windows) and change directories to the
   `examples/calculator/` directory. 
2. To launch the compiled executable within the docker container, execute the
   following:
   ```
   docker run -it -v /$PWD:/workspace levistarrett/masl-exe bin/calculator_transient
   ```
   On Windows:
   ```
   winpty docker run -it -v /$PWD:/workspace levistarrett/masl-exe bin/calculator_transient
   ```

The process will launch, but nothing will happen until there's an external
stimulus and the event queue will just idly wait.  To explore further, launch
with inspector enabled.

### Launching with inspector

1. Open a shell (git-bash on Windows) and change directories to the
   `examples/calculator/` directory. 
2. To launch the compiled executable with inspector enabled, execute the following:
   ```
   docker run -it -p 20000:20000 -p 30000:30000 -p 40000:40000 -v /$PWD:/workspace levistarrett/masl-exe bin/calculator_transient -util Inspector -inspector-port 0
   ```
   On Windows:
   ```
   winpty docker run -it -p 20000:20000 -p 30000:30000 -p 40000:40000 -v /$PWD:/workspace levistarrett/masl-exe bin/calculator_transient -util Inspector -inspector-port 0
   ```

The `-p` docker parameters cause the ports 20000, 30000, and 40000 to be opened
between the host operating system and the process running within container.
Inpsector uses 3 TCP ports for various parts of its communication protocol. The
`-util Inspector` flag tells the generated executable to dynamically load the
inspector libraries and the `-inspector-port 0` tells the executable to listen
on the base ports (20000, 30000, and 40000) with an offset of 0.

_NOTE: The commands to launch MASL executables can be long and clumsy. To streamline things, an alias command can be added to your shell configuration:_

```
alias masl-exe="docker run -it -p 20000:20000 -p 30000:30000 -p 40000:40000 -v /\$PWD:/workspace levistarrett/masl-exe"

# Now, run the executable with:
masl-exe bin/calculator_transient -util Inspector -inspector-port 0
```

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

## MASL langauge reference

The MASL reference manuals can be found here:
- [Current (2016)](https://raw.githubusercontent.com/xtuml/bridgepoint/master/src/org.xtuml.bp.doc/Reference/MASL/LanguageReference/current/maslrefman.pdf)
- [Legacy (2007)](https://raw.githubusercontent.com/xtuml/bridgepoint/master/src/org.xtuml.bp.doc/Reference/MASL/LanguageReference/legacy/maslrefman.pdf)
