//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public class RangeConstraint extends TypeConstraint
    implements org.xtuml.masl.metamodel.type.RangeConstraint
{

  public RangeConstraint ( final Expression range )
  {
    super(range);
  }

  @Override
  public String toString ()
  {
    return "range " + range;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitRangeConstraint(this, p);
  }

}
