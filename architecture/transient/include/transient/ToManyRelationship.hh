/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#pragma once

#include <swa/ProgramError.hh>
#include <unordered_set>

namespace transient {

template <class Related> class ToManyRelationship {
  private:
    typedef SWA::ObjectPtr<Related> RelatedPtr;
    typedef std::unordered_set<RelatedPtr> Container;

  public:
    void link(RelatedPtr rhs) {
        if (!related.insert(rhs).second)
            throw SWA::ProgramError("Objects already linked");
    }
    void unlink(RelatedPtr rhs) {
        if (!related.erase(rhs))
            throw SWA::ProgramError("Objects not linked");
    }

    SWA::Set<RelatedPtr> navigate() const {
        return SWA::Set<RelatedPtr>(related);
    }

    template <class Predicate>
    SWA::Set<RelatedPtr> navigate(Predicate predicate) const {
        SWA::Set<RelatedPtr> result;
        SWA::copy_if(
            related.begin(), related.end(), result.inserter(),
            [predicate](auto &&ptr) { return predicate(ptr.deref()); });
        result.forceUnique();
        return result;
    }

    std::size_t count() const { return related.size(); }

  private:
    Container related;
};

} // namespace transient
