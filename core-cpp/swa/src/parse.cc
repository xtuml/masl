/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#include "swa/parse.hh"
#include <errno.h>
#include "swa/ProgramError.hh"
#include "swa/Timestamp.hh"
#include "swa/Duration.hh"

namespace SWA
{
  template<class T>
  T parseSignedInt ( const std::string& source, int base, T* dummy = 0 )
  {
    if ( source.length() == 0 || std::isspace(source[0]) )
    {
      throw ProgramError("Format not recognised - leading spaces");
    }

    errno = 0;
    char* endPos = 0;
    int64_t result = strtoll ( source.c_str(), &endPos, base );
    if ( endPos < source.c_str()+source.length() || errno == EINVAL )
    {
      throw ProgramError("Format not recognised - trailing characters");
    }
    else if ( errno == ERANGE || result < std::numeric_limits<T>::min() || result > std::numeric_limits<T>::max() )
    {
      throw ProgramError("Value out of range");
    }
    return static_cast<T>(result);
  }

  template<class T>
  T parseUnsignedInt ( const std::string& source, int base, T* dummy = 0 )
  {
    if ( source.length() == 0 || std::isspace(source[0]) )
    {
      throw ProgramError("Format not recognised - leading spaces");
    }

    errno = 0;
    char* endPos = 0;
    int64_t result = strtoull ( source.c_str(), &endPos, base );
    if ( endPos < source.c_str()+source.length() || errno == EINVAL )
    {
      throw ProgramError("Format not recognised - trailing characters");
    }
    else if ( errno == ERANGE || result > std::numeric_limits<T>::max() )
    {
      throw ProgramError("Value out of range");
    }
    return static_cast<T>(result);
  }

  double parseDouble ( const std::string& source )
  {
    if ( source.length() == 0 || std::isspace(source[0]) )
    {
      throw ProgramError("Format not recognised - leading spaces");
    }

    errno = 0;
    char* endPos = 0;
    double result = strtod ( source.c_str(), &endPos );
    if ( endPos < source.c_str()+source.length() || errno == EINVAL )
    {
      throw ProgramError("Format not recognised - trailing characters");
    }
    else if ( errno == ERANGE )
    {
      throw ProgramError("Value out of range");
    }
    return result;
  }

  bool parseBool ( const std::string& source )
  {
    if ( source == "false" )
    {
      return false;
    }
    else if ( source == "true" )
    {
      return true;
    }
    else
    {
      throw ProgramError("Format not recognised");
    }
  }

  template<> Timestamp  parse ( const std::string& source, Timestamp*  ) { return Timestamp::parse(source); }
  template<> Duration  parse ( const std::string& source, Duration*  ) { return Duration::parse(source); }

  template<> double  parse ( const std::string& source, double*  ) { return parseDouble(source); }
  template<> bool  parse ( const std::string& source, bool*  ) { return parseBool(source); }

  template<> int8_t  parse ( const std::string& source, int8_t*  ) { return parseSignedInt<int8_t >(source,10); }
  template<> int16_t parse ( const std::string& source, int16_t* ) { return parseSignedInt<int16_t>(source,10); }
  template<> int32_t parse ( const std::string& source, int32_t* ) { return parseSignedInt<int32_t>(source,10); }
  template<> int64_t parse ( const std::string& source, int64_t* ) { return parseSignedInt<int64_t>(source,10); }

  template<> uint8_t  parse ( const std::string& source, uint8_t*  ) { return parseSignedInt<int8_t >(source,10); }
  template<> uint16_t parse ( const std::string& source, uint16_t* ) { return parseSignedInt<int16_t>(source,10); }
  template<> uint32_t parse ( const std::string& source, uint32_t* ) { return parseSignedInt<int32_t>(source,10); }
  template<> uint64_t parse ( const std::string& source, uint64_t* ) { return parseSignedInt<int64_t>(source,10); }

  template<> int8_t  parseBased ( const std::string& source, int base, int8_t*  ) { return parseSignedInt<int8_t >(source,base); }
  template<> int16_t parseBased ( const std::string& source, int base, int16_t* ) { return parseSignedInt<int16_t>(source,base); }
  template<> int32_t parseBased ( const std::string& source, int base, int32_t* ) { return parseSignedInt<int32_t>(source,base); }
  template<> int64_t parseBased ( const std::string& source, int base, int64_t* ) { return parseSignedInt<int64_t>(source,base); }

  template<> uint8_t  parseBased ( const std::string& source, int base, uint8_t*  ) { return parseSignedInt<int8_t >(source,base); }
  template<> uint16_t parseBased ( const std::string& source, int base, uint16_t* ) { return parseSignedInt<int16_t>(source,base); }
  template<> uint32_t parseBased ( const std::string& source, int base, uint32_t* ) { return parseSignedInt<int32_t>(source,base); }
  template<> uint64_t parseBased ( const std::string& source, int base, uint64_t* ) { return parseSignedInt<int64_t>(source,base); }

}
