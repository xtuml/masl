//
// UK Crown Copyright (c) 2012. All Rights Reserved.
//
package org.xtuml.masl.metamodel;


public interface ASTNode
{

  <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception;
}
