MESSAGE(STATUS "cmake: Using toolchain file: ${CMAKE_TOOLCHAIN_FILE}")

function(xtuml_add_library name )
    add_library(${ARGV})

    common_options(${name})
    add_includes(${name})

    install(TARGETS ${name})

endfunction()

function(xtuml_add_executable name )
    add_executable(${ARGV})

    if ( IS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/include )
        target_include_directories( ${name} PUBLIC include )
    endif()

    common_options(${name})

    install(TARGETS ${name})

endfunction()

function(xtuml_add_local_executable name )
    add_executable(${ARGV})

    common_options(${name})
endfunction()

function(add_includes name)
    if ( IS_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/include )
        target_include_directories( ${name} PUBLIC include )
        install(DIRECTORY include/ DESTINATION ${CMAKE_INSTALL_INCLUDEDIR})
    endif()
endfunction()

function(common_options name)
    target_compile_options( ${name}
        PRIVATE
            $<$<COMPILE_LANGUAGE:CXX>:-Wall>
            $<$<COMPILE_LANGUAGE:CXX>:-Werror>
        PUBLIC
            $<$<COMPILE_LANGUAGE:CXX>:-fmacro-prefix-map=${CMAKE_SOURCE_DIR}=[${CMAKE_PROJECT_NAME}]>
        )

endfunction()