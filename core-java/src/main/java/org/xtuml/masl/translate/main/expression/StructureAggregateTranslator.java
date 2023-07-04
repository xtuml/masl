/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ----------------------------------------------------------------------------
 Classification: UK OFFICIAL
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
        final List<org.xtuml.masl.cppgen.Expression> readParams = new ArrayList<org.xtuml.masl.cppgen.Expression>();
        final List<org.xtuml.masl.cppgen.Expression> writeParams = new ArrayList<org.xtuml.masl.cppgen.Expression>();

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
