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

public interface RelationshipToTableTranslator {

    String getTableName();

    String getCreateTableStatement();

    void createRowCountQuery(String className, Function implFunction);

    PreparedStatement createPreparedStatement(PreparedStatement.PreparedStatementType classification);
}
