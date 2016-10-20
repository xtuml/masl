//
// File: DelayStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;



public class LinkUnlinkStatement extends Statement
    implements org.xtuml.masl.metamodel.code.LinkUnlinkStatement
{

  public static LinkUnlinkStatement create ( final Position position,
                                             final Type type,
                                             final Expression lhs,
                                             final RelationshipSpecification.Reference relRef,
                                             final Expression rhs,
                                             final Expression assoc )
  {
    if ( type == null || lhs == null || relRef == null )
    {
      return null;
    }

    try
    {
      return new LinkUnlinkStatement(position, type, lhs, relRef, rhs, assoc);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  public static class Type
  {

    private final String                                                assocText;
    private final String                                                mainText;
    private final org.xtuml.masl.metamodel.code.LinkUnlinkStatement.Type type;

    private Type ( final String mainText, final String assocText, final org.xtuml.masl.metamodel.code.LinkUnlinkStatement.Type type )
    {
      this.mainText = mainText;
      this.assocText = assocText;
      this.type = type;
    }

    public String getAssocText ()
    {
      return assocText;
    }

    public String getMainText ()
    {
      return mainText;
    }

    public org.xtuml.masl.metamodel.code.LinkUnlinkStatement.Type getType ()
    {
      return type;
    }

    @Override
    public String toString ()
    {
      return mainText;
    }
  }

  public static Type                      LINK   = new Type("link",
                                                            "using",
                                                            org.xtuml.masl.metamodel.code.LinkUnlinkStatement.Type.LINK);
  public static Type                      UNLINK = new Type("unlink",
                                                            "using",
                                                            org.xtuml.masl.metamodel.code.LinkUnlinkStatement.Type.UNLINK);
  private final Expression                assoc;

  private final Expression                lhs;
  private final RelationshipSpecification relationship;
  private final Expression                rhs;
  private final Type                      type;
  private final ObjectDeclaration         lhsObj;
  private final ObjectDeclaration         rhsObj;
  private final ObjectDeclaration         assocObj;

  
  private LinkUnlinkStatement ( final Position position,
                                final Type type,
                                final Expression lhs,
                                final RelationshipSpecification.Reference relRef,
                                final Expression rhs,
                                final Expression assoc ) throws SemanticError
  {
    super(position);
    this.type = type;
    this.lhs = lhs;
    this.rhs = rhs;
    this.assoc = assoc;

    lhsObj = ObjectDeclaration.getObject(lhs, true);
    final RelationshipSpecification relationship = relRef.getRelationshipSpec();


    // First check that statement has all the correct information supplied
    if ( rhs == null && type == LINK )
    {
      throw new SemanticError(SemanticErrorCode.LinkMustSupplyRhs, relRef.getPosition());
    }
    else if ( relationship.getRelationship() instanceof AssociativeRelationshipDeclaration && assoc == null )
    {
      throw new SemanticError(SemanticErrorCode.AssocNeedsUsing, relRef.getPosition());
    }
    else if ( assoc != null && !(relationship.getRelationship() instanceof AssociativeRelationshipDeclaration) )
    {
      throw new SemanticError(SemanticErrorCode.NonAssocWithUsing, relRef.getPosition());
    }
    else if ( relationship.isFromAssociative() )
    {
      throw new SemanticError(SemanticErrorCode.NoLinkFromAssoc, lhs.getPosition());
    }
    else if ( relationship.isToAssociative() )
    {
      throw new SemanticError(SemanticErrorCode.NoLinkToAssoc, relRef.getPosition());
    }


    if ( rhs != null )
    {
      // Get the lhs obj again, but with correct cardinality passed through.
      // This is the easiest way to ensure the correct error messages are
      // raised.
      ObjectDeclaration.getObject(lhs, relationship.getReverseSpec().getCardinality() == MultiplicityType.MANY
                                       && !(relationship.getRelationship() instanceof AssociativeRelationshipDeclaration));

      rhsObj = ObjectDeclaration.getObject(rhs, relationship.getCardinality() == MultiplicityType.MANY
                                                && !(relationship.getRelationship() instanceof AssociativeRelationshipDeclaration));
      if ( relationship.getDestinationObject() != rhsObj )
      {
        throw new SemanticError(SemanticErrorCode.ExpectedInstanceOfExpression,
                                rhs.getPosition(),
                                relationship
                                            .getDestinationObject().getName(),
                                rhsObj.getName());
      }
    }
    else
    {
      rhsObj = null;
    }

    if ( assoc != null )
    {
      assocObj = ObjectDeclaration.getObject(assoc, false);
      final ObjectDeclaration reqdObj = ((AssociativeRelationshipDeclaration)relationship.getRelationship()).getAssocObject();
      if ( reqdObj != assocObj )
      {
        throw new SemanticError(SemanticErrorCode.ExpectedInstanceOfExpression,
                                assoc.getPosition(),
                                reqdObj.getName(),
                                assocObj
                                        .getName());
      }

    }
    else
    {
      assocObj = null;
    }

    this.relationship = relationship;
  }


  
  @Override
  public Expression getAssoc ()
  {
    return this.assoc;
  }

  
  @Override
  public Expression getLhs ()
  {
    return this.lhs;
  }

  
  @Override
  public RelationshipSpecification getRelationship ()
  {
    return this.relationship;
  }

  
  @Override
  public Expression getRhs ()
  {
    return this.rhs;
  }

  
  @Override
  public org.xtuml.masl.metamodel.code.LinkUnlinkStatement.Type getLinkType ()
  {
    return this.type.getType();
  }

  @Override
  public String toString ()
  {
    return type.getMainText()
           + " "
           + lhs
           + " "
           + relationship
           + " "
           + (rhs == null ? "" : " " + rhs)
           + (assoc == null ? "" : " " + type.getAssocText() + " " + assoc)
           + ";";
  }


  @Override
  public ObjectDeclaration getAssocObject ()
  {
    return assocObj;
  }


  @Override
  public ObjectDeclaration getLhsObject ()
  {
    return lhsObj;
  }


  @Override
  public ObjectDeclaration getRhsObject ()
  {
    return rhsObj;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLinkUnlinkStatement(this, p);
  }


}
