# base image
FROM ubuntu:22.04 AS base-stage

RUN apt update && \
    apt install -y g++-12 python3 python3-pip openjdk-17-jdk ninja-build ca-certificates wget && \
    wget -O - https://apt.kitware.com/keys/kitware-archive-latest.asc 2>/dev/null | gpg --dearmor - | tee /usr/share/keyrings/kitware-archive-keyring.gpg >/dev/null &&\
    echo 'deb [signed-by=/usr/share/keyrings/kitware-archive-keyring.gpg] https://apt.kitware.com/ubuntu/ jammy main' | tee /etc/apt/sources.list.d/kitware.list >/dev/null &&\
    apt update &&\
    apt install -y cmake


RUN update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-12 12
RUN update-alternatives --install /usr/bin/gcc gcc /usr/bin/gcc-12 12
RUN update-alternatives --install /usr/bin/python python /usr/bin/python3.10 3
RUN pip install conan
RUN conan profile detect
RUN sed -i "s/compiler\.cppstd=gnu17/compiler.cppstd=20/g" $(conan profile path default)
RUN echo "[options]\n*:shared=True" >> $(conan profile path default)
#RUN echo tools.cmake.cmaketoolchain:generator=Ninja >> /root/.conan2/global.conf
RUN echo core.cache:storage_path=/conan-cache >> /root/.conan2/global.conf
RUN conan remote add xtuml http://conan-server:9300

COPY os-files/ /
COPY conan-files/ /root/.conan2/

VOLUME /conan-cache

WORKDIR /work

