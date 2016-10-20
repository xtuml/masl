/*
 * Filename : TimingMonitor.java
 * 
 * UK Crown Copyright (c) 2007. All Rights Reserved
 */
package org.xtuml.masl.translate.modeltimings;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.translate.main.Architecture;


public class TimingMonitor
{

  public final static Namespace NAMESPACE    = new Namespace("SWA");

  public final static CodeFile  monitorInc   = Architecture.library.createInterfaceHeader("swa/TimingMonitor.hh");
  public final static Class     monitorClass = new Class("TimingMonitor", NAMESPACE, monitorInc);


  public final static Statement getScopedTimingBlock ( final String methodName )
  {
    return new Variable(new TypeUsage(monitorClass), "timingBlockMarker", new Expression[]
      { Literal.createStringLiteral(methodName) }).asStatement();
  }

  public final static Statement getBeginTimingBlock ( final String identifier )
  {
    return new ExpressionStatement(monitorClass.callStaticFunction("beginTimingBlock", new Literal(identifier)));
  }

  public final static Statement getEndTimingBlock ( final String identifier )
  {
    return new ExpressionStatement(monitorClass.callStaticFunction("endTimingBlock", new Literal(identifier)));
  }
}
