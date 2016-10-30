//
// File: AttributeDeclaration.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.object;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.type.BasicType;


public interface AttributeDeclaration
    extends ASTNode
{

  ObjectDeclaration getParentObject ();

  Expression getDefault ();

  PragmaList getPragmas ();

  String getName ();

  BasicType getType ();

  boolean isPreferredIdentifier ();

  boolean isIdentifier ();

  boolean isUnique ();

  boolean isReferential ();

  List<? extends ReferentialAttributeDefinition> getRefAttDefs ();

}
