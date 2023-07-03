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

#include "Logging.hh"
#include <map>
#include <fstream>

int main ( int argc, char** argv )
{
  std::ifstream file(argv[1]);
  if ( file )
  {
    std::map<std::string,std::string> params;
    params["a"] = "AAA";
    params["bb"] = "BBB";
    Logging::Logger::getInstance().loadXMLConfiguration(file, argv[0], "MyName", params );

    Logging::fatal       ( "test fatal" );
    Logging::critical    ( "test critical" );
    Logging::error       ( "test error" );
    Logging::warning     ( "test warning" );
    Logging::notice      ( "test notice" );
    Logging::information ( "test information" );
    Logging::debug       ( "test debug" );
    Logging::trace       ( "test trace" );

  }

  sleep(1);

}
