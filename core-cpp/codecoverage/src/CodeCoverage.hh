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

#ifndef CodeCoverage_HH
#define CodeCoverage_HH

#include "swa/Duration.hh"
#include "swa/Timestamp.hh"
#include "swa/Stack.hh"
#include "swa/StackFrame.hh"
#include "swa/ProcessMonitor.hh"
#include "metadata/MetaData.hh"

#include <stack>
#include "boost/unordered_map.hpp"
#include "boost/functional/hash.hpp"
#include <Poco/DOM/AutoPtr.h>
#include <Poco/DOM/Element.h>


namespace CodeCoverage
{

  class CodeCoverage : public SWA::ProcessMonitor::MonitorConnection
  {
    public:
      class StackFrame
      {
        public:
          StackFrame ( const SWA::StackFrame& frame );
          StackFrame ( SWA::StackFrame::ActionType type, int domain, int object, int action, int line );

          bool operator< ( const StackFrame& rhs ) const;
          bool operator== ( const StackFrame& rhs ) const;
          friend std::ostream& operator<< ( std::ostream& stream, const StackFrame& rhs );

          int getLine() const { return line; } 

          std::size_t hash_value() const
          {
            std::size_t seed = 0;
            boost::hash_combine(seed,type);
            boost::hash_combine(seed,domain);
            boost::hash_combine(seed,object);
            boost::hash_combine(seed,action);
            boost::hash_combine(seed,line);
            return seed;
          }
        private:
          SWA::StackFrame::ActionType type;
          int domain;
          int object;
          int action;
          int line;
      };

      class Time
      {
        public:
          Time( const StackFrame& pos );
          const SWA::Duration& getReal() const    { return real; }
          const SWA::Duration& getUser() const    { return user; }
          const SWA::Duration& getSystem() const  { return system; }
          const StackFrame& getFrame() const      { return frame; }
 
        private:
          SWA::Duration real;
          SWA::Duration user;
          SWA::Duration system;
          StackFrame frame;
      };


      class Statistic
      {
        public:
          Statistic();
          void operator+= ( const Time& startTime );
          int getCount() const                    { return count; }
          const SWA::Duration& getReal() const    { return real; }
          const SWA::Duration& getUser() const    { return user; }
          const SWA::Duration& getSystem() const  { return system; }
        private:
          int count;
          SWA::Duration real;
          SWA::Duration user;
          SWA::Duration system;
      };


    public:
     static CodeCoverage& getInstance();

     virtual std::string getName() { return "Code Coverage"; }

     virtual void startStatement();
     virtual void endStatement();
 
     bool initialise();

     void addLineXML ( Poco::XML::AutoPtr<Poco::XML::Element> parent, StackFrame frame ) const;

     void addServiceXML ( Poco::XML::AutoPtr<Poco::XML::Element> parent, const SWA::ServiceMetaData& state,  SWA::StackFrame::ActionType type, int domain, int object ) const;
     void addStateXML ( Poco::XML::AutoPtr<Poco::XML::Element> parent, const SWA::StateMetaData& state,  int domain, int object ) const;
     void printReport() const;
     void writeReport( std::ostream& stream ) const;
     void saveReport( const std::string& filename ) const;
     void clearStats() { statistics.clear(); startTime = SWA::Timestamp::now(); samplingTime = SWA::Duration::zero(); }
     void setActive( bool active );
     bool isActive() const { return active; }

     CodeCoverage();
     ~CodeCoverage();


    private:
      bool active;
      SWA::Timestamp startTime;
      SWA::Duration samplingTime;
      SWA::Stack& processStack;
      std::vector<Time> timeStack;

      typedef boost::unordered_map<StackFrame,Statistic> Statistics;
      Statistics statistics;

  };

  std::size_t hash_value ( const CodeCoverage::StackFrame& stackFrame ) { return stackFrame.hash_value(); }

}

#endif
