FROM chainguard/glibc-dynamic:latest-dev AS dev
COPY /staging /

# Inspector ports
EXPOSE 20000
EXPOSE 30000
EXPOSE 40000

WORKDIR /apps/petrol
ENV LD_LIBRARY_PATH=/apps/petrol/lib
ENTRYPOINT ["bin/PETROL_PROC_transient"]