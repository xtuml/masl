FROM chainguard/glibc-dynamic:latest-dev AS dev
COPY /staging /

# Inspector ports
EXPOSE 20000
EXPOSE 30000
EXPOSE 40000

WORKDIR /apps/calculator
ENV LD_LIBRARY_PATH=/apps/calculator/lib
ENTRYPOINT ["bin/calculator_transient"]