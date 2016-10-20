/*
 * File: Macro.java
 * 
 * UK Crown Copyright (c) 2006. All Rights Reserved.
 */
package org.xtuml.masl.cppgen;

import java.util.Arrays;
import java.util.List;


/**
 * Creates code to declare a C++ macro. There is no facility at present to
 * define a macro, as in automatically generated code that would be of limited
 * use. This class is intended for referencing macros defined in external header
 * files.
 */
public class Macro
{

  /**
   * Create a reference to a macro defined in the specified header file.
   * 

   *          The name of the macro

   *          The header file in which the macro is defined
   */
  public Macro ( final String name, final CodeFile declaredIn )
  {
    this.name = name;
    this.declaredIn = declaredIn;
  }


  /**
   * Creates a macro call passing the supplied arguments
   * 

   *          the arguments to pass
   * @return a call to this macro
   */
  public MacroCall asMacroCall ( final MacroArgument... args )
  {
    return asMacroCall(Arrays.asList(args));
  }

  /**
   * Creates a macro call passing the supplied arguments
   * 

   *          the arguments to pass
   * @return a call to this macro
   */
  public MacroCall asMacroCall ( final List<MacroArgument> args )
  {
    return new MacroCall(this, args);
  }


  /**
   * Gets the code file that this macro is declared in
   * 
   * @return the code file
   */
  public CodeFile getDeclaredIn ()
  {
    return declaredIn;
  }


  /**
   * Gets the name of this macro
   * 
   * @return the name
   */
  public String getName ()
  {
    return name;
  }


  @Override
  public String toString ()
  {
    return name;
  }


  private final CodeFile declaredIn;

  private final String   name;
}
