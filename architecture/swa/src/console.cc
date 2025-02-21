/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
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
                  std::shared_ptr<std::istream>(&::std::cin,null_deleter()), 
                  std::shared_ptr<std::ostream>(&::std::cout,null_deleter()));    
  return console;
}

SWA::Device& SWA::error_log ()
{
  static Device error_log(std::shared_ptr<std::ostream>(&::std::cerr,null_deleter()));    
  return error_log;
}

SWA::Device& SWA::system_log ()
{
  static Device system_log(std::shared_ptr<std::ostream>(&::std::clog,null_deleter()));    
  return system_log;
}
