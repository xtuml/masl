# base image
FROM rockylinux:9 AS base-stage

# Install stuff
RUN dnf update -y && dnf clean all && \
    dnf --enablerepo=crb install -y python3.11 python3.11-pip gcc-toolset-12-gcc-c++ java-17-openjdk ninja-build perl jq && \
    alternatives --install /usr/bin/python python /usr/bin/python3.11 3 &&\
    alternatives --install /usr/bin/pip pip /usr/bin/pip3.11 3 &&\
    pip install --upgrade pip conan cmake yq && \
    dnf clean all

# Configure conan
RUN source /opt/rh/gcc-toolset-12/enable && conan profile detect && \
    sed -i "s/compiler\.cppstd=gnu17/compiler.cppstd=20/g" $(conan profile path default) && \
    printf "os.glibc=$(python -c 'import platform; print(platform.libc_ver()[1])')\n[options]\n*:shared=True\n" >> $(conan profile path default) && \
    echo tools.cmake.cmaketoolchain:generator=Ninja >> /root/.conan2/global.conf && \
    echo core.cache:storage_path=/conan-cache >> /root/.conan2/global.conf && \
    yq -Y ".os.Linux += {\"glibc\": [ $(seq -f '"2.%.0f"' -s, 5 40) ] }" /root/.conan2/settings.yml > /root/.conan2/settings.yml.tmp && \
    mv /root/.conan2/settings.yml.tmp /root/.conan2/settings.yml && \
    conan remote add xtuml http://conan-server:9300

COPY os-files/ /

WORKDIR /work

ENTRYPOINT ["scl", "run", "gcc-toolset-12" ]
CMD ["/bin/bash"]

