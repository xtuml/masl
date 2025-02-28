/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_ObjectMapperItr_HH
#define Sql_ObjectMapperItr_HH

#include <set>

namespace SQL {

    // *****************************************************************
    //! \brief A Functor class that uses a predicate to select required objects.
    //!
    //! When the full set of objects has been loaded into the ObjectMapper cache
    //! it can prove more efficient to undetake a linear find operation using
    //! these transient objects rather than forwarding find calls onto the
    //! database, which would undertake a suitable query. This functor class
    //! can be used with the std::for_each algorithm to obtain the set of objects
    //! that matches the find predicate.
    //!
    //! The predicates that are used are formed from the find predicate
    //! definitions located in the base class business objects.
    // *****************************************************************
    template <class Map, class Set, class Pred>
    class ObjectMapperSelector : public std::unary_function<typename Map::value_type, void> {
      public:
        ObjectMapperSelector(Set &selectedSet, Pred &predicate)
            : pred(predicate), selected(selectedSet) {}
        ~ObjectMapperSelector() {}

        void operator()(const typename Map::value_type &obj) {
            if (pred(obj.second.get()) == true) {
                selected += typename Set::value_type((obj.second.get()));
            }
        }

      private:
        Pred &pred;
        Set &selected;
    };

    // *****************************************************************
    //! Helper function to undetake the dependent lookup required
    //! to form objects of the ObjectMapperSelector type.
    // *****************************************************************
    template <class Map, class Set, class Pred>
    ObjectMapperSelector<Map, Set, Pred> objectMapperSelector(Map &cachedMap, Set &selectedSet, Pred &predicate) {
        return ObjectMapperSelector<Map, Set, Pred>(selectedSet, predicate);
    }

    // *****************************************************************
    //! \brief A Functor class that uses a predicate to select a required object.
    //!
    // *****************************************************************
    template <class Map, class Obj, class Pred>
    class SingleObjectSelector : public std::unary_function<typename Map::value_type, bool> {
      public:
        SingleObjectSelector(Obj &object, Pred &predicate)
            : selectObj(object), pred(predicate) {}
        ~SingleObjectSelector() {}

        bool operator()(const typename Map::value_type &obj) {
            bool found = false;
            if (pred(obj.second.get()) == true) {
                selectObj = Obj(obj.second.get());
                found = true;
            }
            return found;
        }

      private:
        Obj &selectObj;
        Pred &pred;
    };

    // *****************************************************************
    //! Helper function to undetake the dependent lookup required
    //! to form objects of the SingleObjectSelector type.
    // *****************************************************************
    template <class Map, class Obj, class Pred>
    SingleObjectSelector<Map, Obj, Pred> singleObjectSelector(Map &cachedMap, Obj &selectedObj, Pred &predicate) {
        return SingleObjectSelector<Map, Obj, Pred>(selectedObj, predicate);
    }

    // *****************************************************************
    //! \brief A Map to Set translator Iterator.
    //!
    //! The mapper stores the reformed rows as objects held in a map, keyed
    //! against their unique architecture ids. The objects are held in smart
    //! pointers to guard against memory leaks. Translations from this
    //! representation to a set of objects held as SWA:::ObjectPr types are
    //! required. This iterator undertakes the translation.
    // *****************************************************************
    template <class Map, class Set>
    class MapToSetLoadIterator : public std::iterator<std::output_iterator_tag, void, void, void, void> {
      public:
        explicit MapToSetLoadIterator(Set &cont)
            : container(cont) {}

        MapToSetLoadIterator<Map, Set> &operator=(const typename Map::value_type &value) {
            container += typename Set::value_type(value.second.get());
            return *this;
        }

        // dereferencing is a no-op that returns the iterator itself
        MapToSetLoadIterator<Map, Set> &operator*() {
            return *this;
        }

        // increment operation is a no-op that returns the iterator itself
        MapToSetLoadIterator<Map, Set> &operator++() {
            return *this;
        }
        MapToSetLoadIterator<Map, Set> &operator++(int) {
            return *this;
        }

      private:
        Set &container;
    };

    // *****************************************************************
    //! Helper function to undetake the dependent lookup required
    //! to form objects of the MapToSetInsertIterator type.
    // *****************************************************************
    template <class Map, class Set>
    MapToSetLoadIterator<Map, Set> MapToSetLoader(const Map &map, Set &set) {
        return MapToSetLoadIterator<Map, Set>(set);
    }

    // *****************************************************************
    //! \brief  A Map to Set translator Iterator using a selection criteria.
    //!
    //!
    // *****************************************************************
    template <class Map, class Set>
    class MapToSetKeyComparatorItr : public std::iterator<std::output_iterator_tag, void, void, void, void> {
      public:
        typedef std::set<typename Map::key_type> SourceSet;

        explicit MapToSetKeyComparatorItr(const Map &map, Set &set)
            : sourceMap(map), destinationSet(set) {}

        MapToSetKeyComparatorItr<Map, Set> &operator=(const typename SourceSet::key_type &key) {
            typename Map::const_iterator matchingKeyItr = sourceMap.find(key);
            if (matchingKeyItr != sourceMap.end()) {
                destinationSet.insert(typename Set::key_type((*matchingKeyItr).second.get()));
            }
            return *this;
        }

        // dereferencing is a no-op that returns the iterator itself
        MapToSetKeyComparatorItr<Map, Set> &operator*() {
            return *this;
        }

        // increment operation is a no-op that returns the iterator itself
        MapToSetKeyComparatorItr<Map, Set> &operator++() {
            return *this;
        }
        MapToSetKeyComparatorItr<Map, Set> &operator++(int) {
            return *this;
        }

      private:
        const Map &sourceMap;
        Set &destinationSet;
    };

    // *****************************************************************
    //! Helper function to undetake the dependent lookup required
    //! to form objects of the MapToSetKeyComparatorItr type.
    // *****************************************************************
    template <class Map, class Set>
    MapToSetKeyComparatorItr<Map, Set> MapToSetKeyComparator(const Map &map, Set &set) {
        return MapToSetKeyComparatorItr<Map, Set>(map, set);
    }

    // *****************************************************************
    //! \brief
    //!
    // *****************************************************************
    template <class Map, class Set>
    class LoadCacheIterator : public std::iterator<std::output_iterator_tag, void, void, void, void> {
      public:
        explicit LoadCacheIterator(Map &dest)
            : destination(dest) {}

        LoadCacheIterator<Map, Set> &operator=(const typename Set::key_type &currentObj) {
            if (destination.find(currentObj->getArchitectureId()) == destination.end()) {
                destination.insert(
                    std::make_pair(currentObj->getArchitectureId(), typename Map::mapped_type(currentObj.get()))
                );
            }
            return *this;
        }

        // dereferencing is a no-op that returns the iterator itself
        LoadCacheIterator<Map, Set> &operator*() {
            return *this;
        }

        // increment operation is a no-op that returns the iterator itself
        LoadCacheIterator<Map, Set> &operator++() {
            return *this;
        }
        LoadCacheIterator<Map, Set> &operator++(int) {
            return *this;
        }

      private:
        Map &destination;
    };

    // *****************************************************************
    //! Helper function to undertake the dependent lookup required
    //! to form objects of the LoadCacheIterator type.
    // *****************************************************************
    template <class Map, class Set>
    LoadCacheIterator<Map, Set> LoadCacheItr(Map &map, Set &set) {
        return LoadCacheIterator<Map, Set>(map);
    }

} // namespace SQL

#endif
