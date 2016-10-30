//
// UK Crown Copyright (c) 2012. All Rights Reserved.
//
package org.xtuml.masl.metamodel.code;

import org.xtuml.masl.metamodel.ASTNodeVisitor;


public class StatementTraverser<P>
{

  private final ASTNodeVisitor<Void, P> visitor;

  public StatementTraverser ( final ASTNodeVisitor<Void, P> visitor )
  {
    this.visitor = visitor;

  }

  public void traverse ( final Statement statement, final P p ) throws Exception
  {
    visitor.visit(statement, p);
    for ( final Statement child : statement.getChildStatements() )
    {
      if ( child != null )
      {
        traverse(child, p);
      }
    }
  }

  public void traverseDepthFirst ( final Statement statement, final P p ) throws Exception
  {
    for ( final Statement child : statement.getChildStatements() )
    {
      if ( child != null )
      {
        traverse(child, p);
      }
    }
    visitor.visit(statement, p);
  }


}
