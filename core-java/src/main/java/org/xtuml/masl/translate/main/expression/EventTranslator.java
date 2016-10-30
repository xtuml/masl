/*
 * Filename : EventTranslator.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.expression.EventExpression;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.object.ObjectTranslator;


/**
 * The current implementation of SM_TIMER, requires the MASL language to be
 * modified to support event types as parameters. With the new architectures
 * inbuilt support for timers, event types as parameters is no longer needed and
 * will be deprecated in the furture. To remain compatible with legacy MASL this
 * translator has been maintained to handle the SM_TIMER interface.
 * 
 */
public class EventTranslator extends ExpressionTranslator
{

  private final Scope                                               scope;
  private final ObjectTranslator                                    objectTran;
  private final org.xtuml.masl.translate.main.object.EventTranslator eventTrans;

  public EventTranslator ( final EventExpression expression, final Scope scope )
  {
    this.scope = scope;
    objectTran = ObjectTranslator.getInstance(expression.getEvent().getParentObject());
    eventTrans = objectTran.getEventTranslator(expression.getEvent());
  }

  /**
   * The generation of the code required by this translator is dependent on
   * other parameters that have been passed to the orginal SM_TIMER_Set_Timer
   * domain service invocation. These details cannot be passed to the
   * constructor of this class, so they are deffered until the
   * ArgumentTranslator class detects that an EventTranslator object has been
   * detected. WHen this happens it assumes that the service being invoked is
   * SM_TIMER_Set_Timer and sets the timerId and instance parameter on this
   * object.
   * 
   */
  public void setParameters ( final ParameterDefinition instanceDef,
                              final org.xtuml.masl.metamodel.expression.Expression instanceArg,
                              final ParameterDefinition timerIdDef,
                              final org.xtuml.masl.metamodel.expression.Expression timerIdArg )
  {
    final BasicType paramType = timerIdDef.getType();
    final ExpressionTranslator timerIdTranslator = ExpressionTranslator.createTranslator(timerIdArg, scope, paramType);
    final org.xtuml.masl.cppgen.Expression timerIdReadExpr = timerIdTranslator.getReadExpression();

    final ExpressionTranslator instanceTranslator = ExpressionTranslator.createTranslator(instanceArg, scope, paramType);
    org.xtuml.masl.cppgen.Expression instanceReadExpr = instanceTranslator.getReadExpression();

    if ( !isANormalEvent() )
    {
      // The event that has been passed to the SM_TIMER_set_Timer call is a
      // creation
      // or assigner event. These events are not associated with any particular
      // instance
      // and are therefore implemented on the Population class of the associated
      // object.
      // Therefore modify the generated code to invoke the required Population
      // method.
      // i.e
      // maslo_Timer_Object_TwoPopulation::getSingleton().create_delayed_maslo_Timer_Object_Two_maslev_creation_event_2(
      // maslv_inst1->get_masla_timer_2() )
      instanceReadExpr = objectTran.getPopulationClass().callStaticFunction("getSingleton");
      final Function createDelayFunction = eventTrans.getCreateFunction();
      setReadExpression(createDelayFunction.asFunctionCall(instanceReadExpr, false, timerIdReadExpr));

    }
    else
    {
      final Function createDelayFunction = eventTrans.getCreateFunction();
      setReadExpression(createDelayFunction.asFunctionCall(instanceReadExpr, true, timerIdReadExpr));

    }
  }

  public boolean isANormalEvent ()
  {
    return eventTrans.getEvent().getType() == EventDeclaration.Type.NORMAL;
  }
}
