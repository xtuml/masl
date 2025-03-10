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

import org.xtuml.masl.javagen.ast.expr.ClassLiteral;

public interface PrimitiveType extends Type {

    enum Tag {
        BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, VOID
    }

    Tag getTag();

    @Override
    ClassLiteral clazz();

}
