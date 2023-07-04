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
import org.xtuml.masl.metamodel.expression.RangeExpression;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;

public class RangeTranslator extends ExpressionTranslator {

    public RangeTranslator(final RangeExpression range, final Scope scope) {
        org.xtuml.masl.cppgen.Expression minExp;
        org.xtuml.masl.cppgen.Expression maxExp;

        minExp = createTranslator(range.getMin(), scope).getReadExpression();
        maxExp = createTranslator(range.getMax(), scope).getReadExpression();

        final BasicType contained = range.getMin().getType();
        final TypeUsage type = Types.getInstance().getType(contained);
        setReadExpression(Architecture.sequence(type).callConstructor(Architecture.range(type).callConstructor(minExp,
                                                                                                               maxExp)));
    }

}
