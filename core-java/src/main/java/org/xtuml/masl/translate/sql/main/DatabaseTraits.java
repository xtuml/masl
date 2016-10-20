/*
 * Filename : DatabaseTraits.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.sql.main;

import java.util.Collection;
import java.util.List;

import org.xtuml.masl.cppgen.CodeBlock;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.ThrowStatement;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;


public interface DatabaseTraits
{

  /**
   * @return database product being used by translation.
   */
  public String getName ();

  public Namespace getNameSpace ();

  public String getLibrarySuffix ();

  ObjectToTableTranslator createObjectToTableTranslator ( ObjectTranslator objectTranslator, ObjectDeclaration objectDeclaration );

  BinaryRelationshipToTableTranslator createBinaryRelationshipToTableTranslator ( NormalRelationshipDeclaration relationshipDeclaration );

  TenaryRelationshipToTableTranslator createTenaryRelationshipToTableTranslator ( AssociativeRelationshipDeclaration relationshipDeclaration );

  SubTypeRelationshipToTableTranslator createSubTypeRelationshipToTableTranslator ( SubtypeRelationshipDeclaration relationshipDeclaration,
                                                                                    ObjectDeclaration derivedObject );

  public void addEventCode ( Namespace namespace, CodeFile codeFile, ObjectDeclaration object, EventDeclaration event );

  public ThrowStatement throwDatabaseException ( String error );

  public ThrowStatement throwDatabaseException ( Expression error );

  public org.xtuml.masl.cppgen.Class getBlobClass ();

  interface SqlCritera
  {

    public boolean isEmpty ();

    public Variable getVariable ();

    public void beginCondition ();

    public void endCondition ();

    public void addOperator ( org.xtuml.masl.metamodel.expression.BinaryExpression.Operator operator );

    public void addOperator ( org.xtuml.masl.metamodel.expression.UnaryExpression.Operator operator );

    public void addAttributeNameOperand ( AttributeDeclaration attribute );

    public void addRefAttributeNameOperand ( AttributeDeclaration attribute );

    Collection<ObjectDeclaration> getDependentObjects ();

    Collection<RelationshipDeclaration> getDependentRelationship ();

    public void addParameterOperand ( BasicType paramType, String parameter );

    Expression concatenateWhereClause ( List<Expression> expression );

    public void appendStatements ( CodeBlock block );
  }

  SqlCritera createSqlCriteria ( ObjectDeclaration sourceObject, String criteriaVarName );

}
