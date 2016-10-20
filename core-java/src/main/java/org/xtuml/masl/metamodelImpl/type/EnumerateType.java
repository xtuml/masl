//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.type;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.CheckedLookup;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.name.Named;


public class EnumerateType extends FullTypeDefinition
implements org.xtuml.masl.metamodel.type.EnumerateType
{

  private final CheckedLookup<EnumerateItem> items;

  public static EnumerateType create ( final Position position, final List<EnumerateItem> items )
  {
    if ( items == null )
    {
      return null;
    }
    try
    {
      return new EnumerateType(position, items);
    }
    catch ( final SemanticError e )
    {
      e.report();
      return null;
    }
  }

  public EnumerateType ( final Position position )
  {
    super(position);
    this.items = new CheckedLookup<EnumerateItem>(SemanticErrorCode.EnumerateAlreadyDefinedOnEnumeration,
        SemanticErrorCode.EnumerateNotFoundOnEnumeration,
        new Named()
    {

      @Override
      public String getName ()
      {
        return getUserDefinedType().getName();
      }
    });
  }

  public EnumerateType ( final Position position, final List<EnumerateItem> items ) throws SemanticError
  {
    this(position);
    for ( final EnumerateItem item : items )
    {
      addItem(item);
    }
  }

  public void addItem ( final EnumerateItem item ) throws SemanticError
  {
    if ( item != null )
    {
      item.setEnumerate(this);
      item.setIndex(items.size());
      this.items.put(item.getName(), item);
    }

  }

  @Override
  public void setTypeDeclaration ( final TypeDeclaration typeDeclaration )
  {
    super.setTypeDeclaration(typeDeclaration);
    typeDeclaration.getDomain().addEnumerateItems(this);
  }


  @Override
  public List<EnumerateItem> getItems ()
  {
    return Collections.unmodifiableList(items.asList());
  }

  @Override
  public String toString ()
  {
    return "enum (" + org.xtuml.masl.utils.TextUtils.formatList(items.asList(), "", ", ", "") + ")";
  }

  @Override
  public UserDefinedType getUserDefinedType ()
  {
    return getTypeDeclaration().getDeclaredType();
  }

  public EnumerateItem getItem ( final String name ) throws NotFound
  {
    return items.get(name);
  }

  @Override
  public Expression getMinValue ()
  {
    final EnumerateItem first = items.asList().get(0);
    return first.getReference(first.getPosition());
  }

  @Override
  public Expression getMaxValue ()
  {
    final EnumerateItem last = items.asList().get(items.asList().size() - 1);
    return last.getReference(last.getPosition());
  }

  public int getNoItems ()
  {
    return items.asList().size();
  }

  @Override
  public boolean equals ( final Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj.getClass() != EnumerateType.class )
    {
      return false;
    }
    else
    {
      final EnumerateType rhs = (EnumerateType)obj;
      return items.equals(rhs.items);
    }
  }

  @Override
  public int hashCode ()
  {

    return items.hashCode();
  }

  @Override
  public UserDefinedType getPrimitiveType ()
  {
    return getUserDefinedType();
  }

  @Override
  public ActualType getActualType ()
  {
    return ActualType.ENUMERATE;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitEnumerateType(this, p);
  }


}
