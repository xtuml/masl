# masl

MASL is a Shlaer-Mellor dialect action language and structural modeling
language.

If you are here for the first time, and want to know how to access and build
BridgePoint.  Read the [Developer's Getting Started
Guide](https://github.com/xtuml/bridgepoint/blob/master/doc-bridgepoint/process/Developer%20Getting%20Started%20Guide.md).
If you want to learn more about BridgePoint and xtUML, check out [xtUML
Learn](https://xtuml.org/learn/).  More Questions? Check the
[FAQ](https://github.com/xtuml/bridgepoint/blob/master/doc-bridgepoint/process/FAQ.md)
and if you don't find your answer, post your question to the [xtUML
Forums](https://xtuml.org/community/forum/xtuml-forum/) for help.

## Licensing

This project is Open Source Software (OSS) licensed under Apache 2 and Eclipse
(EPL). Documentation is Creative Commons.

## Governance

These projects are governed according to Eclipse Foundation policies and the
engineering development process of the people who support BridgePoint.
Everything is done in the open. The software is freely available and flexibly
modified. No company owns the software.

## What to Expect

Open issues (at https://support.onefact.net) to identify problems you discover.
Open issues to request new functionality.  The developers attempt to
acknowledge issues within 2-3 days.  Many ssues are addressed by volunteers.
Big issues are addressed by engineers paid to fix the problems by the people
raising the issues.  Providing clear reproduction steps with an attached
example model increases the likelihood that an issue will be explored more
deeply.  This is how it works.  

## Getting started

See the [Getting Started Guide](Getting_Started.md) to jump into building and
running masl models.

## Building the compiler

#### Prerequisites (from clean Ubuntu 16.04 install, should work fine with Oracle JDK8 too)
```
sudo apt-get update
sudo apt-get install gcc g++ pkg-config cmake ninja-build libsqlite3-dev libboost-all-dev libssl-dev uuid-dev gradle openjdk-8-jdk wget
```

#### Unfortunately apt-get install libpoco-dev doesn't install cmake bindings, so build from source
```
wget http://pocoproject.org/releases/poco-1.7.3/poco-1.7.3-all.tar.gz
tar xzf poco-1.7.3-all.tar.gz
cd poco-1.7.3-all
cmake . -G Ninja
sudo ninja install
```

#### Build MASL
```
cmake . -G Ninja -DCMAKE_INSTALL_PREFIX=${PWD}/install
ninja install
```
