find_package(SimpleAdd REQUIRED)

add_compile_options(-fdiagnostics-color=always)

simple_add_executable(
        NAME doc-example
        SOURCES
                example.cc
        LINKS
                amqp_asio::amqp_asio
)