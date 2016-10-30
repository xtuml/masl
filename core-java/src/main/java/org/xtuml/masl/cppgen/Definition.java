//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Writes a definition for a C++ construct. This should be subclassed for each
 * construct requiring a definition.
 */
abstract class Definition
{

  /**
   * Create a definition
   * 

   *          The declaration associated with this definition
   */
  Definition ( final Declaration declaration )
  {
    this.declaration = declaration;
  }

  @Override
  public String toString ()
  {
    final Writer writer = new StringWriter();
    try
    {
      writeDefinition(writer, "", null);
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
    return writer.toString();
  }

  /**
   * Calculates the set of forward declarations needed to compile this
   * definition
   * 
   * @return the required forward declarations
   */
  Set<Declaration> getForwardDeclarations ()
  {
    // If the declaration has been added to an include file, then we will be
    // including that file, so no forward declarations are necessary (other than
    // those supplied by subclasses). Otherwise, we must assume that no
    // declaration is visible, so the definition will be self declaring. In this
    // case we need to have the same forward declarations as the declaration
    // would have needed.
    if ( declaration.getUsageIncludes().size() == 0 )
    {
      return declaration.getForwardDeclarations();
    }
    else
    {
      return new LinkedHashSet<Declaration>();
    }
  }

  /**
   * Calculates the set of include files needed to compile this definition
   * 
   * @return the required include files
   */
  Set<CodeFile> getIncludes ()
  {
    // If the declaration has been added to an include file, then we need to
    // include that file. Otherwise, we must assume that no
    // declaration is available, so the definition will be self declaring. In
    // this
    // case we need to have the same include files as the declaration
    // would have needed.
    final Set<CodeFile> result = new LinkedHashSet<CodeFile>();
    if ( declaration.getUsageIncludes().size() == 0 )
    {
      result.addAll(declaration.getIncludes());
    }
    else
    {
      result.addAll(declaration.getUsageIncludes());
    }
    return result;
  }

  /**
   * Calculates the namsespace containing this definition. If the definition is
   * contained within a class, then returns the namespace that contains that
   * class.
   * 
   * @return the namespace containing this definition
   */
  Namespace getParentNamespace ()
  {
    if ( declaration.getParentClass() == null )
    {
      return declaration.getParentNamespace();
    }
    else
    {
      org.xtuml.masl.cppgen.Class parentClass = declaration.getParentClass();
      while ( parentClass.getDeclaration().getParentClass() != null )
      {
        parentClass = parentClass.getDeclaration().getParentClass();
      }
      return parentClass.getDeclaration().getParentNamespace();
    }
  }

  /**
   * Writes the definition to the supplied Writer.
   * 

   *          the writer to write to

   *          the initial indentation level for the code

   *          the namespace that the declaration is being written to.
   * @throws IOException
   */
  abstract void writeDefinition ( Writer writer, String indent, Namespace currentNamespace ) throws IOException;

  /**
   * The declaration associated with this definition.
   */
  private final Declaration declaration;

  private CodeFile          definedIn = null;

  void setDefinedIn ( final CodeFile file )
  {
    definedIn = file;
  }


  CodeFile getDefinedIn ()
  {
    return definedIn;
  }

}
