//
// File: ExpressionStatement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;



public class VariableDefinitionStatement extends Statement
{

  private final Variable variable;

  public VariableDefinitionStatement ( final Variable variable )
  {
    this.variable = variable;
  }

  @Override
  /**
   * 


   * @throws IOException
   * @see org.xtuml.masl.cppgen.Statement#write(java.io.Writer, java.lang.String)
   */
  void write ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
  {
    ((Variable.VariableDefinition)variable.getDefinition()).writeCodeDefinition(writer, indent, currentNamespace);
  }


  @Override
  Set<Declaration> getForwardDeclarations ()
  {
    final Set<Declaration> result = super.getForwardDeclarations();
    result.addAll(variable.getDefinition().getForwardDeclarations());
    return result;
  }

  @Override
  Set<CodeFile> getIncludes ()
  {
    final Set<CodeFile> result = super.getIncludes();
    result.addAll(variable.getDefinition().getIncludes());
    return result;
  }


}
