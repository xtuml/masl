//
// File: AggregateInitialiser.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.utils.TextUtils;


/**
 * Represents a C++ Agregate Initialiser. An aggregate initialiser is an
 * expression that can be used to initialise a C++ array or structure. For
 * example the expression after the {@code =}in the following,
 * <code>int a[] = {1, 2, 3, 4, 5};</code> or
 * <code>{@literal std::pair<int,std::string>} b = {1, "Fred Bloggs"};</code>
 * They can be nested to initialise arrays of structures etc.
 */
public class AggregateInitialiser extends Expression
{

  /**
   * Creates an aggregate initialiser from a list of expressions. The
   * expressions will be used in the order supplied in the resulting
   * initialiser.
   * 

   *          the initialisers to use
   */
  public AggregateInitialiser ( final List<? extends Expression> initialisers )
  {
    this.initialisers = initialisers;
  }

  @Override
  int getPrecedence ()
  {
    return 0;
  }


  @Override
  String getCode ( final Namespace currentNamespace, final String alignment )
  {
    final List<String> initCode = new ArrayList<String>();
    for ( final Expression initialiser : initialisers )
    {
      String code = initialiser.getCode(currentNamespace, alignment + "\t");

      // The initialisers will be separated by commas, so need to parenthesise
      // them if their precedence is the same or lower than that of the comma
      // operator.
      if ( initialiser.getPrecedence() >= BinaryOperator.COMMA.getPrecedence() )
      {
        code = "(" + code + ")";
      }

      initCode.add(code);
    }

    return "{" + TextUtils.formatList(initCode, "", "\t", "", ",\n" + alignment, "") + "}";

  }

  /**
   * A list of initialisers
   */
  private final List<? extends Expression> initialisers;

}
