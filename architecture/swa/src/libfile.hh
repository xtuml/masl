#pragma once
#include <string>

#define SWA_XSTR(X) #X
#define SWA_STR(X) SWA_XSTR(X)

#ifdef SHARED_LIB_PREFIX
    #define SWA_SHARED_LIB_PREFIX SWA_STR(SHARED_LIB_PREFIX)
#else
    #ifdef __APPLE__
        #define SWA_SHARED_LIB_PREFIX "lib"
    #elif defined(WIN32) || defined(__WIN32) || defined(__WIN32__)
        #define SWA_SHARED_LIB_PREFIX ""
    #else
        #define SWA_SHARED_LIB_PREFIX "lib"
    #endif
#endif

#ifdef SHARED_LIB_SUFFIX
    #define SWA_SHARED_LIB_SUFFIX SWA_STR(SHARED_LIB_SUFFIX)
#else
    #ifdef __APPLE__
        #define SWA_SHARED_LIB_SUFFIX ".dylib"
    #elif defined(WIN32) || defined(__WIN32) || defined(__WIN32__)
        #define SWA_SHARED_LIB_SUFFIX ".dll"
    #else
        #define SWA_SHARED_LIB_SUFFIX ".so"
    #endif
#endif


namespace SWA {

    inline std::string libfile(const std::string& name) {
        return SWA_SHARED_LIB_PREFIX + name + SWA_SHARED_LIB_SUFFIX;
    }
}


#undef SWA_XSTR
#undef SWA_STR
#undef SWA_SHARED_LIB_PREFIX
#undef SWA_SHARED_LIB_SUFFIX