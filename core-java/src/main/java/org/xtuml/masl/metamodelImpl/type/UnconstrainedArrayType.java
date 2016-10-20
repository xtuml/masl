//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;


public class UnconstrainedArrayType extends FullTypeDefinition
    implements org.xtuml.masl.metamodel.type.UnconstrainedArrayType
{

  private final BasicType containedType;
  private final BasicType indexType;

  public UnconstrainedArrayType ( final Position position, final BasicType containedType, final BasicType indexType )
  {
    super(position);
    this.containedType = containedType;
    this.indexType = indexType;
    primitive = SequenceType.createAnonymous(containedType);
  }

  @Override
  public BasicType getContainedType ()
  {
    return containedType;
  }

  @Override
  public BasicType getIndexType ()
  {
    return indexType;
  }

  @Override
  public String toString ()
  {
    return "array (" + indexType + " range<>) of " + containedType;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof UnconstrainedArrayType) )
    {
      return false;
    }
    else
    {
      final UnconstrainedArrayType rhs = (UnconstrainedArrayType)obj;

      return containedType.equals(rhs.containedType) && indexType.equals(rhs.indexType);
    }
  }

  @Override
  public int hashCode ()
  {

    return containedType.hashCode() * 31 + indexType.hashCode();
  }

  @Override
  public SequenceType getPrimitiveType ()
  {
    return primitive;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.UNCONSTRAINED_ARRAY;
  }

  private final SequenceType primitive;

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitUnconstrainedArrayType(this, p);
  }

}
