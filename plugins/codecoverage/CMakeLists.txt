cmake_minimum_required(VERSION 3.15)
project(xtuml_code_coverage CXX)

find_package(pugixml)
find_package(Boost REQUIRED)

find_package(xtuml_cmake_helpers REQUIRED)
find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)

xtuml_add_library(CodeCoverage SHARED
        src/CodeCoverage.cc
)

target_link_libraries(CodeCoverage PUBLIC
        xtuml_metadata::xtuml_metadata
        xtuml_swa::xtuml_swa
        pugixml::pugixml
)

xtuml_add_executable ( CodeCoverageReport
          src/Report.cc
)

target_link_libraries(CodeCoverageReport PRIVATE pugixml::pugixml )
