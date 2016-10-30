//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
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
