# base image
FROM rockylinux/rockylinux:9.5

RUN dnf update -y && dnf --enablerepo=crb install -y \
    python3.12 \
    python3.12-pip \
    gcc-toolset-14-gcc-c++ \
    gdb \
    java-21-openjdk-devel \
    ninja-build \
    perl \
    jq \
    libnsl2-devel && \
    alternatives --install /usr/bin/pip pip /usr/bin/pip3.12 1 && \
    alternatives --install /usr/bin/python python /usr/bin/python3.12 1 && \
    pip install --upgrade pip conan cmake yq && \
    dnf clean all

ENV PATH=/opt/rh/gcc-toolset-14/root/usr/bin${PATH:+:${PATH}}
ENV LD_LIBRARY_PATH=/opt/rh/gcc-toolset-14/root/usr/lib64:/opt/rh/gcc-toolset-14/root/usr/lib
ENV PKG_CONFIG_PATH=/opt/rh/gcc-toolset-14/root/usr/lib64/pkgconfig
ENV MANPATH=/opt/rh/gcc-toolset-14/root/usr/share/man
ENV INFOPATH=/opt/rh/gcc-toolset-14/root/usr/share/info

COPY conan-config conan-config
RUN conan config install conan-config --type dir && rm -rf conan-config

COPY os-files/ /

WORKDIR /work
