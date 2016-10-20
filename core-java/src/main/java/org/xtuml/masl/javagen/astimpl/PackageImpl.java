//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.CompilationUnit;


class PackageImpl extends ASTNodeImpl
    implements org.xtuml.masl.javagen.ast.def.Package
{

  PackageImpl ( final ASTImpl ast, final String name )
  {
    super(ast);
    this.name = name;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitPackage(this, p);
  }

  @Override
  public CompilationUnitImpl addCompilationUnit ( final CompilationUnit compilationUnit )
  {
    compilationUnits.add((CompilationUnitImpl)compilationUnit);
    return (CompilationUnitImpl)compilationUnit;
  }

  @Override
  public Collection<? extends CompilationUnitImpl> getCompilationUnits ()
  {
    return Collections.unmodifiableList(compilationUnits);
  }

  @Override
  public String getName ()
  {
    return name;
  }

  String toPathString ()
  {
    return name.replace('.', System.getProperty("file.separator").charAt(0));
  }

  @Override
  public String toString ()
  {
    return name;
  }

  boolean containsPublicTypeNamed ( final String name )
  {
    for ( final CompilationUnitImpl cu : compilationUnits )
    {
      if ( cu.getName().equals(name) && cu.containsPublicTypeNamed(name) )
      {
        return true;
      }
    }
    return false;
  }

  boolean containsTypeNamed ( final String name )
  {
    for ( final CompilationUnitImpl cu : compilationUnits )
    {
      if ( cu.getName().equals(name) && cu.containsTypeNamed(name) )
      {
        return true;
      }
    }
    return false;
  }

  @Override
  public TypeDeclarationImpl addTypeDeclaration ( final String name )
  {
    return addCompilationUnit(name).addTypeDeclaration(getAST().createTypeDeclaration(name));
  }

  @Override
  public CompilationUnitImpl addCompilationUnit ( final String name )
  {
    return addCompilationUnit(getAST().createCompilationUnit(name));
  }


  private final List<CompilationUnitImpl> compilationUnits = new ChildNodeList<CompilationUnitImpl>(this);

  private final String                    name;

}
