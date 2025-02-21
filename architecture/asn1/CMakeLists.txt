cmake_minimum_required(VERSION 3.15)
project(xtuml_asn1 CXX)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)
find_package(Boost REQUIRED)

xtuml_add_library ( ${CMAKE_PROJECT_NAME} SHARED
            src/DEREncode.cc
            src/DEREncoder.cc
)

target_link_libraries (${CMAKE_PROJECT_NAME} 
        PUBLIC xtuml_swa::xtuml_swa
        Boost::headers
        -lm
)

xtuml_add_executable ( asn1Decoder
        src/asn1Decoder.cc
)

target_link_libraries (asn1Decoder PRIVATE xtuml_asn1 )


