//
// File: Statement.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Superclass of all C++ Statements
 */
public abstract class Statement
{

  /**
   * Writes the statement to the supplied Writer. It will be indented by the
   * suplied indent, and names will be resolved according to the current
   * namespace.
   * 



   * @throws IOException
   */
  abstract void write ( Writer writer, String indent, Namespace currentNamespace ) throws IOException;

  /**
   * Calculates the set of forward declarations necessary for this statement to
   * compile
   * 
   * @return the necessary declarations
   */
  Set<Declaration> getForwardDeclarations ()
  {
    return new LinkedHashSet<Declaration>();
  }

  /**
   * Calculates the set of include files necessary for this statement to compile
   * 
   * @return the necessary include files
   */
  Set<CodeFile> getIncludes ()
  {
    return new LinkedHashSet<CodeFile>();
  }

  void setParent ( final Statement parent )
  {
    this.parent = parent;
  }

  void setParentFunction ( final Function function )
  {
    this.function = function;
  }

  Statement getParent ()
  {
    return parent;
  }

  Function getParentFunction ()
  {
    if ( function == null && parent != null )
    {
      return parent.getParentFunction();
    }
    else
    {
      return function;
    }
  }

  private Statement parent   = null;

  private Function  function = null;
}
