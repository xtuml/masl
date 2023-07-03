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

#ifndef TRANSIENT_Population_HH
#define TRANSIENT_Population_HH

#include <boost/unordered_map.hpp>
#include "PairSecondIterator.hh"
#include "swa/collection.hh"
#include "ThreadListener.hh"
#include "boost/bind/bind.hpp"

namespace transient
{
  template<class Object, class MainPopulation>
  class TransientPopulation : public MainPopulation
  {
    private:
      typedef SWA::ObjectPtr<Object> ObjectPtr;
      typedef boost::unordered_map< ::SWA::IdType, ObjectPtr> PopType;

    public:
      TransientPopulation()
        : maxArchId(0)
      {
      }

      virtual ObjectPtr getInstance ( ::SWA::IdType id ) const
      {
        typename PopType::const_iterator pos = population.find(id);
        if ( pos != population.end() ) return pos->second;
        else return ObjectPtr();
      }

      virtual ::SWA::IdType getNextArchId()
      {
        return ++maxArchId;
      }

      virtual std::size_t size() const
      {
        return population.size();
      }

      virtual ::SWA::Set<ObjectPtr> findAll() const { return ::SWA::Set<ObjectPtr> (begin(),end(),true); }
      virtual ObjectPtr findOne() const { return ::SWA::find_one(begin(),end()); };
      virtual ObjectPtr findOnly() const { return ::SWA::find_only(begin(),end()); };

      virtual ~TransientPopulation()
      {
        for ( typename PopType::iterator it = population.begin(); it != population.end(); ++it )
        {
          delete it->second.get();
        }
        population.clear();
      }
                                        
      virtual void deleteInstance ( ObjectPtr instance ) 
      {
        population.erase(instance->getArchitectureId());
        instanceDeleted(instance);
        // Don't actually delete the instance until the end of 
        // the thread, as it may still be in use, eg in a 
        // polymorphic event handler. 
        ThreadListener::getInstance().addCleanup(boost::bind(cleanupInstance,instance));
      }

    private:
      static void cleanupInstance ( ObjectPtr instance )
      {
        delete instance.get();
      }

    private:
      virtual void instanceDeleted(ObjectPtr instance) {};
      virtual void instanceCreated(ObjectPtr instance) {};
  
    protected:
      void addInstance ( ObjectPtr instance )
      { 
        population.insert(typename PopType::value_type(instance->getArchitectureId(),instance));
        instanceCreated(instance);
      }

      typedef PairSecondIterator<typename PopType::const_iterator> iterator;

      virtual iterator begin() const { return iterator(population.begin()); }
      virtual iterator end()  const { return iterator(population.end()); }
        
    private:
      PopType population;
      ::SWA::IdType maxArchId;

  };
}

#endif
