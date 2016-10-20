//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef TRANSIENT_ToManyRelationship_HH
#define TRANSIENT_ToManyRelationship_HH

#include "swa/ProgramError.hh"
#include <boost/unordered_set.hpp>
#include <boost/bind.hpp>

namespace transient
{
  template <class Related>
  class ToManyRelationship
  {
    private:
      typedef SWA::ObjectPtr<Related> RelatedPtr;
      typedef boost::unordered_set<RelatedPtr> Container;

    public:
      void link ( RelatedPtr rhs ) { if ( !related.insert(rhs).second ) throw SWA::ProgramError ("Objects already linked"); }
      void unlink ( RelatedPtr rhs ) { if ( !related.erase(rhs) ) throw SWA::ProgramError ("Objects not linked"); }

      SWA::Set<RelatedPtr> navigate() const { return SWA::Set<RelatedPtr>(related); }

      template<class Predicate>
      SWA::Set<RelatedPtr> navigate( Predicate predicate ) const
      { 
        SWA::Set<RelatedPtr> result;
        SWA::copy_if(related.begin(),related.end(),result.inserter(),boost::bind(predicate,boost::bind(&RelatedPtr::deref,_1)));
        result.forceUnique();
        return result;
      }

      std::size_t count() const { return related.size(); }

    private:
      Container related;

  };

}


#endif
