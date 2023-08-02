FROM python:latest
RUN pip install conan-server
EXPOSE 9300
VOLUME /conan-data
COPY server.conf /conan-data/server.conf

HEALTHCHECK CMD [ "curl", "-f ", "http://localhost:9300/v1/ping"]

ENTRYPOINT ["conan_server", "-d", "/conan-data"]