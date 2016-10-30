//
// File: InternalType.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;


public class InternalType extends BasicType
{

  public final static InternalType OBJECT          = new InternalType("object");
  public final static InternalType SERVICE         = new InternalType("service");
  public final static InternalType TYPE            = new InternalType("type");
  public final static InternalType AMBIGUOUS_ENUM  = new InternalType("enum");
  public final static InternalType STREAM_MODIFIER = new InternalType("stream_modifier");
  public static final InternalType TERMINATOR      = new InternalType("terminator");
  public static final InternalType SPLIT           = new InternalType("split");
  public static final InternalType CHARACTERISTIC  = new InternalType("characteristic");

  private InternalType ( final String name )
  {
    super(null, true);
    this.name = name;
  }

  private final String name;

  @Override
  public String toString ()
  {
    return name;
  }


  @Override
  public boolean equals ( final Object obj )
  {
    return this == obj;
  }

  @Override
  public int hashCode ()
  {
    return name.hashCode();
  }

  @Override
  public InternalType getBasicType ()
  {
    return this;
  }

  @Override
  public InternalType getPrimitiveType ()
  {
    return this;
  }


  @Override
  public ActualType getActualType ()
  {
    return null;
  }


  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    throw new IllegalStateException("Cannot Visit Internal Type");
  }

}
