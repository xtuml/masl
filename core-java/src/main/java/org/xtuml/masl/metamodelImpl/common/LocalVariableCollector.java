//
// File: LocalVariableCollector.java
//
// UK Crown Copyright (c) 2010. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.common;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.metamodel.DoNothingASTNodeVisitor;
import org.xtuml.masl.metamodel.code.CodeBlock;
import org.xtuml.masl.metamodel.code.ForStatement;
import org.xtuml.masl.metamodel.code.Statement;
import org.xtuml.masl.metamodel.code.StatementTraverser;
import org.xtuml.masl.metamodel.code.VariableDefinition;


public class LocalVariableCollector
    extends StatementTraverser<List<VariableDefinition>>
{

  public LocalVariableCollector ( final Statement statement )
  {
    super(new DoNothingASTNodeVisitor<Void, List<VariableDefinition>>()
    {

      @Override
      public final Void visitCodeBlock ( final CodeBlock statement, final List<VariableDefinition> variables ) throws Exception
      {
        variables.addAll(statement.getVariables());
        return null;
      }

      @Override
      public Void visitForStatement ( final ForStatement statement, final List<VariableDefinition> variables ) throws Exception
      {
        variables.add(statement.getLoopSpec().getLoopVariableDef());
        return null;
      }
    });

    try
    {
      traverse(statement, variables);
    }
    catch ( final Exception e )
    {
      e.printStackTrace();
    }
  }

  public List<VariableDefinition> getLocalVariables ()
  {
    return variables;
  }

  private final List<VariableDefinition> variables = new ArrayList<VariableDefinition>();

}
