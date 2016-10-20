//
// UK Crown Copyright (c) 2011. All Rights Reserved.
//
package org.xtuml.masl.javagen.ast.expr;

import org.xtuml.masl.javagen.ast.def.Package;


public interface PackageQualifier
    extends Qualifier
{

  Package getPackage ();
}
