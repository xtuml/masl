//
// File: FindExpression.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodelImpl.type.BagType;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.CollectionType;
import org.xtuml.masl.metamodelImpl.type.SetType;
import org.xtuml.masl.utils.HashCode;



public class NavigationExpression extends Expression
    implements org.xtuml.masl.metamodel.expression.NavigationExpression
{

  public static NavigationExpression create ( final Position position,
                                              final Expression lhs,
                                              final RelationshipSpecification.Reference relRef,
                                              final Expression whereClause )
  {
    if ( lhs == null || relRef == null )
    {
      return null;
    }

    return new NavigationExpression(position, lhs, relRef.getRelationshipSpec(), whereClause);
  }


  private final Expression                lhs;
  private final RelationshipSpecification relationship;
  private final Expression                condition;

  
  private NavigationExpression ( final Position position,
                                 final Expression lhs,
                                 final RelationshipSpecification relationship,
                                 final Expression whereClause )
  {
    super(position);
    this.lhs = lhs;
    this.relationship = relationship;
    this.condition = whereClause;
  }


  
  public Expression getCondition ()
  {
    return this.condition;
  }

  @Override
  public Expression getSkeleton ()
  {
    if ( condition != null )
    {
      return condition.getFindSkeleton();
    }
    else
    {
      return null;
    }
  }

  @Override
  public List<Expression> getArguments ()
  {
    return Collections.unmodifiableList(condition.getFindArguments());
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

  /**
   * 
   * @return
   * @see org.xtuml.masl.metamodelImpl.expression.Expression#getType()
   */
  @Override
  public BasicType getType ()
  {
    if ( lhs.getType().getBasicType() instanceof SetType && relationship.getReverseSpec().getCardinality() == MultiplicityType.ONE )
    {
      // If back relationship is single valued, then we can continue to
      // guarantee uniqueness
      return SetType.createAnonymous(relationship.getDestinationObject().getType());
    }
    else if ( lhs.getType().getBasicType() instanceof CollectionType )
    {
      return BagType.createAnonymous(relationship.getDestinationObject().getType());
    }
    else if ( relationship.getCardinality() == MultiplicityType.MANY )
    {
      // Single instance -> many, so can guarantee uniqueness
      return SetType.createAnonymous(relationship.getDestinationObject().getType());
    }
    else
    {
      // single instance -> one
      return relationship.getDestinationObject().getType();
    }
  }


  @Override
  public String toString ()
  {
    return lhs + " -> " + relationship;

  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( getClass() != obj.getClass() )
    {
      return false;
    }
    final NavigationExpression other = (NavigationExpression)obj;
    if ( condition == null )
    {
      if ( other.condition != null )
      {
        return false;
      }
    }
    else if ( !condition.equals(other.condition) )
    {
      return false;
    }
    if ( lhs == null )
    {
      if ( other.lhs != null )
      {
        return false;
      }
    }
    else if ( !lhs.equals(other.lhs) )
    {
      return false;
    }
    if ( relationship == null )
    {
      if ( other.relationship != null )
      {
        return false;
      }
    }
    else if ( !relationship.equals(other.relationship) )
    {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode ()
  {
    return HashCode.makeHash(lhs, relationship, condition);
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitNavigationExpression(this, p);
  }

}
