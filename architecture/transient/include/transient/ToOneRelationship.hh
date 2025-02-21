/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef TRANSIENT_ToOneRelationship_HH
#define TRANSIENT_ToOneRelationship_HH

#include <swa/ObjectPtr.hh>
#include <swa/ProgramError.hh>

namespace transient {
//
//  --->Object
//
template <class Related> class ToOneRelationship {
  private:
    typedef SWA::ObjectPtr<Related> RelatedPtr;

  public:
    ToOneRelationship() : related() {}

    void link(RelatedPtr rhs) {
        if (related)
            throw SWA::ProgramError("Attempt to overwrite relationship");
        related = rhs;
    }
    void unlink(RelatedPtr rhs) {
        if (related != rhs)
            throw SWA::ProgramError("Objects not linked");
        related = RelatedPtr();
    }

    RelatedPtr navigate() const { return related; }

    template <class Predicate> RelatedPtr navigate(Predicate predicate) const {
        return related && predicate(related.deref()) ? related : RelatedPtr();
    }

    std::size_t count() const { return related ? 1 : 0; }

  private:
    RelatedPtr related;
};

} // namespace transient

#endif
