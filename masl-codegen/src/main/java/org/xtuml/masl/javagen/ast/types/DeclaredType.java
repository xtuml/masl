/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.types;

import org.xtuml.masl.javagen.ast.def.TypeDeclaration;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.NewInstance;
import org.xtuml.masl.javagen.ast.expr.Qualifier;

import java.util.List;

public interface DeclaredType extends ReferenceType {

    List<? extends Type> getTypeArguments();

    TypeDeclaration getTypeDeclaration();

    void addTypeArgument(Type argument);

    Qualifier getQualifier();

    void forceQualifier();

    NewInstance newInstance(Expression... args);

}
