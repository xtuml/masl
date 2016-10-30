//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <vector>
#include <string>
#include <iostream>
#include <stdexcept>
#include <algorithm>

#include "swa/Process.hh"
#include "swa/Exception.hh"
#include "swa/Schedule.hh"
#include "swa/ProgramError.hh"
#include "swa/CommandLine.hh"

#include <boost/tokenizer.hpp>
#include <boost/bind.hpp>
#include <boost/bind.hpp>

#include <dlfcn.h>

namespace SWA
{
  namespace
  {
    const char* const UtilOption = "-util";
    const char* const LoopDisableOption = "-mainloop-disable";
  }

  int main(int argc, const char* const * argv )
  {
    Process::getInstance().getCommandLine().registerOption ( NamedOption(UtilOption,"Utility to include",false,"utility",true,true) );
    Process::getInstance().getCommandLine().registerOption ( NamedOption(LoopDisableOption,"terminate after schedules",false,"",false) );

    try
    {
      try
      {
        Process::getInstance().getCommandLine().parse(argc,argv,true);
      }
      catch ( ProgramError& e )
      {
        std::cerr << Process::getInstance().getCommandLine().getUsage() << std::endl;
        return 1;
      }
      Process::getInstance().runStartup();


      std::cout << "Starting Process " << Process::getInstance().getName() << std::endl;
      Process::getInstance().initialise();

      if ( Process::getInstance().getCommandLine().optionPresent(UtilOption) )
      {
        const std::vector<std::string>& libs = Process::getInstance().getCommandLine().getMultiOption(UtilOption);
        for ( std::vector<std::string>::const_iterator it = libs.begin(), end = libs.end(); it != end; ++it )
        {
          boost::tokenizer<> tok(*it);
          for ( boost::tokenizer<>::iterator it = tok.begin(), end = tok.end(); it != end; ++it )
          {
            std::string libname = "lib" + *it + ".so";
            if ( dlopen(libname.c_str(),RTLD_NOW|RTLD_GLOBAL) ) 
            {
              std::cout << "Included utility " << *it << std::endl;
            }
            else
            {
              std::cout << "Utility " << *it << " not found. " << std::endl;
              std::cout << dlerror() << std::endl;
            }

          }
        }
      }

      Process::getInstance().endInitialisation();


      // Reparse the command line now that any utilities have 
      // been loaded and startup scenarios have been run (and 
      // possibly added their own command line arguments) 
      try
      {
        Process::getInstance().getCommandLine().parse(argc,argv,false);
      }
      catch ( ProgramError& e )
      {
        std::cerr << Process::getInstance().getCommandLine().getUsage() << std::endl;
        return 1;
      }

      Process::getInstance().endStartup();

      Process::getInstance().runSchedules();

      if ( !Process::getInstance().getCommandLine().optionPresent(LoopDisableOption) )
      {
        Process::getInstance().mainLoop();
      }
    }
    catch (const Exception& e )
    {
      std::cerr << e.what() << std::endl;
      return 1;
    }
    return 0;
  }
}
