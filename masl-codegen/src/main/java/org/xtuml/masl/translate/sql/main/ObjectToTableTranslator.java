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
import org.xtuml.masl.metamodel.object.AttributeDeclaration;

import java.util.List;

public interface ObjectToTableTranslator {

    String getTableName();

    int getArchIdColumnIndex();

    int getArchIdBindIndex();

    List<String> getColumnNameList();

    List<String> getAttributeNameList();

    String getCreateTableStatement();

    boolean isBlobColumn(AttributeDeclaration attribute);

    int getColumnIndex(AttributeDeclaration attribute);

    int getBindIndex(AttributeDeclaration attribute);

    boolean hasCurrentStateColumn();

    int getCurrentStateBindIndex();

    PreparedStatement createPreparedStatement(PreparedStatement.PreparedStatementType classification);

    void addGetMaxFnBody(String className, Variable attributeName, Function executeGetMaxFn);

    void addGetMaxIdFnBody(String className, Function executeGetMaxFn);

    void addGetRowCountFnBody(String className, Function executeGetRowCountFn);

    void addExecuteSelectBody(Function selectFn,
                              Variable cacheParameter,
                              Variable criteriaParameter,
                              Variable resultParameter);

    void addExecuteSelectBody(Function selectFn, Variable cacheParameter, Variable criteriaParameter);

}
