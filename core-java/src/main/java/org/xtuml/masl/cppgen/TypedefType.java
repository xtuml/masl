//
// File: TypedefType.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;



public class TypedefType extends Type
{

  class TypedefDeclaration extends Declaration
  {

    @Override
    public Set<Declaration> getForwardDeclarations ()
    {
      final Set<Declaration> result = super.getForwardDeclarations();
      result.addAll(aliasFor.getDirectUsageForwardDeclarations());

      return result;
    }

    @Override
    public Set<CodeFile> getIncludes ()
    {
      final Set<CodeFile> result = super.getIncludes();
      result.addAll(aliasFor.getDirectUsageIncludes());

      return result;
    }

    @Override
    void writeDeclaration ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
    {
      writer.write(indent
                   + "typedef "
                   + aliasFor.getQualifiedName(currentNamespace)
                   + " "
                   + getQualifiedName(currentNamespace)
                   + ";\n");
    }


    @Override
    void writeForwardDeclaration ( final Writer writer, final String indent, final Namespace currentNamespace ) throws IOException
    {
      writer.write(indent
                   + "typedef "
                   + aliasFor.getQualifiedName(currentNamespace)
                   + " "
                   + getQualifiedName(currentNamespace)
                   + ";\n");
    }
  }

  
  public TypedefType ( final String name, final Namespace parentNamespace, final TypeUsage aliasFor )
  {
    super(name, parentNamespace);
    this.aliasFor = aliasFor;
    getDeclaration().setParentNamespace(parentNamespace);
    thisNamespace = new Namespace(name);
    thisNamespace.setParentNamespace(parentNamespace);
  }

  public TypedefType ( final String name, final Namespace parentNamespace, final TypeUsage aliasFor, final CodeFile declaredIn )
  {
    this(name, parentNamespace, aliasFor);
    getDeclaration().addDeclaredIn(declaredIn);
    thisNamespace = new Namespace(name);
    thisNamespace.setParentNamespace(parentNamespace);
  }

  
  public TypedefType ( final String name, final TypeUsage aliasFor )
  {
    super(name);
    thisNamespace = new Namespace(name);
    this.aliasFor = aliasFor;
  }


  public Class asClass ()
  {
    return new Class(getName(), getParentNamespace(), getDirectUsageIncludes());
  }

  public TypedefType referenceNestedType ( final String name )
  {
    return new TypedefType(name, getNamespace(), null);
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
    final Set<CodeFile> result = new LinkedHashSet<CodeFile>();
    if ( declaration.getParentClass() == null )
    {
      result.addAll(super.getDirectUsageIncludes());
      result.addAll(declaration.getUsageIncludes());
    }
    else
    {
      result.addAll(declaration.getParentClass().getDirectUsageIncludes());
    }
    return result;
  }

  @Override
  Set<CodeFile> getNoRefDirectUsageIncludes ()
  {
    final Set<CodeFile> result = new LinkedHashSet<CodeFile>();
    if ( declaration.getParentClass() == null )
    {
      result.addAll(super.getNoRefDirectUsageIncludes());
      result.addAll(declaration.getUsageIncludes());
    }
    else
    {
      result.addAll(declaration.getParentClass().getNoRefDirectUsageIncludes());
    }
    return result;
  }

  @Override
  Set<CodeFile> getIndirectUsageIncludes ()
  {
    final Set<CodeFile> result = super.getIndirectUsageIncludes();
    result.addAll(declaration.getUsageIncludes());
    return result;
  }

  /**
   * @return the namespace that this typedef defines
   */
  Namespace getNamespace ()
  {
    return thisNamespace;
  }

  @Override
  boolean isTemplateType ()
  {
    return aliasFor.isTemplateType();
  }

  @Override
  boolean preferPassByReference ()
  {
    return aliasFor.preferPassByReference();
  }

  @Override
  void setParentNamespace ( final Namespace parentNamespace )
  {
    super.setParentNamespace(parentNamespace);
    getDeclaration().setParentNamespace(parentNamespace);
    thisNamespace.setParentNamespace(parentNamespace);
  }

  /**
   * The namespace that is defined by this class
   */
  private Namespace         thisNamespace;

  private final TypeUsage   aliasFor;

  private final Declaration declaration = new TypedefDeclaration();

}
