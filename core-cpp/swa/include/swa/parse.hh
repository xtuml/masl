//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_parse_HH
#define SWA_parse_HH

#include <string>
#include <stdint.h>

namespace SWA
{
  class Timestamp;
  class Duration;

  template<class T> T parse ( const std::string& source, T* dummy = 0 );
  template<class T> T parseBased ( const std::string& source, int base, T* dummy = 0 );

  template<> Timestamp  parse ( const std::string& source, Timestamp* );
  template<> Duration  parse ( const std::string& source, Duration* );

  template<> double  parse ( const std::string& source, double* );
  template<> bool  parse ( const std::string& source, bool* );

  template<> int8_t  parse ( const std::string& source, int8_t* );
  template<> int16_t parse ( const std::string& source, int16_t* );
  template<> int32_t parse ( const std::string& source, int32_t* );
  template<> int64_t parse ( const std::string& source, int64_t* );

  template<> uint8_t  parse ( const std::string& source, uint8_t*  );
  template<> uint16_t parse ( const std::string& source, uint16_t* );
  template<> uint32_t parse ( const std::string& source, uint32_t* );
  template<> uint64_t parse ( const std::string& source, uint64_t* );

  template<> int8_t  parseBased ( const std::string& source, int base, int8_t*  );
  template<> int16_t parseBased ( const std::string& source, int base, int16_t* );
  template<> int32_t parseBased ( const std::string& source, int base, int32_t* );
  template<> int64_t parseBased ( const std::string& source, int base, int64_t* );

  template<> uint8_t  parseBased ( const std::string& source, int base, uint8_t*  );
  template<> uint16_t parseBased ( const std::string& source, int base, uint16_t* );
  template<> uint32_t parseBased ( const std::string& source, int base, uint32_t* );
  template<> uint64_t parseBased ( const std::string& source, int base, uint64_t* );

}
#endif
