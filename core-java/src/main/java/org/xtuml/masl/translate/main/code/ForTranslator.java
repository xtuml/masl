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

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.code.LoopSpec;
import org.xtuml.masl.metamodel.code.LoopSpec.FromToRange;
import org.xtuml.masl.metamodel.code.LoopSpec.TypeRange;
import org.xtuml.masl.metamodel.code.LoopSpec.VariableElements;
import org.xtuml.masl.metamodel.code.LoopSpec.VariableRange;
import org.xtuml.masl.metamodel.type.BasicType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;

public class ForTranslator extends CodeTranslator {

    protected ForTranslator(final org.xtuml.masl.metamodel.code.ForStatement maslFor,
                            final Scope parentScope,
                            final CodeTranslator parentTranslator) {
        super(maslFor, parentScope, parentTranslator);

        final StatementGroup codeBlock = getCode();

        final CodeBlock forCode = new CodeBlock();

        final org.xtuml.masl.metamodel.code.VariableDefinition maslLoopVar = maslFor.getLoopSpec().getLoopVariableDef();

        final TypeUsage type = Types.getInstance().getType(maslLoopVar.getType());

        final LoopSpec loopSpec = maslFor.getLoopSpec();

        VariableDefinitionStatement startVariable = null;
        Expression endCondition = null;
        Expression increment = null;

        if (loopSpec instanceof FromToRange fromTo) {

            final Expression
                    from =
                    ExpressionTranslator.createTranslator(fromTo.getFrom(), getScope()).getReadExpression();
            final Expression to = ExpressionTranslator.createTranslator(fromTo.getTo(), getScope()).getReadExpression();

            final Variable it = new Variable(type, "i", loopSpec.isReverse() ? to : from);
            startVariable = it.asStatement();

            if (loopSpec.isReverse()) {
                endCondition = new BinaryExpression(it.asExpression(), BinaryOperator.GREATER_THAN_OR_EQUAL, from);
                increment = new UnaryExpression(UnaryOperator.PREDECREMENT, it.asExpression());
            } else {
                endCondition = new BinaryExpression(it.asExpression(), BinaryOperator.LESS_THAN_OR_EQUAL, to);
                increment = new UnaryExpression(UnaryOperator.PREINCREMENT, it.asExpression());
            }

            loopVar = new Variable(type.getConstReferenceType(), Mangler.mangleName(maslLoopVar), it.asExpression());
            getScope().addVariable(maslLoopVar, loopVar.asExpression());
            loopVarDef.appendStatement(new VariableDefinitionStatement(loopVar));
        } else if (loopSpec instanceof TypeRange) {
            final TypeRange range = (TypeRange) loopSpec;

            final BasicType rangeType = range.getType().getBasicType();

            final Expression
                    from =
                    ExpressionTranslator.createTranslator(rangeType.getMinValue(), getScope()).getReadExpression();
            final Expression
                    to =
                    ExpressionTranslator.createTranslator(rangeType.getMaxValue(), getScope()).getReadExpression();

            Class itType = Architecture.rangeIterator(type);
            Expression startValue = itType.callConstructor(from, to);
            Expression endValue = itType.callConstructor(to);

            if (loopSpec.isReverse()) {
                itType = Std.reverseIterator(itType);
                final Expression tmp = startValue;
                startValue = itType.callConstructor(endValue);
                endValue = itType.callConstructor(tmp);
            }

            final Variable it = new Variable(new TypeUsage(itType), "i", startValue);
            startVariable = it.asStatement();

            endCondition = new BinaryExpression(it.asExpression(), BinaryOperator.NOT_EQUAL, endValue);
            increment = new UnaryExpression(UnaryOperator.PREINCREMENT, it.asExpression());

            loopVar =
                    new Variable(type.getConstReferenceType(),
                                 Mangler.mangleName(maslLoopVar),
                                 new UnaryExpression(UnaryOperator.DEREFERENCE, it.asExpression()));
            getScope().addVariable(maslLoopVar, loopVar.asExpression());
            loopVarDef.appendStatement(new VariableDefinitionStatement(loopVar));

        } else if (loopSpec instanceof VariableElements elements) {

            final org.xtuml.masl.metamodel.expression.Expression maslVariable = elements.getVariable();

            // Store the collection in a temporary in the case it is a
            // complex expression, as not doing so could mean that the begin and end
            // iterators refer to different collections (eg if the expression returns
            // a different temporary each time
            final Class varType = (Class) Types.getInstance().getType(maslVariable.getType().getBasicType()).getType();
            final Variable
                    collectionVar =
                    new Variable(new TypeUsage(varType),
                                 "collection",
                                 ExpressionTranslator.createTranslator(maslVariable, getScope()).getReadExpression());

            codeBlock.appendStatement(collectionVar.asStatement());

            final Expression collection = collectionVar.asExpression();

            final String begin = (loopSpec.isReverse() ? "r" : "") + "begin";
            final String end = (loopSpec.isReverse() ? "r" : "") + "end";
            final String iterator = "const_" + (loopSpec.isReverse() ? "reverse_" : "") + "iterator";

            final TypeUsage itType = new TypeUsage(varType.referenceNestedType(iterator));

            final Variable it = new Variable(itType, "i", new Function(begin).asFunctionCall(collection, false));

            // Even though might be a reverse iterator, the reverse iterator
            // implementation will convert
            // the ++ operator into the required decrement operation. This allowed
            // reverse iterators and
            // forward iterators to be used by the same algorithms. Therefore just
            // always use PREINCREMENT.
            increment = new UnaryExpression(UnaryOperator.PREINCREMENT, it.asExpression());

            startVariable = new VariableDefinitionStatement(it);
            endCondition =
                    new BinaryExpression(it.asExpression(),
                                         BinaryOperator.NOT_EQUAL,
                                         new Function(end).asFunctionCall(collection, false));

            loopVar =
                    new Variable(type.getConstReferenceType(),
                                 Mangler.mangleName(maslLoopVar),
                                 new UnaryExpression(UnaryOperator.DEREFERENCE, it.asExpression()));
            getScope().addVariable(maslLoopVar, loopVar.asExpression());
            loopVarDef.appendStatement(new VariableDefinitionStatement(loopVar));
        } else if (loopSpec instanceof VariableRange range) {

            final org.xtuml.masl.metamodel.expression.Expression maslVariable = range.getVariable();

            // Store the range in a temporary reference for efficiency in the case it
            // is a complex expression
            final Class varType = (Class) Types.getInstance().getType(maslVariable.getType().getBasicType()).getType();
            final Variable
                    collectionVar =
                    new Variable(new TypeUsage(varType, TypeUsage.ConstReference),
                                 "collection",
                                 ExpressionTranslator.createTranslator(maslVariable, getScope()).getReadExpression());

            codeBlock.appendStatement(collectionVar.asStatement());

            final Expression collection = collectionVar.asExpression();

            final Expression from = new Function("first").asFunctionCall(collection, false);
            final Expression to = new Function("last").asFunctionCall(collection, false);

            final Variable it = new Variable(type, "it", loopSpec.isReverse() ? to : from);
            startVariable = new VariableDefinitionStatement(it);

            if (loopSpec.isReverse()) {
                endCondition = new BinaryExpression(it.asExpression(), BinaryOperator.GREATER_THAN_OR_EQUAL, from);
                increment = new UnaryExpression(UnaryOperator.PREDECREMENT, it.asExpression());
            } else {
                endCondition = new BinaryExpression(it.asExpression(), BinaryOperator.LESS_THAN_OR_EQUAL, to);
                increment = new UnaryExpression(UnaryOperator.PREINCREMENT, it.asExpression());
            }

            loopVar = new Variable(type.getConstReferenceType(), Mangler.mangleName(maslLoopVar), it.asExpression());
            getScope().addVariable(maslLoopVar, loopVar.asExpression());
            loopVarDef.appendStatement(new VariableDefinitionStatement(loopVar));

        }

        forCode.appendStatement(loopVarDef);
        codeBlock.appendStatement(new ForStatement(startVariable, endCondition, increment, forCode));

        for (final org.xtuml.masl.metamodel.code.Statement maslStatement : maslFor.getStatements()) {
            final CodeTranslator translator = createChildTranslator(maslStatement);
            final CodeBlock translation = translator.getFullCode();
            forCode.appendStatement(translation);
        }

    }

    Label getEndOfLoopLabel() {
        if (label == null) {
            // Need a label at the end of the loop so that a masl Exit statement will
            // know where to go to. See ExitStatement for why we can't just use break
            // to
            // do this;
            label = new Label();
            getCode().appendStatement(new LabelStatement(label));
        }
        return label;
    }

    public Variable getLoopVariable() {
        return loopVar;
    }

    public StatementGroup getLoopVarDef() {
        return loopVarDef;
    }

    private final StatementGroup loopVarDef = new StatementGroup();
    private Variable loopVar;
    private Label label;
}
