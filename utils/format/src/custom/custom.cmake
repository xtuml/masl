#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Format PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Format_services.cc )

find_package(fmt)

target_link_libraries(Format PUBLIC fmt::fmt )
