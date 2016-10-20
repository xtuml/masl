//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.metamodel;

import java.util.Collection;


public abstract class AbstractASTNodeVisitor<R, P>
    implements ASTNodeVisitor<R, P>
{

  public final R visit ( final ASTNode node ) throws Exception
  {
    return visit(node, null);
  }

  @Override
  public final R visit ( final ASTNode node, final P p ) throws Exception
  {
    if ( node == null )
    {
      return visitNull(p);
    }
    else
    {
      return node.accept(this, p);
    }
  }

  public final R visit ( final Collection<? extends ASTNode> nodes ) throws Exception
  {
    return visit(nodes, null);
  }

  public final R visit ( final Collection<? extends ASTNode> nodes, final P p ) throws Exception
  {
    R r = null;
    for ( final ASTNode node : nodes )
    {
      r = visit(node);
    }
    return r;
  }

  @Override
  public R visitNull ( final P p ) throws Exception
  {
    throw new NullPointerException();
  }
}
