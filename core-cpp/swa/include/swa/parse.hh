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
