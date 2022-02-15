#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
target_sources( UUID_interface PRIVATE ${CMAKE_CURRENT_LIST_DIR}/UUID_services.cc )

target_link_libraries ( UUID_interface uuid )
