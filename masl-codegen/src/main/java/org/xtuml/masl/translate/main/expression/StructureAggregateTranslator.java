/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.expression.StructureAggregate;
import org.xtuml.masl.metamodel.type.AnonymousStructure;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;

import java.util.ArrayList;
import java.util.List;

public class StructureAggregateTranslator extends ExpressionTranslator {

    StructureAggregateTranslator(final StructureAggregate aggregate, final Scope scope, final BasicType requiredType) {
        final List<org.xtuml.masl.cppgen.Expression> readParams = new ArrayList<>();
        final List<org.xtuml.masl.cppgen.Expression> writeParams = new ArrayList<>();

        for (final Expression element : aggregate.getElements()) {
            readParams.add(createTranslator(element, scope).getReadExpression());
            writeParams.add(createTranslator(element, scope).getWriteableExpression());
        }

        if ((requiredType == null || requiredType instanceof AnonymousStructure) &&
            aggregate.getType() instanceof AnonymousStructure) {
            setReadExpression(BigTuple.getMakeTuple(readParams));
            setWriteableExpression(BigTuple.getMakeTuple(writeParams));
        } else {
            final TypeUsage
                    cppType =
                    Types.getInstance().getType(requiredType == null ? aggregate.getType() : requiredType);
            setReadExpression(cppType.getType().callConstructor(readParams));
            setWriteableExpression(cppType.getType().callConstructor(writeParams));
        }
    }
}
