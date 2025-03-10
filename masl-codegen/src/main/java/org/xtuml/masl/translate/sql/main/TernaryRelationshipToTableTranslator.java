/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.sql.main;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Variable;

public interface TernaryRelationshipToTableTranslator extends RelationshipToTableTranslator {

    String getLeftColumnName();

    String getRightColumnName();

    String getAssocColumnName();

    void addLoadAllBody(Function loadAllFn, Variable cachedTernaryContVar);

    void addLoadLhsBody(Function loadLhsFn, Variable rhsIdentityVar, Variable cachedTernaryContVar);

    void addLoadRhsBody(Function loadRhsFn, Variable lhsIdentityVar, Variable cachedTernaryContVar);

    void addLoadAssBody(Function loadAssFn, Variable assIdentityVar, Variable cachedTernaryContVar);
}
