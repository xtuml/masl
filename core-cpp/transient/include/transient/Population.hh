//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef TRANSIENT_Population_HH
#define TRANSIENT_Population_HH

#include <boost/unordered_map.hpp>
#include "PairSecondIterator.hh"
#include "swa/collection.hh"
#include "ThreadListener.hh"
#include "boost/bind.hpp"

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
