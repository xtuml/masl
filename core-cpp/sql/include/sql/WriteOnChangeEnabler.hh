//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   SqlMonitor.hh
//
//============================================================================//
#ifndef Sql__WriteSqlOnChangeMonitor_HH
#define Sql__WriteSqlOnChangeMonitor_HH

#include <string>

namespace SQL {

// *****************************************************************
//! @brief 
// *****************************************************************
class WriteOnChangeEnabler
{
  public:
    explicit WriteOnChangeEnabler(const std::string& objectName);
            ~WriteOnChangeEnabler();
     bool isEnabled();

  private:
     // prevent copy and assignment
     WriteOnChangeEnabler(const WriteOnChangeEnabler& rhs);  
     WriteOnChangeEnabler& operator=(const WriteOnChangeEnabler& rhs);  

  private:
     const std::string name;
};

} // end namespace SQL
#endif
