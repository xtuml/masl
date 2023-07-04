/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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
