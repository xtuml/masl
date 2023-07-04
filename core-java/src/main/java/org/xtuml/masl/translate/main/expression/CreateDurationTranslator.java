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

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.expression.CreateDurationExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;

public class CreateDurationTranslator extends ExpressionTranslator {

    CreateDurationTranslator(final CreateDurationExpression expression, final Scope scope) {
        final Expression
                arg =
                ExpressionTranslator.createTranslator(expression.getArgument(), scope).getReadExpression();
        switch (expression.getField()) {
            case Weeks:
                setReadExpression(Architecture.Duration.fromWeeks(arg));
                break;
            case Days:
                setReadExpression(Architecture.Duration.fromDays(arg));
                break;
            case Hours:
                setReadExpression(Architecture.Duration.fromHours(arg));
                break;
            case Minutes:
                setReadExpression(Architecture.Duration.fromMinutes(arg));
                break;
            case Seconds:
                setReadExpression(Architecture.Duration.fromSeconds(arg));
                break;
            case Millis:
                setReadExpression(Architecture.Duration.fromMillis(arg));
                break;
            case Micros:
                setReadExpression(Architecture.Duration.fromMicros(arg));
                break;
            case Nanos:
                setReadExpression(Architecture.Duration.fromNanos(arg));
                break;
        }
    }

}
