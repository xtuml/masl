/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

import org.xtuml.masl.javagen.ast.ASTNodeVisitor;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.expr.PackageQualifier;

public class PackageQualifierImpl extends QualifierImpl implements PackageQualifier {

    public PackageQualifierImpl(final ASTImpl ast, final Package pkg) {
        super(ast);
        this.pkg = pkg;
    }

    @Override
    public Package getPackage() {
        return pkg;
    }

    @Override
    public void accept(final ASTNodeVisitor v) throws Exception {
        v.visitPackageQualifier(this);
    }

    private final Package pkg;
}
