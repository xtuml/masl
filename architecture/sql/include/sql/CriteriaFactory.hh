/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sql_CriteriaFactory_HH
#define Sql_CriteriaFactory_HH

#include <memory>

#include "Criteria.hh"

namespace SQL {

// *****************************************************************
// *****************************************************************
class CloneableCriteria {
  protected:
    CloneableCriteria() {}

  public:
    virtual ~CloneableCriteria() {}

    virtual std::shared_ptr<CriteriaImpl> clone() const = 0;

  private:
    CloneableCriteria(const CloneableCriteria &rhs);
    CloneableCriteria &operator=(const CloneableCriteria &rhs);
};

// *****************************************************************
// *****************************************************************
class CriteriaFactory {
  public:
    // ****************************************************
    //! Return the single instance of this factory.
    // ****************************************************
    static CriteriaFactory &singleton();

    // ****************************************************
    //! Register the Criteria implementation that should be
    //! used by the sql framework. Only one registration should
    //! be undertaken. This factory will take ownership of
    //! the supplied Criteria instance.
    // ****************************************************
    bool registerImpl(const std::shared_ptr<CloneableCriteria> &impl);

    // ****************************************************
    //! @return a cloned version of the registered CloneableCriteria instance
    // ****************************************************
    std::shared_ptr<CriteriaImpl> newInstance();

  private:
    CriteriaFactory(const CriteriaFactory &rhs);
    CriteriaFactory &operator=(const CriteriaFactory &rhs);

  private:
    CriteriaFactory();
    ~CriteriaFactory();

  private:
    std::shared_ptr<CloneableCriteria> impl_;
};

} // namespace SQL

#endif
