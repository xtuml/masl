//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.def;

import java.util.Set;

import org.xtuml.masl.javagen.ast.ASTNode;


public interface Modifiers
    extends ASTNode
{

  Set<? extends Modifier> getModifiers ();

  boolean isAbstract ();

  boolean isFinal ();

  boolean isNative ();

  boolean isPrivate ();

  boolean isProtected ();

  boolean isPublic ();

  boolean isStatic ();

  boolean isStrictFp ();

  boolean isSynchronized ();

  boolean isTransient ();

  boolean isVolatile ();

}
