#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Environment_interface PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Environment_services.cc ${CMAKE_CURRENT_LIST_DIR}/Environment_JNI.cc )
target_include_directories( Environment_interface PRIVATE /usr/lib/jvm/java-8-openjdk-arm64/include /usr/lib/jvm/java-8-openjdk-arm64/include/linux )
