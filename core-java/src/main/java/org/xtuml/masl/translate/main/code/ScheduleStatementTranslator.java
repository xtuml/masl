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

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.ExpressionStatement;
import org.xtuml.masl.metamodel.expression.Expression;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.object.EventTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.ArrayList;
import java.util.List;

public class ScheduleStatementTranslator extends CodeTranslator {

    protected ScheduleStatementTranslator(final org.xtuml.masl.metamodel.code.ScheduleStatement schedule,
                                          final Scope parentScope,
                                          final CodeTranslator parentTranslator) {
        super(schedule, parentScope, parentTranslator);

        final ExpressionTranslator
                timerIdTranslator =
                ExpressionTranslator.createTranslator(schedule.getTimerId(), getScope());
        final List<org.xtuml.masl.cppgen.Expression> paramExprs = new ArrayList<>();

        for (final Expression parameter : schedule.getGenerate().getArguments()) {
            final ExpressionTranslator paramTranslator = ExpressionTranslator.createTranslator(parameter, getScope());
            final org.xtuml.masl.cppgen.Expression paramExpression = paramTranslator.getReadExpression();
            paramExprs.add(paramExpression);
        }

        if (getScope().getParentObject() != null &&
            ((getScope().getState() != null && getScope().getState().getType() == State.Type.NORMAL) ||
             (getScope().getObjectService() != null && getScope().getObjectService().isInstance()))) {
            paramExprs.add(getScope().getParentObject().getObjectId());
            paramExprs.add(getScope().getParentObject().getGetId().asFunctionCall());
        }

        final ObjectDeclaration eventObject = schedule.getGenerate().getEvent().getParentObject();
        final ObjectTranslator objectTranslator = ObjectTranslator.getInstance(eventObject);
        final EventTranslator eventTrans = objectTranslator.getEventTranslator(schedule.getGenerate().getEvent());

        org.xtuml.masl.cppgen.Expression eventToSchedule;
        if (schedule.getGenerate().getEvent().getType() == EventDeclaration.Type.NORMAL) {
            final org.xtuml.masl.cppgen.Expression
                    destObject =
                    ExpressionTranslator.createTranslator(schedule.getGenerate().getToInstance(),
                                                          getScope()).getReadExpression();
            eventToSchedule = eventTrans.getCreateFunction().asFunctionCall(destObject, true, paramExprs);
        } else {
            eventToSchedule = eventTrans.getCreateFunction().asFunctionCall(paramExprs);
        }
        org.xtuml.masl.cppgen.Expression
                timeout =
                ExpressionTranslator.createTranslator(schedule.getTime(), getScope()).getReadExpression();

        if (!schedule.isAbsoluteTime()) {
            timeout = new BinaryExpression(timeout, BinaryOperator.PLUS, Architecture.Timestamp.now);
        }

        final org.xtuml.masl.cppgen.Expression
                period =
                schedule.getPeriod() == null ?
                Architecture.Duration.zero :
                ExpressionTranslator.createTranslator(schedule.getPeriod(), getScope()).getReadExpression();

        final org.xtuml.masl.cppgen.Expression
                scheduleTimerFnCall =
                Architecture.Timer.scheduleTimer(timerIdTranslator.getReadExpression(),
                                                 timeout,
                                                 period,
                                                 eventToSchedule);

        getCode().appendStatement(new ExpressionStatement(scheduleTimerFnCall));
    }
}
