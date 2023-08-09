FROM python:3.11
RUN pip install conan-server
EXPOSE 9300
COPY server.conf /conan-data/server.conf
VOLUME /conan-data
STOPSIGNAL SIGINT
HEALTHCHECK CMD curl --fail http://localhost:9300/v2/ping || exit 1
ENTRYPOINT ["conan_server", "-d", "/conan-data"]