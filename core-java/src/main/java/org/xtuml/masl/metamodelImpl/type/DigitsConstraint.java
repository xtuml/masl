//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.expression.Expression;


public class DigitsConstraint extends TypeConstraint
    implements org.xtuml.masl.metamodel.type.DigitsConstraint
{

  private final Expression digits;

  public DigitsConstraint ( final Expression digits, final RangeConstraint range )
  {
    super(range.getRange());
    this.digits = digits;
  }

  @Override
  public Expression getDigits ()
  {
    return digits;
  }

  @Override
  public String toString ()
  {
    return "digits " + digits + " range " + range;
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( !(obj instanceof DigitsConstraint) )
    {
      return false;
    }
    else
    {
      final DigitsConstraint rhs = (DigitsConstraint)obj;

      return super.equals(rhs) && digits.equals(rhs.digits);
    }
  }

  @Override
  public int hashCode ()
  {

    return super.hashCode() * 31 + digits.hashCode();
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitDigitsConstraint(this, p);
  }

}
