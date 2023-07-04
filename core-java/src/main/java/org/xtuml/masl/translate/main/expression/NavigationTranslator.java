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
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.metamodel.expression.CorrelatedNavExpression;
import org.xtuml.masl.metamodel.expression.NavigationExpression;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.type.BagType;
import org.xtuml.masl.metamodel.type.SetType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.ArrayList;
import java.util.List;

public class NavigationTranslator extends ExpressionTranslator {

    NavigationTranslator(final NavigationExpression navigation, final Scope scope) {
        final ObjectTranslator sourceObj = ObjectTranslator.getInstance(navigation.getRelationship().getFromObject());

        final Function
                navFunction =
                sourceObj.getRelationshipTranslator(navigation.getRelationship()).getPublicAccessors().getNavigateFunction(
                        navigation.getSkeleton());

        final TypeUsage
                destObj =
                new TypeUsage(ObjectTranslator.getInstance(navigation.getRelationship().getDestinationObject()).getMainClass());

        Function archNavFn = null;
        if (navigation.getRelationship().getCardinality() == MultiplicityType.MANY) {
            if (navigation.getType() instanceof BagType) {
                archNavFn = Architecture.navigateManyBag(destObj);
            } else {
                archNavFn = Architecture.navigateMany(destObj);
            }
        } else {
            if (navigation.getType() instanceof BagType) {
                archNavFn = Architecture.navigateOneBag(destObj);
            } else {
                archNavFn = Architecture.navigateOne(destObj);
            }
        }

        final List<org.xtuml.masl.cppgen.Expression> bindArgs = new ArrayList<org.xtuml.masl.cppgen.Expression>();
        bindArgs.add(navFunction.asFunctionPointer());
        bindArgs.add(Boost.bind_1);

        if (navigation.getSkeleton() != null) {
            final List<? extends org.xtuml.masl.metamodel.expression.Expression> maslParams = navigation.getArguments();

            final List<Expression> findArgs = new ArrayList<Expression>(maslParams.size());

            for (final org.xtuml.masl.metamodel.expression.Expression param : maslParams) {
                findArgs.add(createTranslator(param, scope).getReadExpression());
            }

            // If too many parameters for boost bind to cope with (not forgetting
            // the bound object), then wrap in a tuple. Note that the navigate
            // function should already be done!
            if (findArgs.size() + 1 > Boost.MAX_BIND_PARAMS) {
                bindArgs.add(BigTuple.getMakeTuple(findArgs));
            } else {
                bindArgs.addAll(findArgs);
            }
        }

        final ExpressionTranslator lhsTrans = createTranslator(navigation.getLhs(), scope);

        setReadExpression(archNavFn.asFunctionCall(lhsTrans.getReadExpression(), Boost.bind.asFunctionCall(bindArgs)));
        setWriteableExpression(getReadExpression());

    }

    NavigationTranslator(final CorrelatedNavExpression navigation, final Scope scope) {
        final boolean
                doBackwards =
                navigation.getLhs().getType().isCollection() && !navigation.getRhs().getType().isCollection();

        final boolean toMulti = doBackwards || navigation.getRhs().getType().isCollection();

        final RelationshipSpecification
                spec =
                doBackwards ? navigation.getRelationship().getReverseSpec() : navigation.getRelationship();

        final ObjectTranslator sourceObj = ObjectTranslator.getInstance(spec.getFromObject());

        final Function
                corrFunction =
                toMulti ?
                sourceObj.getRelationshipTranslator(spec).getPublicAccessors().getMultipleCorrelateFunction() :
                sourceObj.getRelationshipTranslator(spec).getPublicAccessors().getSingleCorrelateFunction();

        final Expression
                lhs =
                createTranslator(doBackwards ? navigation.getRhs() : navigation.getLhs(), scope).getReadExpression();
        final Expression
                rhs =
                createTranslator(doBackwards ? navigation.getLhs() : navigation.getRhs(), scope).getReadExpression();

        Function archNavFn = null;
        if (navigation.getType() instanceof SetType) {
            archNavFn = Architecture.correlateSet;
        } else if (navigation.getType() instanceof BagType) {
            archNavFn = Architecture.correlateBag;
        } else {
            archNavFn = Architecture.correlateInstance;
        }

        setReadExpression(archNavFn.asFunctionCall(lhs, rhs, corrFunction.asFunctionPointer()));
        setWriteableExpression(getReadExpression());
    }

}
