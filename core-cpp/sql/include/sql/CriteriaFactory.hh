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
