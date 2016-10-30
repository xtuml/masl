//
// File: State.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodel.statemodel;

import java.util.List;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.code.CodeBlock;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.PragmaList;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;


public interface State
    extends ASTNode
{

  enum Type
  {
    NORMAL, CREATION, TERMINAL, ASSIGNER, ASSIGNER_START
  }

  String getName ();

  ObjectDeclaration getParentObject ();


  CodeBlock getCode ();

  PragmaList getDeclarationPragmas ();

  PragmaList getDefinitionPragmas ();

  List<? extends ParameterDefinition> getParameters ();

  List<? extends VariableDefinition> getLocalVariables ();

  Type getType ();

  String getQualifiedName ();

  String getFileName ();

  String getFileHash ();
}
