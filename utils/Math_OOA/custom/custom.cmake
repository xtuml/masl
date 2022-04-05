#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Math_interface PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Math_services.cc ${CMAKE_CURRENT_LIST_DIR}/Math_JNI.cc )
target_include_directories( Math_interface PRIVATE $ENV{JAVA_HOME}/include $ENV{JAVA_HOME}/include/linux )

target_link_libraries ( Math_interface m )
