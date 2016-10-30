//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.NewArray;
import org.xtuml.masl.javagen.ast.types.Type;


class NewArrayImpl extends ExpressionImpl
    implements NewArray
{

  NewArrayImpl ( final ASTImpl ast,
                                       final Type type,
                                       final int noDimensons,
                                       final ArrayInitializer initialValue )
  {
    super(ast);
    setType(type);
    setNoDimensions(noDimensons);
    setInitialValue(initialValue);
  }

  NewArrayImpl ( final ASTImpl ast,
                                       final Type type,
                                       final int noDimensons,
                                       final Expression... dimensionSizes )
  {
    super(ast);
    setType(type);
    setNoDimensions(noDimensons);
    for ( final Expression dimSize : dimensionSizes )
    {
      addDimensionSize(dimSize);
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitNewArray(this, p);
  }

  @Override
  public ExpressionImpl addDimensionSize ( final Expression dimensionSize )
  {
    this.dimensionSizes.add((ExpressionImpl)dimensionSize);
    return (ExpressionImpl)dimensionSize;
  }

  @Override
  public List<? extends ExpressionImpl> getDimensionSizes ()
  {
    return Collections.unmodifiableList(dimensionSizes);
  }


  @Override
  public ArrayInitializerImpl getInitialValue ()
  {
    return initialValue.get();
  }

  @Override
  public int getNoDimensions ()
  {
    return noDimensions;
  }

  @Override
  public TypeImpl getType ()
  {
    return type.get();
  }

  @Override
  public ArrayInitializerImpl setInitialValue ( final ArrayInitializer initialValue )
  {
    this.initialValue.set((ArrayInitializerImpl)initialValue);
    return (ArrayInitializerImpl)initialValue;
  }

  @Override
  public void setNoDimensions ( final int noDimensions )
  {
    this.noDimensions = noDimensions;
  }


  @Override
  public TypeImpl setType ( final Type type )
  {
    this.type.set((TypeImpl)type);
    return (TypeImpl)type;
  }

  @Override
  protected int getPrecedence ()
  {
    return 13;
  }


  private final ChildNodeList<ExpressionImpl>   dimensionSizes = new ChildNodeList<ExpressionImpl>(this);

  private int                                   noDimensions;

  private final ChildNode<ArrayInitializerImpl> initialValue   = new ChildNode<ArrayInitializerImpl>(this);


  private final ChildNode<TypeImpl>             type           = new ChildNode<TypeImpl>(this);
}
