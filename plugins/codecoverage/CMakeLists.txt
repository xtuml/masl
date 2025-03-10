cmake_minimum_required(VERSION 3.30)
project(xtuml_code_coverage CXX)

find_package(pugixml)
find_package(Boost REQUIRED)

find_package(xtuml_swa REQUIRED)
find_package(xtuml_metadata REQUIRED)

add_library(CodeCoverage SHARED
        src/CodeCoverage.cc
)

target_link_libraries(CodeCoverage PUBLIC
        xtuml_metadata::xtuml_metadata
        xtuml_swa::xtuml_swa
        pugixml::pugixml
)
target_compile_options(CodeCoverage PRIVATE -Wall -Werror)
install(TARGETS CodeCoverage)

add_executable ( CodeCoverageReport
          src/Report.cc
)

target_link_libraries(CodeCoverageReport PRIVATE pugixml::pugixml )
target_compile_options(CodeCoverageReport PRIVATE -Wall -Werror)
install(TARGETS CodeCoverageReport)
