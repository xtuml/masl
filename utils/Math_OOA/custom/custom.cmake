#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( Math_interface PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Math_services.cc )

target_link_libraries ( Math_interface m )
