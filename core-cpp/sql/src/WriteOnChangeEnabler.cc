//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <iostream>
#include <sstream>
#include <iterator>
#include <algorithm>

#include "sql/WriteOnChangeEnabler.hh"

#include "swa/Process.hh"
#include "swa/CommandLine.hh"

namespace {

// *****************************************************************
// *****************************************************************
const char * const wocCommandLineArg   = "-woc";
const char * const wocCommandLineUsage = "do not cache modifications but write SQL directly on change";
const char * const wocCommandLineDesc  = "['ALL':Name:...]";

// *****************************************************************
// *****************************************************************
bool configureCommandLine()
{
  SWA::Process::getInstance().getCommandLine().registerOption (SWA::NamedOption(wocCommandLineArg,wocCommandLineUsage,false,wocCommandLineDesc,false));
  return true;
}

// *****************************************************************
// *****************************************************************
bool isWocMonitorEnabled(const std::string& objectName)
{
   static bool enabled = SWA::Process::getInstance().getCommandLine().optionPresent(wocCommandLineArg); 

   bool enableForName = false;
   if (enabled == true){
       const std::string wocOptions = SWA::Process::getInstance().getCommandLine().getOption(wocCommandLineArg); 
       std::string currentOption;
       std::istringstream lexerStream(wocOptions);
       while(std::getline(lexerStream,currentOption,':')){
            if (currentOption == objectName || currentOption == "ALL"){
                enableForName = true;
                break;
             }
       }
   }
   return enableForName; 
}

// *****************************************************************
// *****************************************************************
bool initCommandLine = configureCommandLine();

}

namespace SQL {

// *****************************************************************
// *****************************************************************
WriteOnChangeEnabler::WriteOnChangeEnabler(const std::string& objectName):
     name(objectName)
{

}

// *****************************************************************
// *****************************************************************
WriteOnChangeEnabler::~WriteOnChangeEnabler()
{

}

// *****************************************************************
// *****************************************************************
bool WriteOnChangeEnabler::isEnabled()
{
  return isWocMonitorEnabled(name);
}

} // end namespace SQL
