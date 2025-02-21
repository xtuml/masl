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

public interface SubTypeRelationshipToTableTranslator extends RelationshipToTableTranslator {

    String getLeftColumnName();

    String getRightColumnName();

    String getTypeColumnName();

    void addLoadAllBody(Function loadAllFn, Variable lhsToRhsLinkSet, Variable rhsToLhsLinkSet);

    void addLoadLhsBody(Function loadLhsFn, Variable identityVar, Variable lhsToRhsLinkSet, Variable rhsToLhsLinkSet);

    void addLoadRhsBody(Function loadRhsFn, Variable identityVar, Variable lhsToRhsLinkSet, Variable rhsToLhsLinkSet);
}
