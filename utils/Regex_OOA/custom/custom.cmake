#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
find_package ( Boost COMPONENTS regex )

target_sources( Regex_interface PRIVATE ${CMAKE_CURRENT_LIST_DIR}/Regex_services.cc )

target_link_libraries ( Regex_interface Boost::regex )
