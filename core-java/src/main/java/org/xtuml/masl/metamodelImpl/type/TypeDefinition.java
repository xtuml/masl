//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public abstract class TypeDefinition extends Positioned
    implements org.xtuml.masl.metamodel.type.TypeDefinition
{

  TypeDefinition ( final Position position )
  {
    super(position);
  }

  @Override
  public Expression getMinValue ()
  {
    return null;
  }

  @Override
  public Expression getMaxValue ()
  {
    return null;
  }

  @Override
  public TypeDefinition getDefinedType ()
  {
    return this;
  }

  private TypeDeclaration typeDeclaration = null;

  @Override
  public TypeDeclaration getTypeDeclaration ()
  {
    return typeDeclaration;
  }

  public void setTypeDeclaration ( final TypeDeclaration typeDeclaration )
  {
    this.typeDeclaration = typeDeclaration;
  }

  // Force typedefs to implement equals and hashCode
  @Override
  abstract public boolean equals ( Object obj );

  @Override
  abstract public int hashCode ();

  abstract public BasicType getPrimitiveType ();

  @Override
  public boolean isNumeric ()
  {
    return false;
  }

  @Override
  public boolean isCollection ()
  {
    return false;
  }

  @Override
  public boolean isString ()
  {
    return false;
  }

  @Override
  public boolean isCharacter ()
  {
    return false;
  }

  public void checkCanBePublic ()
  {
  }

}
