//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_Domain_HH
#define SWA_Domain_HH

#include "boost/function.hpp"

#include <stdint.h>
#include <string>
#include <map>

#include "types.hh"

namespace SWA
{

  class Domain
  {
    public:
      Domain () : id(0), name(""), interface(true) {}
      Domain ( int id, const std::string& name, bool interface ) : id(id), name(name), interface(interface) { }

      int getId() const { return id; }

      const std::string& getName() const { return name; }

      bool isInterface() const { return interface; }
      void setInterface( bool interface ) { this->interface = false; }


      typedef boost::function<void()> Scenario;
      typedef boost::function<void()> External;

      void addExternal ( int id, const Scenario& external );
      void addScenario ( int id, const External& scenario );

      const boost::function<void()>& getScenario(int id) const;
      const boost::function<void()>& getExternal(int id) const;
  
    private:
      typedef std::map<int,Scenario>            ScenarioLookup;
      typedef std::map<int,External>            ExternalLookup;

      int id;
      std::string name;
      bool interface;

      ScenarioLookup            scenarios;
      ExternalLookup            externals;

  };
}

#endif
