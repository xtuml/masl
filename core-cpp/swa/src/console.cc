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

#include "swa/console.hh"

namespace
{

  struct null_deleter
  {
      void operator()(void const *) const
      {
      }
  };

}


SWA::Device& SWA::console ()
{
  static Device console(
                  boost::shared_ptr<std::istream>(&::std::cin,null_deleter()), 
                  boost::shared_ptr<std::ostream>(&::std::cout,null_deleter()));    
  return console;
}

SWA::Device& SWA::error_log ()
{
  static Device error_log(boost::shared_ptr<std::ostream>(&::std::cerr,null_deleter()));    
  return error_log;
}

SWA::Device& SWA::system_log ()
{
  static Device system_log(boost::shared_ptr<std::ostream>(&::std::clog,null_deleter()));    
  return system_log;
}
