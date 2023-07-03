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

#ifndef TRANSIENT_ToManyRelationship_HH
#define TRANSIENT_ToManyRelationship_HH

#include "swa/ProgramError.hh"
#include <boost/unordered_set.hpp>
#include <boost/bind/bind.hpp>

namespace transient
{

  using namespace boost::placeholders;


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
