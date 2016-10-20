//
// File: Expression.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.expression;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;


public interface Expression
    extends ASTNode
{

  BasicType getType ();

  List<? extends FindParameterExpression> getFindParameters ();

  /**
   * Returns a list of all the attributes that are checked for equality with a
   * find parameter in the given expression. They must all be linked with 'and'.
   * If there are attributes which are checked for anything other than equality
   * or if they are not linked with 'and', then null is returned. This function
   * is called recursively on subexpressions until the bottom of the tree is
   * reached, or a null is returned by a subexpression.
   * 
   * This function is used to check whether a find expression is finding an
   * exact match on a unique identifier. If it is, then a number of
   * optimisations may be possible.
   * 
   * @return a set of attribute names, or null if this is not a compatible
   *         expression
   */
  List<? extends AttributeDeclaration> getFindEqualAttributes ();

  /**
   * 
   * @return For a complex expresion return the literal result.
   */
  public LiteralExpression evaluate ();

  List<? extends Expression> getChildExpressions ();

}
