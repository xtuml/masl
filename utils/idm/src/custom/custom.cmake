find_package(xtuml_idm REQUIRED)
target_sources(IDM PRIVATE ${CMAKE_CURRENT_LIST_DIR}/IDM_services.cc)
target_link_libraries(IDM PRIVATE xtuml_idm::xtuml_idm)
