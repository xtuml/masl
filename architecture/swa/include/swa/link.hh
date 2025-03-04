/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_link_HH
#define SWA_link_HH

#include "Bag.hh"
#include "Set.hh"
#include "combine_collection.hh"

namespace SWA {

    // unlink all collection non associative
    template <class LhsObj, template <class T> class LhsColl>
    void unlink(const LhsColl<ObjectPtr<LhsObj>> &lhs, void (LhsObj::*unlinker)()) {
        for (typename LhsColl<ObjectPtr<LhsObj>>::const_iterator it = lhs.begin(), end = lhs.end(); it != end; ++it) {
            (it->get()->*unlinker)();
        }
    }

    // link collection with collection to many
    template <
        class LhsObj,
        template <class T> class LhsColl,
        class RhsObj,
        template <class T> class RhsColl,
        class AssocObj>
    Set<ObjectPtr<AssocObj>> link(
        const LhsColl<ObjectPtr<LhsObj>> &lhs,
        const RhsColl<ObjectPtr<RhsObj>> &rhs,
        Set<ObjectPtr<AssocObj>> (LhsObj::*linker)(const Bag<ObjectPtr<RhsObj>> &)
    ) {
        Set<ObjectPtr<AssocObj>> result =
            combine_collection<Set, ObjectPtr<AssocObj>>(lhs, [&](const ObjectPtr<LhsObj> &l) {
                return (l.get().*linker)(rhs);
            });
        result.forceUnique();
        return result;
    }

    // unlink collection with collection to many
    template <
        class LhsObj,
        template <class T> class LhsColl,
        class RhsObj,
        template <class T> class RhsColl,
        class AssocObj>
    Set<ObjectPtr<AssocObj>> unlink(
        const LhsColl<ObjectPtr<LhsObj>> &lhs,
        const RhsColl<ObjectPtr<RhsObj>> &rhs,
        Set<ObjectPtr<AssocObj>> (LhsObj::*unlinker)(const Bag<ObjectPtr<RhsObj>> &)
    ) {
        Set<ObjectPtr<AssocObj>> result =
            combine_collection<Set, ObjectPtr<AssocObj>>(lhs, [&](const ObjectPtr<LhsObj> &l) {
                return (l.get().*unlinker)(rhs);
            });
        result.forceUnique();
        return result;
    }

    // unlink all collection to one
    template <class LhsObj, template <class T> class LhsColl, class AssocObj>
    Set<ObjectPtr<AssocObj>> unlink(const LhsColl<ObjectPtr<LhsObj>> &lhs, ObjectPtr<AssocObj> (LhsObj::*unlinker)()) {
        Set<ObjectPtr<AssocObj>> result;
        result.reserve(lhs.size());
        for (typename LhsColl<ObjectPtr<LhsObj>>::const_iterator it = lhs.begin(), end = lhs.end(); it != end; ++it) {
            result += (it->get()->*unlinker)();
        }
        result.forceUnique();
        return result;
    }

    // unlink all collection to many
    template <class LhsObj, template <class T> class LhsColl, class AssocObj>
    Set<ObjectPtr<AssocObj>>
    unlink(const LhsColl<ObjectPtr<LhsObj>> &lhs, Set<ObjectPtr<AssocObj>> (LhsObj::*unlinker)()) {
        Set<ObjectPtr<AssocObj>> result =
            combine_collection<Set, ObjectPtr<AssocObj>>(lhs, [&](const ObjectPtr<LhsObj> &l) {
                return (l.get().*unlinker)();
            });
        result.forceUnique();
        return result;
    }

} // namespace SWA

#endif
