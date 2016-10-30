//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast;

import java.util.Collection;


public interface ASTNode
{

  <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception;

  AST getAST ();

  Collection<? extends ASTNode> getChildNodes ();

  ASTNode getParentNode ();

}
