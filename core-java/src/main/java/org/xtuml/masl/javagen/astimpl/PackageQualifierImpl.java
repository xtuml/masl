//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.expr.PackageQualifier;


public class PackageQualifierImpl extends QualifierImpl
    implements PackageQualifier
{

  public PackageQualifierImpl ( final ASTImpl ast, final Package pkg )
  {
    super(ast);
    this.pkg = pkg;
  }

  @Override
  public Package getPackage ()
  {
    return pkg;
  }

  @Override
  public <R, P> R accept ( final ASTNodeVisitor<R, P> v, final P p ) throws Exception
  {
    return v.visitPackageQualifier(this, p);
  }

  private final Package pkg;
}
