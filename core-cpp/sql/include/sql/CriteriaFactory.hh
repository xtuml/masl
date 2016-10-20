//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   CriteriaFactory.hh
//
//============================================================================//
#ifndef Sql_CriteriaFactory_HH
#define Sql_CriteriaFactory_HH

#include "boost/shared_ptr.hpp"

#include "Criteria.hh"

namespace SQL {

// *****************************************************************
// *****************************************************************
class CloneableCriteria
{
    protected:
        CloneableCriteria(){}

    public:
        virtual ~CloneableCriteria() {}

        virtual boost::shared_ptr<CriteriaImpl> clone() const = 0;

   private:
      CloneableCriteria (const CloneableCriteria& rhs);
      CloneableCriteria& operator=(const CloneableCriteria& rhs);
};

// *****************************************************************
// *****************************************************************
class CriteriaFactory
{
   public:
     // ****************************************************
     //! Return the single instance of this factory.
     // ****************************************************
     static CriteriaFactory& singleton();
     
     // ****************************************************
     //! Register the Criteria implementation that should be
     //! used by the sql framework. Only one registration should
     //! be undertaken. This factory will take ownership of
     //! the supplied Criteria instance. 
     // ****************************************************
     bool registerImpl(const boost::shared_ptr<CloneableCriteria>& impl);

     // ****************************************************
     //! @return a cloned version of the registered CloneableCriteria instance 
     // ****************************************************
     boost::shared_ptr<CriteriaImpl> newInstance();

   private:
      CriteriaFactory(const CriteriaFactory& rhs);
      CriteriaFactory& operator=(const CriteriaFactory& rhs);

   private:
      CriteriaFactory();
     ~CriteriaFactory();

   private:
      boost::shared_ptr<CloneableCriteria> impl_;
};

} // end SQL namespace

#endif
