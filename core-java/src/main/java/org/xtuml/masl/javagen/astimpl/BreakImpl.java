//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.code.LabeledStatement;


public class BreakImpl extends StatementImpl
    implements org.xtuml.masl.javagen.ast.code.Break
{

  public BreakImpl ( final ASTImpl ast )
  {
    super(ast);
  }

  @Override
  public LabeledStatement getReferencedLabel ()
  {
    return referencedLabel;
  }

  @Override
  public void setReferencedLabel ( final LabeledStatement label )
  {
    this.referencedLabel = label;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitBreak(this, p);
  }

  // Not a child node, just a reference to a node in another tree.
  private LabeledStatement referencedLabel = null;

}
