cmake_minimum_required(VERSION 3.5)

project(petrol VERSION 1.0)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} $ENV{HOME}/masl/core-cpp/cmake)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} $ENV{HOME}/masl/install/lib/cmake/MaslCore)
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} $ENV{HOME}/masl/install/lib/cmake/UtilityDomains)

set(INSTALL_LIB_DIR lib CACHE PATH "Installation directory for libraries")
set(INSTALL_BIN_DIR bin CACHE PATH "Installation directory for executables")
set(INSTALL_INCLUDE_DIR include CACHE PATH "Installation directory for header files")

include(MaslCoreConfig)
include(UtilityDomainsConfig)
include(SimpleAdd)

find_package(Poco REQUIRED COMPONENTS XML Foundation)
find_package(Sqlite3 REQUIRED)
find_package(Boost REQUIRED QUIET COMPONENTS system)
link_libraries(Boost::boost)

add_subdirectory(src/PSC_OOA)
add_subdirectory(src/PETROL_PROC)
