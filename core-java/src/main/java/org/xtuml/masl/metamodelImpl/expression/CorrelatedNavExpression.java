//
// File: FindExpression.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Arrays;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.type.BagType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.SetType;



public class CorrelatedNavExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.CorrelatedNavExpression
{

  public static CorrelatedNavExpression create ( final Position position,
                                                 final Expression lhs,
                                                 final Expression rhs,
                                                 final RelationshipSpecification.Reference relRef )
  {
    if ( lhs == null || rhs == null || relRef == null )
    {
      return null;
    }

    try
    {
      final ObjectDeclaration lhsObj = ObjectDeclaration.getObject(lhs, true);
      final ObjectDeclaration rhsObj = ObjectDeclaration.getObject(rhs, true);

      final RelationshipSpecification leftToAssoc = relRef.getRelationshipSpec();

      final RelationshipDeclaration rel = leftToAssoc.getRelationship();
      if ( !(rel instanceof AssociativeRelationshipDeclaration) )
      {
        throw new SemanticError(SemanticErrorCode.CorrelateNotAssociative, position, rel.getName());
      }

      final AssociativeRelationshipDeclaration assocRel = (AssociativeRelationshipDeclaration)rel;

      if ( (assocRel.getLeftObject() == lhsObj && assocRel.getRightObject() != rhsObj)
           || (assocRel.getRightObject() == rhsObj && assocRel.getLeftObject() != lhsObj) )
      {
        throw new SemanticError(SemanticErrorCode.CorrelateObjsIncorrect,
                                position,
                                lhsObj.getName(),
                                rhsObj.getName(),
                                assocRel.getName());
      }

      return new CorrelatedNavExpression(position, lhs, rhs, leftToAssoc.getNonAssocSpec());
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }


  private final Expression                lhs;
  private final Expression                rhs;
  private final RelationshipSpecification relationship;

  
  public CorrelatedNavExpression ( final Position position,
                                   final Expression lhs,
                                   final Expression rhs,
                                   final RelationshipSpecification relationship )
  {
    super(position);
    this.lhs = lhs;
    this.rhs = rhs;
    this.relationship = relationship;
  }

  
  @Override
  public Expression getLhs ()
  {
    return this.lhs;
  }

  
  @Override
  public Expression getRhs ()
  {
    return this.rhs;
  }

  
  @Override
  public RelationshipSpecification getRelationship ()
  {
    return this.relationship;
  }


  /**
   * 
   * @return
   * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
   */
  @Override
  public BasicType getType ()
  {
    if ( lhs.getType().getBasicType() instanceof InstanceType && rhs.getType().getBasicType() instanceof InstanceType )
    {
      return relationship.getAssocSpec().getDestinationObject().getType();
    }
    else if ( (lhs.getType().getBasicType() instanceof SetType || lhs.getType().getBasicType() instanceof InstanceType) && (rhs.getType()
                                                                                                                               .getBasicType() instanceof SetType || rhs.getType()
                                                                                                                                                                        .getBasicType() instanceof InstanceType) )
    {
      return SetType.createAnonymous(relationship.getAssocSpec().getDestinationObject().getType());
    }
    else
    {
      return BagType.createAnonymous(relationship.getAssocSpec().getDestinationObject().getType());
    }
  }

  @Override
  public String toString ()
  {
    return lhs + " with " + rhs + " -> " + relationship.getAssocSpec();

  }


  @Override
  public int hashCode ()
  {
    return lhs.hashCode() ^ rhs.hashCode() ^ relationship.hashCode();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj instanceof CorrelatedNavExpression )
    {

      final CorrelatedNavExpression obj2 = ((CorrelatedNavExpression)obj);
      return lhs.equals(obj2.lhs) && rhs.equals(obj2.rhs) && relationship.equals(obj2.relationship);
    }
    else
    {
      return false;
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitCorrelatedNavExpression(this, p);
  }

  @Override
  public List<Expression> getChildExpressions ()
  {
    return Arrays.<Expression>asList(lhs, rhs);
  }


}
