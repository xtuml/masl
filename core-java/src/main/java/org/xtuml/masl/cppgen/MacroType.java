/*
 * Filename : MacroType.java
 * 
 * UK Crown Copyright (c) 2008. All Rights Reserved
 */
package org.xtuml.masl.cppgen;

/**
 * Enable a Macro declaration to be used on the left hand side of a variable
 * definition.
 * 
 */
public class MacroType extends Type
{

  public MacroType ( final MacroCall type )
  {
    super(type.toString());
  }

  @Override
  boolean preferPassByReference ()
  {
    return false;
  }

}
