//
// File: TemplateSpecialisation.java
//
// UK Crown Copyright (c) 2007. All Rights Reserved.
//
package org.xtuml.masl.cppgen;

import java.util.Set;


public abstract class TemplateSpecialisation
{

  abstract String getValue ( Namespace currentNamespace );

  abstract Set<Declaration> getDirectUsageForwardDeclarations ();

  abstract Set<CodeFile> getDirectUsageIncludes ();

  abstract Set<Declaration> getIndirectUsageForwardDeclarations ();

  abstract Set<CodeFile> getIndirectUsageIncludes ();

  abstract Set<CodeFile> getNoRefDirectUsageIncludes ();

  abstract boolean isTemplateType ();

  public static TemplateSpecialisation create ( final Expression expression )
  {
    return new TemplateSpecialisation()
    {

      @Override
      String getValue ( final Namespace currentNamespace )
      {
        return expression.getCode(currentNamespace);
      }

      @Override
      Set<Declaration> getDirectUsageForwardDeclarations ()
      {
        return expression.getForwardDeclarations();
      }

      @Override
      Set<Declaration> getIndirectUsageForwardDeclarations ()
      {
        return expression.getForwardDeclarations();
      }

      @Override
      Set<CodeFile> getDirectUsageIncludes ()
      {
        return expression.getIncludes();
      }

      @Override
      Set<CodeFile> getIndirectUsageIncludes ()
      {
        return expression.getIncludes();
      }

      @Override
      Set<CodeFile> getNoRefDirectUsageIncludes ()
      {
        return expression.getIncludes();
      }

      @Override
      boolean isTemplateType ()
      {
        return false;
      }

    };
  }

  public static TemplateSpecialisation create ( final TypeUsage type )
  {
    return new TemplateSpecialisation()
    {

      @Override
      String getValue ( final Namespace currentNamespace )
      {
        return type.getQualifiedName(currentNamespace);
      }

      @Override
      Set<Declaration> getDirectUsageForwardDeclarations ()
      {
        return type.getDirectUsageForwardDeclarations();
      }

      @Override
      Set<CodeFile> getDirectUsageIncludes ()
      {
        return type.getDirectUsageIncludes();
      }

      @Override
      Set<CodeFile> getNoRefDirectUsageIncludes ()
      {
        return type.getNoRefDirectUsageIncludes();
      }

      @Override
      Set<Declaration> getIndirectUsageForwardDeclarations ()
      {
        return type.getIndirectUsageForwardDeclarations();
      }

      @Override
      Set<CodeFile> getIndirectUsageIncludes ()
      {
        return type.getIndirectUsageIncludes();
      }

      @Override
      boolean isTemplateType ()
      {
        return type.isTemplateType();
      }

    };
  }

}
