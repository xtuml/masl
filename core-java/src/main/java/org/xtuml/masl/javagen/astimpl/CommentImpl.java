//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Comment;


public class CommentImpl extends TypeMemberImpl
    implements Comment
{

  CommentImpl ( final ASTImpl ast, final String text )
  {
    super(ast);
    this.text = text;
  }

  @Override
  public String getText ()
  {
    return text;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitComment(this, p);
  }

  private final String text;
}
