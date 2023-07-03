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

#ifndef TRANSIENT_ToOneRelationship_HH
#define TRANSIENT_ToOneRelationship_HH

#include "swa/ProgramError.hh"
#include "swa/ObjectPtr.hh"

namespace transient
{
  //
  //  --->Object
  //
  template <class Related>
  class ToOneRelationship
  {
    private:
      typedef SWA::ObjectPtr<Related> RelatedPtr;

    public:
      ToOneRelationship() : related() {}

      void link ( RelatedPtr rhs ) { if ( related ) throw SWA::ProgramError("Attempt to overwrite relationship"); related = rhs; }
      void unlink ( RelatedPtr rhs ) { if ( related != rhs ) throw SWA::ProgramError("Objects not linked"); related = RelatedPtr(); }

      RelatedPtr navigate() const { return related; }

      template<class Predicate> 
      RelatedPtr navigate( Predicate predicate ) const { return related && predicate(related.deref()) ? related : RelatedPtr(); }

      std::size_t count() const { return related?1:0; } 

    private:
        RelatedPtr related;
  };

}

#endif
