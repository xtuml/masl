//
// File: Stack.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.translate.stacktrack;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.translate.main.Architecture;


public class Stack
{

  private final static Namespace NAMESPACE                      = new Namespace("SWA");

  private final static CodeFile  stackInc                       = Architecture.library.createInterfaceHeader("swa/Stack.hh");
  private final static Class     stackClass                     = new Class("Stack", NAMESPACE, stackInc);

  private final static TypeUsage enteringDomainServiceClass     = new TypeUsage(new Class("EnteringDomainService",
                                                                                          stackClass
                                                                                                    .getNamespace(),
                                                                                          stackInc));

  private final static TypeUsage enteringObjectServiceClass     = new TypeUsage(new Class("EnteringObjectService",
                                                                                          stackClass
                                                                                                    .getNamespace(),
                                                                                          stackInc));

  private final static TypeUsage enteringTerminatorServiceClass = new TypeUsage(new Class("EnteringTerminatorService",
                                                                                          stackClass
                                                                                                    .getNamespace(),
                                                                                          stackInc));

  private final static TypeUsage enteringStateClass             = new TypeUsage(new Class("EnteringState",
                                                                                          stackClass.getNamespace(),
                                                                                          stackInc));

  private final static TypeUsage executingStatement             = new TypeUsage(new Class("ExecutingStatement",
                                                                                          stackClass.getNamespace(),
                                                                                          stackInc));

  private final static Class     declareThisClass               = new Class("DeclareThis", stackClass.getNamespace(), stackInc);

  private final static Class     declareParameterClass          = new Class("DeclareParameter", stackClass.getNamespace(), stackInc);

  private final static Class     declareLocalVarClass           = new Class("DeclareLocalVariable",
                                                                            stackClass.getNamespace(),
                                                                            stackInc);

  private final static Class     enteredActionClass             = new Class("EnteredAction", stackClass.getNamespace(), stackInc);

  private final static Statement enteredAction                  = new Variable(new TypeUsage(enteredActionClass),
                                                                               "enteredActionMarker")
                                                                                                     .asStatement();

  private final static TypeUsage enteredCatch                   = new TypeUsage(new Class("EnteredCatch",
                                                                                          stackClass.getNamespace(),
                                                                                          stackInc));


  public final static Statement defineThis ( final Variable variable )
  {
    return new Variable(new TypeUsage(declareThisClass), "thisVar", new Expression[]
      { variable.asExpression() }).asStatement();
  }

  public final static Statement defineParameter ( final Variable variable )
  {
    return new Variable(new TypeUsage(declareParameterClass), "pm_" + variable.getName(), new Expression[]
      { variable.asExpression() }).asStatement();
  }

  public final static Statement defineVariable ( final Expression id, final Variable variable )
  {
    return new Variable(new TypeUsage(declareLocalVarClass), "pm_" + variable.getName(), id, variable.asExpression()).asStatement();
  }

  public final static Statement getExecutingStatement ( final int minLine )
  {
    return new Variable(executingStatement, "statement", new Expression[]
      { new Literal(minLine) }).asStatement();
  }

  public final static Statement getEnteredCatch ( final int minLine )
  {
    return new Variable(enteredCatch, "catcher", new Expression[]
      { new Literal(minLine) }).asStatement();
  }

  public final static Statement getEnteredAction ()
  {
    return enteredAction;
  }

  public final static Statement getEnteringDomainService ( final Expression domainId, final Expression serviceId )
  {
    return new Variable(enteringDomainServiceClass, "enteringActionMarker", domainId, serviceId).asStatement();
  }

  public final static Statement getEnteringObjectService ( final Expression domainId,
                                                           final Expression objectId,
                                                           final Expression serviceId )
  {
    return new Variable(enteringObjectServiceClass, "enteringActionMarker", domainId, objectId, serviceId).asStatement();
  }

  public final static Statement getEnteringTerminatorService ( final Expression domainId,
                                                               final Expression termId,
                                                               final Expression serviceId )
  {
    return new Variable(enteringTerminatorServiceClass, "enteringActionMarker", domainId, termId, serviceId).asStatement();
  }

  public final static Statement getEnteringState ( final Expression domainId, final Expression objectId, final Expression stateId )
  {
    return new Variable(enteringStateClass, "enteringActionMarker", domainId, objectId, stateId).asStatement();
  }

}
