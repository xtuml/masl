/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen;

import org.xtuml.masl.javagen.ast.AST;
import org.xtuml.masl.javagen.ast.ASTFactory;
import org.xtuml.masl.javagen.ast.code.CodeBlock;
import org.xtuml.masl.javagen.ast.code.LocalVariable;
import org.xtuml.masl.javagen.ast.def.Package;
import org.xtuml.masl.javagen.ast.def.*;

public class Test {

    public static void main(final String[] args) throws Exception {

        final AST ast = ASTFactory.createAST();
        final Package pkg = ast.createPackage("org.xtuml.datacache");
        final TypeDeclaration td = pkg.addTypeDeclaration("MyClass");

        final Field fieldP1 = td.addField(ast.createInt(), "p1");
        final Field fieldP3 = td.addField(ast.createInt(), "p3");

        final Method method = td.addMethod("SomeMethod");
        final Parameter p1 = method.addParameter(ast.createInt(), "p1");
        final Parameter p2 = method.addParameter(ast.createInt(), "p2");
        method.setReturnType(ast.createInt());

        final CodeBlock code = method.setCodeBlock();

        code.addStatement(ast.createReturn(fieldP1.asExpression().add(p1.asExpression().add(p2.asExpression()))));
        final LocalVariable localP3 = ast.createLocalVariable(ast.createInt(), "p3");
        localP3.setFinal();

        code.addStatement(fieldP3.asExpression().increment());
        code.addStatement(localP3);
        code.addStatement(fieldP3.asExpression().increment());

        final CodeWriter writer = new CodeWriter();
        writer.visit(pkg);

        System.out.println(writer.getCode());
        writer.clear();

    }
}
