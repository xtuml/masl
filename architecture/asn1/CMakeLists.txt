cmake_minimum_required(VERSION 3.30)
project(xtuml_asn1 CXX)

find_package(xtuml_swa REQUIRED)
find_package(Boost REQUIRED)

add_library(xtuml_asn1 SHARED
        src/DEREncode.cc
        src/DEREncoder.cc
)

target_link_libraries(xtuml_asn1
        PUBLIC xtuml_swa::xtuml_swa
        Boost::headers
        -lm
)

target_sources(xtuml_asn1
        PUBLIC FILE_SET HEADERS BASE_DIRS include FILES
        include/asn1)
target_compile_options(xtuml_asn1 PRIVATE -Wall -Werror)
install(TARGETS xtuml_asn1 FILE_SET HEADERS)


add_executable(asn1Decoder src/asn1Decoder.cc)
target_link_libraries(asn1Decoder PRIVATE xtuml_asn1)
target_compile_options(asn1Decoder PRIVATE -Wall -Werror)
install(TARGETS asn1Decoder FILE_SET HEADERS)

