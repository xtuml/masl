//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.expr.ArrayInitializer;
import org.xtuml.masl.javagen.ast.expr.Expression;


class ArrayInitializerImpl extends ExpressionImpl
    implements ArrayInitializer
{

  ArrayInitializerImpl ( final ASTImpl ast, final Expression... elements )
  {
    super(ast);
    for ( final Expression element : elements )
    {
      addElement(element);
    }
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitArrayInitializer(this, p);
  }

  @Override
  public ExpressionImpl addElement ( final Expression element )
  {
    elements.add((ExpressionImpl)element);
    return (ExpressionImpl)element;
  }

  @Override
  public List<? extends Expression> getElements ()
  {
    return Collections.unmodifiableList(elements);
  }

  @Override
  protected int getPrecedence ()
  {
    // Should never be used anywhere other then where it is syntactically
    // unambiguous, so should never need parenthesizing
    return Integer.MAX_VALUE;
  }

  private final ChildNodeList<ExpressionImpl> elements = new ChildNodeList<ExpressionImpl>(this);

}
