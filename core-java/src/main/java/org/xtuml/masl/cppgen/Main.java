//
// File: Main.java
//
// UK Crown Copyright (c) 2010. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

public class Main extends Function
{

  private final Expression argc;
  private final Expression argv;

  public Main ()
  {
    super("main");
    setReturnType(new TypeUsage(FundamentalType.INT));

    argc = createParameter(new TypeUsage(FundamentalType.INT), "argc").asExpression();
    argv = createParameter(new TypeUsage(false, FundamentalType.CHAR, new boolean[]
      { true, true }, false), "argv").asExpression();

  }

  public Expression getArgc ()
  {
    return argc;
  }

  public Expression getArgv ()
  {
    return argv;
  }


}
