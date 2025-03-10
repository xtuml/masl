/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.ast.def;

import org.xtuml.masl.javagen.ast.ASTNode;
import org.xtuml.masl.javagen.ast.expr.Expression;
import org.xtuml.masl.javagen.ast.expr.MethodInvocation;
import org.xtuml.masl.javagen.ast.types.Type;

public interface Method extends TypeMember, Callable, GenericItem, Throwing, ASTNode {

    Modifiers getModifiers();

    String getName();

    void setName(String name);

    Type getReturnType();

    Type setReturnType(Type type);

    void setStatic();

    void setAbstract();

    void setFinal();

    void setSynchronized();

    void setNative();

    void setStrictFp();

    boolean isStatic();

    boolean isAbstract();

    boolean isFinal();

    boolean isSynchronized();

    boolean isNative();

    boolean isStrictFp();

    MethodInvocation call(Expression... args);

}
