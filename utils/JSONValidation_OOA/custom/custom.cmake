find_package ( Boost COMPONENTS regex )
target_link_libraries ( JSONValidation PUBLIC Boost::regex )
target_link_libraries ( JSONValidation PUBLIC Boost::regex )
install (
  FILES 
    ${CMAKE_CURRENT_LIST_DIR}/json-metaschema.json
  DESTINATION share/json )
