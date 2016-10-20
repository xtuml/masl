//
// File: ObjectServiceTranslator.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.main.object;

import java.util.ArrayList;
import java.util.List;

import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.ParameterTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.code.CodeTranslator;


public class StateActionTranslator
{

  StateActionTranslator ( final ObjectTranslator objectTranslator, final State state, final Expression stateId )
  {
    this.state = state;
    this.stateId = stateId;
    this.objectTranslator = objectTranslator;
    this.domainTranslator = objectTranslator.getDomainTranslator();

    function = objectTranslator.getMain().addStateFunction(state);

    scope = new Scope(objectTranslator);
    scope.setState(state);

    for ( final ParameterDefinition param : state.getParameters() )
    {
      final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
      parameters.add(paramTrans);
      scope.addParameter(param, paramTrans.getVariable().asExpression());
    }
  }

  public List<ParameterTranslator> getParameters ()
  {
    return parameters;
  }


  private final Expression stateId;

  public Expression getStateId ()
  {
    return stateId;
  }

  void translate ()
  {
    CodeFile file;
    if ( state.getCode() != null || state.getDeclarationPragmas().hasPragma("generated_code") )
    {
      file = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(state));
    }
    else
    {
      file = domainTranslator.getNativeStubs();
    }

    file.addFunctionDefinition(function);


    if ( state.getCode() != null )
    {
      codeTranslator = CodeTranslator.createTranslator(state.getCode(), scope);

      function.getCode().appendStatement(codeTranslator.getFullCode());
    }
  }

  private final List<ParameterTranslator> parameters = new ArrayList<ParameterTranslator>();


  public CodeTranslator getCodeTranslator ()
  {
    return codeTranslator;
  }


  public Function getFunction ()
  {
    return function;
  }

  Scope getScope ()
  {
    return scope;
  }

  public State getState ()
  {
    return state;
  }

  private final Function         function;

  private final Scope            scope;

  private final State            state;

  private CodeTranslator         codeTranslator = null;

  private final ObjectTranslator objectTranslator;
  private final DomainTranslator domainTranslator;


  public ObjectTranslator getObjectTranslator ()
  {
    return objectTranslator;
  }

  public static StateActionTranslator getInstance ( final State state )
  {
    return ObjectTranslator.getInstance(state.getParentObject()).getStateActionTranslator(state);
  }

}
