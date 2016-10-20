//
// File: FunctionCall.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.util.Set;



public class FunctionPointer extends Expression
{

  private final Function function;


  /**
   * Creates a function pointer expression from a given function. The function
   * can be either a free function or a member function.
   * 

   *          the function to create a pointer to
   */
  public FunctionPointer ( final Function function )
  {
    this.function = function;
  }


  @Override
  String getCode ( final Namespace currentNamespace, final String alignment )
  {
    if ( function.isMember() )
    {
      return "&" + function.getDeclaration().getParentClass().getQualifiedName(currentNamespace) + "::" + function.getName();
    }
    else
    {
      return "&" + function.getQualifiedName(currentNamespace);
    }
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();
    result.addAll(function.getCallIncludes());
    return result;
  }

  @Override
  int getPrecedence ()
  {
    return 3;
  }


}
