/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_correlate_HH
#define SWA_correlate_HH

#include "Bag.hh"
#include "Set.hh"
#include "combine_collection.hh"
#include <iostream>

namespace SWA {

    // Correlate single instances
    template <class LhsObj, class RhsObj, class AssocObj>
    ObjectPtr<AssocObj> correlate_instance(
        const LhsObj *lhs,
        const ObjectPtr<RhsObj> &rhs,
        ObjectPtr<AssocObj> (LhsObj::*correlator)(const ObjectPtr<RhsObj> &) const
    ) {
        return lhs && rhs ? (lhs->*correlator)(rhs) : ObjectPtr<AssocObj>();
    }

    // Correlate single instances
    template <class LhsObj, class RhsObj, class AssocObj>
    ObjectPtr<AssocObj> correlate_instance(
        const ObjectPtr<LhsObj> &lhs,
        const ObjectPtr<RhsObj> &rhs,
        ObjectPtr<AssocObj> (LhsObj::*correlator)(const ObjectPtr<RhsObj> &) const
    ) {
        return lhs && rhs ? (lhs.get()->*correlator)(rhs) : ObjectPtr<AssocObj>();
    }

    // Correlate single instance with bag. We don't put any ordering constraints on
    // the instance based correlator, so result is a bag
    template <class LhsObj, class RhsObj, class AssocObj>
    Bag<ObjectPtr<AssocObj>> correlate_bag(
        const LhsObj *lhs,
        const Bag<ObjectPtr<RhsObj>> &rhs,
        Bag<ObjectPtr<AssocObj>> (LhsObj::*correlator)(const Bag<ObjectPtr<RhsObj>> &) const
    ) {
        return lhs ? (lhs->*correlator)(rhs) : Bag<ObjectPtr<AssocObj>>();
    }

    // Correlate single instance with set
    template <class LhsObj, class RhsObj, class AssocObj>
    Set<ObjectPtr<AssocObj>> correlate_set(
        const LhsObj *lhs,
        const Set<ObjectPtr<RhsObj>> &rhs,
        Bag<ObjectPtr<AssocObj>> (LhsObj::*correlator)(const Bag<ObjectPtr<RhsObj>> &) const
    ) {
        if (lhs) {
            Set<ObjectPtr<AssocObj>> result = (lhs->*correlator)(rhs);
            result.forceUnique();
            return result;
        } else {
            return Set<ObjectPtr<AssocObj>>();
        }
    }

    // Correlate single instance with sequence. We don't put any ordering
    // constraints on the instance based correlator, so result is a bag
    template <class LhsObj, class RhsObj, template <class T> class RhsColl, class AssocObj>
    Bag<ObjectPtr<AssocObj>> correlate_bag(
        const ObjectPtr<LhsObj> &lhs,
        const RhsColl<ObjectPtr<RhsObj>> &rhs,
        Bag<ObjectPtr<AssocObj>> (LhsObj::*correlator)(const Bag<ObjectPtr<RhsObj>> &) const
    ) {
        return lhs ? (lhs.get()->*correlator)(rhs) : Bag<ObjectPtr<AssocObj>>();
    }

    // Correlate single instance with set
    template <class LhsObj, class RhsObj, class AssocObj>
    Set<ObjectPtr<AssocObj>> correlate_set(
        const ObjectPtr<LhsObj> &lhs,
        const Set<ObjectPtr<RhsObj>> &rhs,
        Bag<ObjectPtr<AssocObj>> (LhsObj::*correlator)(const Bag<ObjectPtr<RhsObj>> &) const
    ) {
        if (lhs) {
            Set<ObjectPtr<AssocObj>> result = (lhs.get()->*correlator)(rhs);
            result.forceUnique();
            return result;
        } else {
            return Set<ObjectPtr<AssocObj>>();
        }
    }

    // Correlate collection with collection. No ordering guarantees, so result is a
    // bag.
    template <
        class LhsObj,
        template <class T> class LhsColl,
        class RhsObj,
        template <class T> class RhsColl,
        class AssocObj>
    Bag<ObjectPtr<AssocObj>> correlate_bag(
        const LhsColl<ObjectPtr<LhsObj>> &lhs,
        const RhsColl<ObjectPtr<RhsObj>> &rhs,
        Bag<ObjectPtr<AssocObj>> (LhsObj::*correlator)(const Bag<ObjectPtr<RhsObj>> &) const
    ) {
        return combine_collection<Set, ObjectPtr<AssocObj>>(lhs, [&](const ObjectPtr<LhsObj> &l) {
            return (l.get().*correlator)(rhs);
        });
    }

    // Correlate set with set
    template <class LhsObj, class RhsObj, class AssocObj>
    Set<ObjectPtr<AssocObj>> correlate_set(
        const Set<ObjectPtr<LhsObj>> &lhs,
        const Set<ObjectPtr<RhsObj>> &rhs,
        Bag<ObjectPtr<AssocObj>> (LhsObj::*correlator)(const Bag<ObjectPtr<RhsObj>> &) const
    ) {
        Set<ObjectPtr<AssocObj>> result =
            combine_collection<Set, ObjectPtr<AssocObj>>(lhs, [&](const ObjectPtr<LhsObj> &l) {
                return (l.get().*correlator)(rhs);
            });
        result.forceUnique();
        return result;
    }

} // namespace SWA

#endif
