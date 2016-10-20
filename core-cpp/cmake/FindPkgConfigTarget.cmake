#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
find_package(PkgConfig QUIET)
find_package(PackageHandleStandardArgs QUIET)

function(pkg_config_target TARGET MODULE )

  if ( NOT TARGET ${TARGET} )
    list( LENGTH ARGN EXTRA_ARGS )
    if ( EXTRA_ARGS GREATER 0 )
      list(GET ARGN 0 PACKAGE )
      set(FOUND_VAR ${PACKAGE}_FOUND)
      if ( EXTRA_ARGS GREATER 1 )
        list(GET ARGN 1 COMPONENT )
        set(FOUND_VAR ${PACKAGE}_${COMPONENT}_FOUND)
      endif()
    else()
      set(PACKAGE ${MODULE})
    endif()
  
    
    if ( NOT DEFINED ${MODULE}_FIND_REQUIRED )
      if ( DEFINED COMPONENT )
        set(${MODULE}_FIND_REQUIRED   ${${PACKAGE}_FIND_REQUIRED_${COMPONENT}})
      else()
        set(${MODULE}_FIND_REQUIRED   ${${PACKAGE}_FIND_REQUIRED})
      endif()
    endif()

    if ( NOT DEFINED ${MODULE}_FIND_QUIETLY )
      set(${MODULE}_FIND_QUIETLY   ${${PACKAGE}_FIND_QUIETLY})
    endif()

    if ( NOT DEFINED ${MODULE}_FIND_VERSION )
      set(${MODULE}_FIND_VERSION   ${${PACKAGE}_FIND_VERSION})
    endif()

    if(${MODULE}_FIND_QUIETLY)
      set(QUIET "QUIET")
    endif()

    if(${MODULE}_FIND_REQUIRED)
      set(REQUIRED "REQUIRED")
    endif()

    pkg_check_modules(PC_${MODULE} ${QUIET} ${REQUIRED} ${MODULE})

    if ( PC_${MODULE}_FOUND )
      find_package_handle_standard_args(
          ${MODULE}
            REQUIRED_VARS PC_${MODULE}_LIBRARIES 
            VERSION_VAR   PC_${MODULE}_VERSION )

      if ( ${MODULE}_FOUND )
        if ( DEFINED COMPONENT )
            set( ${PACKAGE}_${COMPONENT}_FOUND                 ${${MODULE}_FOUND}      PARENT_SCOPE)
        endif()
        if ( NOT DEFINED ${PACKAGE}_FOUND )
          set(${PACKAGE}_FOUND   ${${MODULE}_FOUND}      PARENT_SCOPE)
        endif()

        set(${PACKAGE}_VERSION ${PC_${MODULE}_VERSION} PARENT_SCOPE)

        add_library(${TARGET} INTERFACE IMPORTED)

        set_property ( TARGET ${TARGET} PROPERTY INTERFACE_INCLUDE_DIRECTORIES        ${PC_${MODULE}_INCLUDE_DIRS} )
        set_property ( TARGET ${TARGET} PROPERTY INTERFACE_SYSTEM_INCLUDE_DIRECTORIES ${PC_${MODULE}_INCLUDE_DIRS} )
        set_property ( TARGET ${TARGET} PROPERTY INTERFACE_COMPILE_OPTIONS            ${PC_${MODULE}_CFLAGS_OTHER} )

        foreach(lib ${PC_${MODULE}_LIBRARIES})
          find_library(${TARGET}_${lib}_LIBRARY ${lib} PATHS ${PC_${MODULE}_LIBRARY_DIRS} )
          mark_as_advanced(FORCE ${TARGET}_${lib}_LIBRARY)
          set_property ( TARGET ${TARGET} APPEND PROPERTY INTERFACE_LINK_LIBRARIES             ${${TARGET}_${lib}_LIBRARY} )
        endforeach()
      else()
        if ( ${PACKAGE}_FIND_REQUIRED_${COMPONENT} OR NOT DEFINED COMPONENT )
          set(${PACKAGE}_FOUND   FALSE      PARENT_SCOPE)
        endif()
      endif()
    else()
      if ( ${PACKAGE}_FIND_REQUIRED_${COMPONENT} OR NOT DEFINED COMPONENT )
        set(${PACKAGE}_FOUND   FALSE      PARENT_SCOPE)
      endif()
    endif()

  endif()

endfunction()
