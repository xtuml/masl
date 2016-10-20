/*
 * Filename : FunctionObjectCall.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.cppgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.xtuml.masl.utils.TextUtils;


public class FunctionObjectCall extends Expression
{

  private final Expression       funObj;
  private final List<Expression> arguments;

  public FunctionObjectCall ( final Expression funObj, final List<Expression> arguments )
  {
    this.funObj = funObj;
    this.arguments = arguments;
  }

  public FunctionObjectCall ( final Expression funObj, final Expression... expressions )
  {
    this.funObj = funObj;
    this.arguments = Arrays.<Expression>asList(expressions);
  }

  @Override
  String getCode ( final Namespace currentNamespace, final String alignment )
  {
    final StringBuffer buf = new StringBuffer();
    buf.append(funObj.getCode(currentNamespace));
    final List<String> argCode = new ArrayList<String>();
    for ( final Expression arg : arguments )
    {
      String code = arg.getCode(currentNamespace);
      // Need to parenthesise any comma operator to ensure correct parsing
      if ( arg.getPrecedence() >= BinaryOperator.COMMA.getPrecedence() )
      {
        code = "(" + code + ")";
      }
      argCode.add(code);
    }
    buf.append("(" + TextUtils.formatList(argCode, " ", ", ", " ") + ")");
    return buf.toString();
  }

  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = super.getForwardDeclarations();
    for ( final Expression arg : arguments )
    {
      result.addAll(arg.getForwardDeclarations());
    }
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();

    result.addAll(funObj.getIncludes());
    for ( final Expression arg : arguments )
    {
      result.addAll(arg.getIncludes());
    }
    return result;
  }

  @Override
  int getPrecedence ()
  {
    return 0;
  }

}
