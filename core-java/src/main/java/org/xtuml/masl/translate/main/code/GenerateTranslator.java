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
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.GenerateStatement;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.InstanceType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.ArgumentTranslator;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.List;

public class GenerateTranslator extends CodeTranslator {

    protected GenerateTranslator(final GenerateStatement generate,
                                 final Scope parentScope,
                                 final CodeTranslator parentTranslator) {
        super(generate, parentScope, parentTranslator);

        final EventDeclaration event = generate.getEvent();

        final ObjectDeclaration
                toObj =
                generate.getToInstance() == null ?
                event.getParentObject() :
                ((InstanceType) generate.getToInstance().getType()).getObjectDeclaration();

        final ObjectTranslator obj = ObjectTranslator.getInstance(toObj);

        final Function generateFunction = obj.getEventTranslator(event).getCreateFunction();

        final List<Expression>
                args =
                new ArgumentTranslator(event.getParameters(), generate.getArguments(), getScope()).getArguments();

        if (getScope().getParentObject() != null &&
            ((getScope().getState() != null && getScope().getState().getType() == State.Type.NORMAL) ||
             (getScope().getObjectService() != null && getScope().getObjectService().isInstance()))) {
            args.add(getScope().getParentObject().getObjectId());
            args.add(getScope().getParentObject().getGetId().asFunctionCall());
        }

        Expression generatedEvent;
        if (event.getType() == EventDeclaration.Type.NORMAL) {
            final Expression
                    toInstance =
                    ExpressionTranslator.createTranslator(generate.getToInstance(), getScope()).getReadExpression();

            generatedEvent = generateFunction.asFunctionCall(toInstance, true, args);
        } else {
            generatedEvent = generateFunction.asFunctionCall(args);
        }

        final Expression
                addEvent =
                new Function("addEvent").asFunctionCall(Architecture.eventQueue, false, generatedEvent);
        getCode().appendStatement(addEvent.asStatement());

    }

}
