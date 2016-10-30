//
// File: FromToRange.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.MinMaxRange;


public class FromToRange extends LoopSpec
    implements org.xtuml.masl.metamodel.code.LoopSpec.FromToRange
{

  
  public FromToRange ( final String loopVariable, final boolean reverse, final MinMaxRange range )
  {
    super(loopVariable, reverse, range.getType().getContainedType());
    this.range = range;
  }

  
  @Override
  public Expression getFrom ()
  {
    return range.getMin();
  }

  
  @Override
  public Expression getTo ()
  {
    return range.getMax();
  }

  @Override
  public String toString ()
  {
    return super.toString() + " " + range;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLoopFromToRange(this, p);
  }

  private final MinMaxRange range;

}
