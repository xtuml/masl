FROM levistarrett/masl-dev:latest

RUN useradd -m -u 501 -s /bin/bash builder
USER builder
RUN cp /etc/skel/.bashrc /home/builder

RUN conan config install /etc/conan-config --type dir
USER root
RUN  rm -rf conan-config
USER builder

WORKDIR /work
VOLUME /home/builder/.conan2/p
