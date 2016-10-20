//
// File: EnumerationType.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.xtuml.masl.utils.TextUtils;


/**
 * Represents a C++ enum type
 */
public class EnumerationType extends Type
{

  /**
   * Represents one of the eumerators in an enum type.
   */
  public class Enumerator
  {

    /**
     * Creates an enumerator for the enclosing enumeration
     * 

     *          The name of the enumerate.

     *          The value to be assigned to this enumerate. If null, then the
     *          enumerate will have the default value.
     */
    Enumerator ( final String name, final Expression value )
    {
      this.name = name;
      this.value = value;
    }

    /**
     * Creates an expression that evaluates to the enumerator
     * 
     * @return an expression representing this value
     */
    public Expression asExpression ()
    {
      return new Expression()
      {

        @Override
        String getCode ( final Namespace currentNamespace, final String alignment )
        {
          return getQualifiedName(currentNamespace);
        }

        @Override
        Set<Declaration> getForwardDeclarations ()
        {
          return new LinkedHashSet<Declaration>();
        }

        @Override
        Set<CodeFile> getIncludes ()
        {
          return getDirectUsageIncludes();
        }

        @Override
        int getPrecedence ()
        {
          return 0;
        }
      };

    }

    @Override
    public String toString ()
    {
      return name + (value != null ? " = " + value : "");
    }

    /**
     * Gets the declaration code for this enumerator.
     * 

     *          The namespace that the enumeration is to be declared in.
     * @return the declaration code
     */
    String getDeclaration ( final Namespace currentNamespace )
    {
      return name + (value != null ? "\t= " + value.getCode(currentNamespace, "\t") : "");
    }

    /**
     * Gets the fully qualified name of the enumerator
     * 
     * @return the fully qualified name
     */
    String getQualifiedName ()
    {
      return getQualifiedName(null);
    }


    /**
     * Gets the name of the enumerator suitably qualified to be used in the
     * supplied namespace
     * 

     *          The namespace the name is to be used in
     * @return the qualified name
     */
    String getQualifiedName ( final Namespace currentNamespace )
    {
      if ( getParentNamespace() == null || getParentNamespace().contains(currentNamespace) )
      {
        return name;
      }
      else
      {
        return getParentNamespace().getQualifiedName(currentNamespace) + "::" + name;
      }

    }

    final private String     name;
    final private Expression value;

  }

  /**
   * The declaration for the enclosing enumeration
   */
  class EnumerationDeclaration extends Declaration
  {

    @Override
    public boolean equals ( final Object rhs )
    {
      if ( this == rhs )
      {
        return true;
      }
      if ( rhs instanceof EnumerationDeclaration )
      {
        final EnumerationDeclaration rhsDec = (EnumerationDeclaration)rhs;
        return EnumerationType.this.equals(rhsDec.getEnumeration());
      }
      return false;
    }

    @Override
    public int hashCode ()
    {
      return EnumerationType.this.hashCode();
    }

    @Override
    Set<Declaration> getForwardDeclarations ()
    {
      final Set<Declaration> result = super.getForwardDeclarations();
      for ( final Enumerator val : values )
      {
        if ( val.value != null )
        {
          result.addAll(val.value.getForwardDeclarations());
        }
      }
      return result;
    }

    @Override
    Set<CodeFile> getIncludes ()
    {
      final Set<CodeFile> result = super.getIncludes();
      for ( final Enumerator val : values )
      {
        if ( val.value != null )
        {
          result.addAll(val.value.getIncludes());
        }
      }
      return result;
    }

    @Override
    void writeDeclaration ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
    {
      final StringBuilder definition = new StringBuilder();

      final List<String> strings = new ArrayList<String>(values.size());
      for ( final Enumerator value : values )
      {
        strings.add(value.getDeclaration(currentNamespace));
      }
      definition.append(indent
                        + "enum "
                        + getQualifiedName(currentNamespace) + " {"
                        + TextUtils.formatList(strings, " ", "\t", "", ",\n", " ") + "};\n");
      TextUtils.alignTabs(writer, definition.toString());
    }

    @Override
    void writeForwardDeclaration ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
    {
      writeDeclaration(writer, indent, currentNamespace);
    }


    /**
     * Gets the enumeration that this declaratio declares
     * 
     * @return The enumeration
     */
    private EnumerationType getEnumeration ()
    {
      return EnumerationType.this;
    }

  }

  /**
   * Creates a new enumeration type in the global namespace
   * 

   *          The name of the type
   */
  public EnumerationType ( final String name )
  {
    super(name);
  }

  /**
   * Creates a new enumeration type
   * 

   *          The name of the type

   *          The namspace the type is contained in
   */
  public EnumerationType ( final String name, final Namespace parentNamespace )
  {
    super(name, parentNamespace);
    getDeclaration().setParentNamespace(parentNamespace);
  }

  /**
   * Adds a new enumerator to the enumeration
   * 

   *          The name of the enumerator

   *          The value that this name will be assigned. <code>null</code> means
   *          that no value is specified, so the C++ default will be used.
   * @return the emumerator that was added
   */
  public Enumerator addEnumerator ( final String name, final Expression value )
  {
    final Enumerator result = new Enumerator(name, value);
    values.add(result);
    return result;
  }


  @Override
  public boolean equals ( final Object rhs )
  {
    if ( this == rhs )
    {
      return true;
    }
    if ( rhs instanceof EnumerationType )
    {
      final EnumerationType rhsEnum = (EnumerationType)rhs;
      return super.equals(rhsEnum);
    }
    return false;
  }

  @Override
  public String toString ()
  {
    final Writer writer = new StringWriter();
    try
    {
      getDeclaration().writeDeclaration(writer, "", null);
    }
    catch ( final IOException e )
    {
      e.printStackTrace();
    }
    return writer.toString();
  }

  @Override
  Declaration getDeclaration ()
  {
    return declaration;
  }

  @Override
  Set<CodeFile> getDirectUsageIncludes ()
  {
    final Set<CodeFile> result = super.getDirectUsageIncludes();
    result.addAll(declaration.getUsageIncludes());
    return result;
  }

  @Override
  Set<CodeFile> getNoRefDirectUsageIncludes ()
  {
    final Set<CodeFile> result = super.getNoRefDirectUsageIncludes();
    result.addAll(declaration.getUsageIncludes());
    return result;
  }


  @Override
  Set<CodeFile> getIndirectUsageIncludes ()
  {
    // We could redeclare the type locally, as a forward declaration, but less
    // messy in the generated code to include the file.
    return getDirectUsageIncludes();
  }

  @Override
  boolean preferPassByReference ()
  {
    return false;
  }

  private final Declaration      declaration = new EnumerationDeclaration();

  private final List<Enumerator> values      = new ArrayList<Enumerator>();

}
