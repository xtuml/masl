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

#ifndef TRANSIENT_ToManyAssociative_HH
#define TRANSIENT_ToManyAssociative_HH

#include "PairFirstIterator.hh"
#include "PairSecondIterator.hh"
#include "swa/ProgramError.hh"
#include <boost/unordered_map.hpp>
#include <boost/bind/bind.hpp>

namespace transient
{
  using namespace boost::placeholders;

  template <class Related, class Associative>
  class ToManyAssociative
  {
    private:
      typedef SWA::ObjectPtr<Related> RelatedPtr;
      typedef SWA::ObjectPtr<Associative> AssociativePtr;
      typedef boost::unordered_map<RelatedPtr,AssociativePtr> Container;

    public:
      void link ( RelatedPtr rhs, AssociativePtr assoc );
      void unlink ( RelatedPtr rhs, AssociativePtr assoc );
 
      AssociativePtr correlate ( RelatedPtr rhs ) const;

      SWA::Set<RelatedPtr> navigate() const { return SWA::Set<RelatedPtr>(begin(),end(),true); }

      template<class Predicate>
      SWA::Set<RelatedPtr> navigate( Predicate predicate ) const
      { 
        SWA::Set<RelatedPtr> result;
        SWA::copy_if(begin(),end(),result.inserter(),boost::bind(predicate,boost::bind(&RelatedPtr::deref,_1)));
        result.forceUnique();
        return result;
      }


      SWA::Set<AssociativePtr> navigateAssociative() const { return SWA::Set<AssociativePtr>(assocBegin(),assocEnd(),true); }

      template<class Predicate>
      SWA::Set<AssociativePtr> navigateAssociative( Predicate predicate ) const
      { 
        SWA::Set<AssociativePtr> result;
        SWA::copy_if(assocBegin(),assocEnd(),result.inserter(),boost::bind(predicate,boost::bind(&AssociativePtr::deref,_1)));
        result.forceUnique();
        return result;
      }



      std::size_t count() const { return related.size(); }

    private:
      Container related;

      typedef PairFirstIterator<typename Container::const_iterator> iterator;
      typedef PairSecondIterator<typename Container::const_iterator> assocIterator;

      iterator begin() const { return iterator(related.begin()); }
      iterator end()  const { return iterator(related.end()); };

      assocIterator assocBegin() const { return assocIterator(related.begin()); }
      assocIterator assocEnd() const { return assocIterator(related.end()); };

  };

  template <class Related, class Associative>
  void ToManyAssociative<Related,Associative>::link ( RelatedPtr rhs, AssociativePtr assoc )
  { 
    if ( !related.insert(typename Container::value_type(rhs,assoc)).second )
    {
      throw SWA::ProgramError("Objects already linked");
    }
  }


  template <class Related, class Associative>
  void ToManyAssociative<Related,Associative>::unlink ( RelatedPtr rhs, AssociativePtr assoc )
  {
    // Find the main object
    typename Container::iterator pos = related.find(rhs); 
    if ( pos == related.end() || pos->second != assoc ) throw SWA::ProgramError("Objects not linked");

    // Remove the link
    related.erase(pos);
  }

  template <class Related, class Associative>
  typename ToManyAssociative<Related,Associative>::AssociativePtr ToManyAssociative<Related,Associative>::correlate ( RelatedPtr rhs ) const
  { 
    typename Container::const_iterator it = related.find(rhs);
    return ( it == related.end() ) ? AssociativePtr() : it->second;
  }


}
#endif
