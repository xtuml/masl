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

import java.util.Set;

public interface Modifiers extends ASTNode {

    Set<? extends Modifier> getModifiers();

    boolean isAbstract();

    boolean isFinal();

    boolean isNative();

    boolean isPrivate();

    boolean isProtected();

    boolean isPublic();

    boolean isStatic();

    boolean isStrictFp();

    boolean isSynchronized();

    boolean isTransient();

    boolean isVolatile();

}
