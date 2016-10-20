//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;


public class ChildNode<C extends ASTNodeImpl>
{

  ChildNode ( final ASTNodeImpl parent )
  {
    this.parent = parent;
  }

  void set ( final C childNode )
  {
    if ( this.childNode != null )
    {
      parent.removeChildNode(this.childNode);
    }
    if ( childNode != null )
    {
      parent.addChildNode(childNode);
    }
    this.childNode = childNode;
  }

  void clear ()
  {
    set(null);
  }

  C get ()
  {
    return childNode;
  }

  private final ASTNodeImpl parent;
  private C                 childNode = null;
}
