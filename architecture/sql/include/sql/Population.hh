/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_Population_HH
#define Sql_Population_HH

#include "swa/ObjectPtr.hh"
#include "swa/Set.hh"
#include "swa/types.hh"

#include "Exception.hh"
#include <memory>

namespace SQL {

    // *****************************************************************
    //! \brief
    //!
    //!
    //!
    // *****************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    class SqlPopulation : public MainPopulation {
      public:
        typedef PSMapper MapperType;
        typedef Object ObjectType;
        typedef PSObject PsObjectType;
        typedef SWA::ObjectPtr<Object> ObjectPtr;
        typedef SWA::ObjectPtr<PSObject> PsObjectPtr;
        typedef SWA::Set<PsObjectPtr> PsObjectPtrSet;

        // ********************************************************************
        //!
        // ********************************************************************
        virtual void deleteInstance(ObjectPtr instance);

        // ********************************************************************
        //!
        // ********************************************************************
        ObjectPtr getInstance(::SWA::IdType id) const;

        // ********************************************************************
        //!
        // ********************************************************************
        ::std::size_t size() const;

        // ********************************************************************
        //!
        // ********************************************************************
        bool initialise();

        // ********************************************************************
        //!
        // ********************************************************************
        void markAsDirty(const ::SWA::IdType &identity);

        // ********************************************************************
        //!
        // ********************************************************************
        void writeOnChange(const bool enable) const;

        // ********************************************************************
        //!
        // ********************************************************************
        void forceFlush() const;

        // ********************************************************************
        //!
        // ********************************************************************
        ObjectPtr findOne() const;

        // ********************************************************************
        //!
        // ********************************************************************
        ObjectPtr findOnly() const;

        // ********************************************************************
        //!
        // ********************************************************************
        ::SWA::Set<ObjectPtr> findAll() const;

      protected:
        // ********************************************************************
        //! Constructor
        // ********************************************************************
        SqlPopulation();

        // ********************************************************************
        //! Destructor
        // ********************************************************************
        virtual ~SqlPopulation();

        // ********************************************************************
        //! \return the next available unique architecute id.
        // ********************************************************************
        virtual ::SWA::IdType getNextArchId();

      protected:
        std::shared_ptr<PSMapper> mapper;
    };

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::SqlPopulation()
        : mapper(new PSMapper) {}

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::~SqlPopulation() {}

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    ::SWA::IdType SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::getNextArchId() {
        return mapper->getNextArchId();
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    void SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::deleteInstance(ObjectPtr instance) {
        mapper->deleteInstance(instance);
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    typename SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::ObjectPtr
    SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::getInstance(::SWA::IdType id) const {
        return mapper->getInstance(id);
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    ::std::size_t SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::size() const {
        return mapper->size();
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    bool SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::initialise() {
        return mapper->initialise();
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    void SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::markAsDirty(const ::SWA::IdType &identity) {
        mapper->markAsDirty(identity);
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    void SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::writeOnChange(const bool enable) const {
        mapper->writeOnChange(enable);
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    void SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::forceFlush() const {
        mapper->forceFlush();
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    typename SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::ObjectPtr
    SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::findOne() const {
        return mapper->findOne();
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    typename SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::ObjectPtr
    SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::findOnly() const {
        // If zero instance count then just return null object, if more
        // than one object then throw an exception.

        if (mapper->size() > 1) {
            throw SqlException(::boost::make_tuple(
                "SqlPopulation::findOnly - more than one object found", "[", mapper->getObjectName(), "]"
            ));
        }

        ObjectPtr object;
        if (mapper->size() == 1) {
            object = mapper->findOne();
        }
        return object;
    }

    // *****************************************************************************
    // *****************************************************************************
    template <class Object, class PSObject, class PSMapper, class MainPopulation>
    ::SWA::Set<typename SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::ObjectPtr>
    SqlPopulation<Object, PSObject, PSMapper, MainPopulation>::findAll() const {
        ::SWA::Set<ObjectPtr> instanceSet;
        mapper->findAll(instanceSet);
        instanceSet.forceUnique();
        return instanceSet;
    }

} // namespace SQL

#endif
