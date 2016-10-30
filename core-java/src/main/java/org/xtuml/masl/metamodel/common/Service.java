//
// File: Service.java
//
// UK Crown Copyright (c) 2009. All Rights Reserved.
//
package org.xtuml.masl.metamodel.common;

import java.util.List;

import org.xtuml.masl.metamodel.code.CodeBlock;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.exception.ExceptionReference;
import org.xtuml.masl.metamodel.type.BasicType;


public interface Service
{

  String getName ();

  PragmaList getDeclarationPragmas ();

  PragmaList getDefinitionPragmas ();

  List<? extends ExceptionReference> getExceptionSpecs ();

  List<? extends ParameterDefinition> getParameters ();

  List<? extends VariableDefinition> getLocalVariables ();

  BasicType getReturnType ();

  Visibility getVisibility ();

  boolean isFunction ();

  int getOverloadNo ();

  CodeBlock getCode ();

  String getQualifiedName ();

  String getFileName ();

  String getFileHash ();

}
