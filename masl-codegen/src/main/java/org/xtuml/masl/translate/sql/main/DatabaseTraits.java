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

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;

import java.util.Collection;
import java.util.List;

public interface DatabaseTraits {

    /**
     * @return database product being used by translation.
     */
    String getName();

    Namespace getNameSpace();

    String getLibrarySuffix();

    ObjectToTableTranslator createObjectToTableTranslator(ObjectTranslator objectTranslator,
                                                          ObjectDeclaration objectDeclaration);

    BinaryRelationshipToTableTranslator createBinaryRelationshipToTableTranslator(NormalRelationshipDeclaration relationshipDeclaration);

    TernaryRelationshipToTableTranslator createTernaryRelationshipToTableTranslator(AssociativeRelationshipDeclaration relationshipDeclaration);

    SubTypeRelationshipToTableTranslator createSubTypeRelationshipToTableTranslator(SubtypeRelationshipDeclaration relationshipDeclaration,
                                                                                    ObjectDeclaration derivedObject);

    void addEventCode(Namespace namespace, CodeFile codeFile, ObjectDeclaration object, EventDeclaration event);

    ThrowStatement throwDatabaseException(String error);

    ThrowStatement throwDatabaseException(Expression error);

    org.xtuml.masl.cppgen.Class getBlobClass();

    interface SqlCritera {

        boolean isEmpty();

        Variable getVariable();

        void beginCondition();

        void endCondition();

        void addOperator(org.xtuml.masl.metamodel.expression.BinaryExpression.Operator operator);

        void addOperator(org.xtuml.masl.metamodel.expression.UnaryExpression.Operator operator);

        void addAttributeNameOperand(AttributeDeclaration attribute);

        void addRefAttributeNameOperand(AttributeDeclaration attribute);

        Collection<ObjectDeclaration> getDependentObjects();

        Collection<RelationshipDeclaration> getDependentRelationship();

        void addParameterOperand(BasicType paramType, String parameter);

        Expression concatenateWhereClause(List<Expression> expression);

        void appendStatements(CodeBlock block);
    }

    SqlCritera createSqlCriteria(ObjectDeclaration sourceObject, String criteriaVarName);

}
