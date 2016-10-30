//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_Schedule_HH
#define SWA_Schedule_HH

#include <string>
#include <vector>
#include <boost/function.hpp>

namespace SWA
{
  class Schedule
  {
    public:
      Schedule ( const std::string& name, const std::string& text );
      
      bool isValid() const { return valid; }

      const std::string& getName() const { return name; }

      typedef boost::function<void()> Action;
 
      typedef std::vector<Action> Actions;
      const Actions& getActions() const { return actions; }

    private:
      void reportError( int lineNo, const std::string& error );
      std::string name;
      std::string text;
      bool valid;

      Actions actions;
  };
}

#endif
