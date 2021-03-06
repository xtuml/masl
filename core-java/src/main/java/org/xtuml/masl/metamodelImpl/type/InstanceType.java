//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;


public class InstanceType extends BasicType
    implements org.xtuml.masl.metamodel.type.InstanceType
{

  private final ObjectDeclaration object;

  public static InstanceType create ( final Position position, final ObjectNameExpression object, final boolean anonymous )
  {
    if ( object == null )
    {
      return null;
    }

    return new InstanceType(position, object.getObject(), anonymous);
  }

  public static InstanceType createAnonymous ( final ObjectDeclaration object )
  {
    return new InstanceType(null, object, true);
  }

  private InstanceType ( final Position position, final ObjectDeclaration object, final boolean anonymous )
  {
    super(position, anonymous);
    this.object = object;
  }

  @Override
  public String toString ()
  {
    return (isAnonymousType() ? "anonymous " : "") + "instance of " + object.getName();
  }

  @Override
  public ObjectDeclaration getObjectDeclaration ()
  {
    return object;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof InstanceType) )
    {
      return false;
    }
    else
    {
      final InstanceType rhs = (InstanceType)obj;

      return object.equals(rhs.object);
    }
  }

  @Override
  protected boolean isAssignableFromRelaxation ( final BasicType rhs )
  {
    // Special case for assignment from anonymous anyinstance - this would be
    // the type of the null literal.
    return rhs instanceof AnyInstanceType && rhs.isAnonymousType();
  }

  @Override
  protected boolean isConvertibleFromRelaxation ( final BasicType rhs )
  {
    // Special case for assignment from anonymous anyinstance - this would be
    // the type of the null literal.
    return rhs instanceof AnyInstanceType && rhs.isAnonymousType();
  }


  @Override
  public int hashCode ()
  {
    return object.hashCode();
  }

  @Override
  public InstanceType getBasicType ()
  {
    return this;
  }

  @Override
  public InstanceType getPrimitiveType ()
  {
    return this;
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.INSTANCE;
  }

  @Override
  public void checkCanBePublic ()
  {
    new SemanticError(SemanticErrorCode.PrivateTypeCannotBeUsedPublicly, getPosition(), toString()).report();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitInstanceType(this, p);
  }

}
