
add_executable(xtuml_amqp_doc_example
        src/example.cc
)

target_link_libraries(xtuml_amqp_doc_example
        PRIVATE
        xtuml_amqp_client
)