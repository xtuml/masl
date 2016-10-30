//
// File: TypeRange.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.type.BasicType;


public class TypeRange extends LoopSpec
    implements org.xtuml.masl.metamodel.code.LoopSpec.TypeRange
{

  
  public TypeRange ( final String loopVariable, final boolean reverse, final BasicType type )
  {
    super(loopVariable, reverse, type);
    this.type = type;
  }

  
  @Override
  public BasicType getType ()
  {
    return this.type;
  }


  @Override
  public String toString ()
  {
    return super.toString() + " " + type + "'elements";
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitLoopTypeRange(this, p);
  }


  private final BasicType type;


}
