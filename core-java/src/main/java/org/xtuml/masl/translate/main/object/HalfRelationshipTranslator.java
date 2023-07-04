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
package org.xtuml.masl.translate.main.object;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.*;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.BigTuple;
import org.xtuml.masl.translate.main.Boost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HalfRelationshipTranslator {

    private static final Map<ObjectDeclaration, HalfRelationshipTranslator> translators = new HashMap<>();

    static HalfRelationshipTranslator getTranslator(final ObjectDeclaration object) {
        return translators.get(object);
    }

    public HalfRelationshipTranslator(final ConcreteObjectTranslator concreteObj, final ObjectDeclaration object) {
        mainObjectTranslator = concreteObj.getMainObjectTranslator();
        this.concreteObj = concreteObj;
        translators.put(object, this);
    }

    public void addRelationship(final RelationshipSpecification spec, final RelationshipSpecification assocSpec) {
        final HalfRelationshipTranslator relatedObj = getTranslator(spec.getDestinationObject());

        final TypeUsage related = new TypeUsage(relatedObj.getMainClass());
        Class relClass;

        if (assocSpec != null) {
            final HalfRelationshipTranslator assocObj = getTranslator(assocSpec.getDestinationObject());
            final TypeUsage assoc = new TypeUsage(assocObj.mainObjectTranslator.getMainClass());
            if (spec.getCardinality() == MultiplicityType.MANY) {
                relClass = getToManyAssocClass(related, assoc);
            } else {
                relClass = getToOneAssocClass(related, assoc);
            }
        } else {
            if (spec.getCardinality() == MultiplicityType.MANY) {
                relClass = getToManyRelClass(related);
            } else {
                relClass = getToOneRelClass(related);
            }

        }

        final String
                relName =
                spec.getRelationship().getName() +
                "_" +
                (spec.getRole() == null ? "" : spec.getRole() + "_") +
                spec.getDestinationObject().getName();

        final Variable
                relAtt =
                getMainClass().createMemberVariable(relationshipVars,
                                                    relName,
                                                    new TypeUsage(relClass),
                                                    Visibility.PRIVATE);

        relationshipAttributes.put(spec, relAtt);

        final Function getter = addRelationshipGetter(relClass, relAtt, spec, false);
        final Function constGetter = addRelationshipGetter(relClass, relAtt, spec, true);

        relationshipGetters.put(spec, getter);
        relationshipConstGetters.put(spec, constGetter);
        if (assocSpec != null) {
            relationshipGetters.put(assocSpec, getter);
            relationshipConstGetters.put(assocSpec, constGetter);
        }
    }

    Map<RelationshipSpecification, Variable> relationshipAttributes = new HashMap<>();

    public Variable getRelationshipAttribute(final RelationshipSpecification spec) {
        return relationshipAttributes.get(spec);
    }

    public void addRelationshipCorr(final RelationshipSpecification spec, final Function correlator) {
        final RelationshipDeclaration relDec = spec.getRelationship();
        if (relDec instanceof AssociativeRelationshipDeclaration assocRel) {
            // The correlate function should be just on the objects between the two
            // ends of the relationship, and not on the associative object itself.
            if (assocRel.getAssocObject() != mainObjectTranslator.getObjectDeclaration()) {
                final Function downcast = new Function("downcast");
                Class downcastClass = getTranslator(assocRel.getLeftObject()).getMainClass();
                if (assocRel.getLeftObject() == mainObjectTranslator.getObjectDeclaration()) {
                    downcastClass = getTranslator(assocRel.getRightObject()).getMainClass();
                }
                downcast.addTemplateSpecialisation(new TypeUsage(downcastClass));

                final Function relGetter = getRelationshipConstGetter(spec);
                getBodyFile().addFunctionDefinition(correlator);
                final Variable rhs = correlator.getParameters().get(0);

                final Function navFunction = new Function("correlate");
                final Statement
                        code =
                        new ReturnStatement(navFunction.asFunctionCall(relGetter.asFunctionCall(),
                                                                       false,
                                                                       downcast.asFunctionCall(rhs.asExpression(),
                                                                                               false)));
                correlator.getCode().appendStatement(code);
            }
        }
    }

    public void addRelationshipDeclarationGroups() {
        relationships = getMainClass().createDeclarationGroup("Relationship Getters");
        relationshipVars = getMainClass().createDeclarationGroup("Storage for each relationship");
    }

    public void addRelationshipLink(final RelationshipSpecification spec, final Function linker, final boolean unlink) {
        final HalfRelationshipTranslator rhsTrans = getTranslator(spec.getDestinationObject());

        final RelationshipDeclaration relDec = spec.getRelationship();

        final RelationshipSpecification reverseSpec = spec.getReverseSpec();

        final Function forwardGetter = getRelationshipGetter(spec);
        final Function reverseGetter = rhsTrans.getRelationshipGetter(reverseSpec);

        getBodyFile().addFunctionDefinition(linker);
        final Function linkFn = new Function((unlink ? "un" : "") + "link");
        final Function undoFn = new Function((unlink ? "" : "un") + "link");

        final Variable rhs = linker.getParameters().get(0);

        final Function downcast = new Function("downcast");
        downcast.addTemplateSpecialisation(new TypeUsage(rhsTrans.getMainClass()));

        final Variable
                rhs2 =
                new Variable(new TypeUsage(Architecture.objectPtr(new TypeUsage(rhsTrans.getMainClass()))),
                             "rhs2",
                             downcast.asFunctionCall(rhs.asExpression(), false));

        final Expression forwardRel = forwardGetter.asFunctionCall(getMainClass().getThis().asExpression(), true);

        final Expression reverseRel = reverseGetter.asFunctionCall(rhs2.asExpression(), true);
        final FunctionCall
                lhs =
                Architecture.objectPtr(new TypeUsage(getMainClass())).callConstructor(getMainClass().getThis().asExpression());

        if (!(relDec instanceof AssociativeRelationshipDeclaration)) {

            // <Type> rhs2 = rhs.downcast<Type>();
            linker.getCode().appendStatement(rhs2.asStatement());

            // Check that subtype link does not overwrite existing subtype
            if (!unlink && relDec instanceof SubtypeRelationshipDeclaration supRelDec) {

                final Expression
                        supObj =
                        supRelDec.getSupertype() == spec.getDestinationObject() ?
                        rhs2.asExpression() :
                        getMainClass().getThis().asExpression();
                final HalfRelationshipTranslator supTrans = getTranslator(supRelDec.getSupertype());

                Expression isLinked = null;
                for (final ObjectDeclaration subtype : supRelDec.getSubtypes()) {
                    final RelationshipSpecification subSpec = supRelDec.getSuperToSubSpec(subtype);
                    final Expression
                            linkCount =
                            new Function("count").asFunctionCall(supTrans.getRelationshipConstGetter(subSpec).asFunctionCall(
                                    supObj,
                                    true), false);
                    isLinked =
                            isLinked == null ? linkCount : new BinaryExpression(isLinked, BinaryOperator.OR, linkCount);
                }
                final ThrowStatement
                        throwStatement =
                        new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                                "Cannot link instance - relationship " + relDec.getName() + " already linked")));
                linker.getCode().appendStatement(new IfStatement(isLinked, throwStatement));
            }

            // this->getrel().link(rhs2);
            final Expression forwardLink = linkFn.asFunctionCall(forwardRel, false, rhs2.asExpression());
            final Expression forwardUndo = undoFn.asFunctionCall(forwardRel, false, rhs2.asExpression());

            linker.getCode().appendStatement(forwardLink.asStatement());

            final Expression reverseLink = linkFn.asFunctionCall(reverseRel, false, lhs);

            // rhs2->getrel().link(this);
            final CodeBlock tryBlock = new CodeBlock();
            tryBlock.appendStatement(reverseLink.asStatement());
            final CodeBlock undoBlock = new CodeBlock();
            undoBlock.appendStatement(forwardUndo.asStatement());
            undoBlock.appendStatement(new ThrowStatement());
            linker.getCode().appendStatement(new TryCatchBlock(tryBlock,
                                                               new TryCatchBlock.CatchBlock(null, undoBlock)));

        } else {
            final HalfRelationshipTranslator
                    assocTrans =
                    getTranslator(((AssociativeRelationshipDeclaration) relDec).getAssocObject());

            final Function
                    assocForwardGetter =
                    assocTrans.getRelationshipGetter(reverseSpec.getAssocSpec().getReverseSpec());
            final Function assocReverseGetter = assocTrans.getRelationshipGetter(spec.getAssocSpec().getReverseSpec());

            final Variable assoc = linker.getParameters().get(1);
            final Function assocDowncast = new Function("downcast");
            assocDowncast.addTemplateSpecialisation(new TypeUsage(assocTrans.getMainClass()));
            final Variable
                    assoc2 =
                    new Variable(new TypeUsage(Architecture.objectPtr(new TypeUsage(assocTrans.getMainClass()))),
                                 "assoc2",
                                 assocDowncast.asFunctionCall(assoc.asExpression(), false));

            // <Type> rhs2 = rhs.downcast<Type>();
            linker.getCode().appendStatement(rhs2.asStatement());

            // <Type> assoc2 = assoc.downcast<Type>();
            linker.getCode().appendStatement(assoc2.asStatement());

            final Expression forwardAssocRel = assocForwardGetter.asFunctionCall(assoc2.asExpression(), true);
            final Expression reverseAssocRel = assocReverseGetter.asFunctionCall(assoc2.asExpression(), true);

            final Expression
                    forwardLink =
                    linkFn.asFunctionCall(forwardRel, false, rhs2.asExpression(), assoc2.asExpression());
            final Expression reverseLink = linkFn.asFunctionCall(reverseRel, false, lhs, assoc2.asExpression());
            final Expression forwardAssocLink = linkFn.asFunctionCall(forwardAssocRel, false, rhs2.asExpression());
            final Expression reverseAssocLink = linkFn.asFunctionCall(reverseAssocRel, false, lhs);

            final Expression
                    forwardUndo =
                    undoFn.asFunctionCall(forwardRel, false, rhs2.asExpression(), assoc2.asExpression());
            final Expression reverseUndo = undoFn.asFunctionCall(reverseRel, false, lhs, assoc2.asExpression());
            final Expression forwardAssocUndo = undoFn.asFunctionCall(forwardAssocRel, false, rhs2.asExpression());

            CodeBlock parent = linker.getCode();
            parent.appendStatement(forwardLink.asStatement());

            CodeBlock tryBlock = new CodeBlock();
            tryBlock.appendStatement(reverseLink.asStatement());
            CodeBlock undoBlock = new CodeBlock();
            undoBlock.appendStatement(forwardUndo.asStatement());
            undoBlock.appendStatement(new ThrowStatement());
            parent.appendStatement(new TryCatchBlock(tryBlock, new TryCatchBlock.CatchBlock(null, undoBlock)));

            parent = tryBlock;

            tryBlock = new CodeBlock();
            tryBlock.appendStatement(forwardAssocLink.asStatement());
            undoBlock = new CodeBlock();
            undoBlock.appendStatement(reverseUndo.asStatement());
            undoBlock.appendStatement(new ThrowStatement());
            parent.appendStatement(new TryCatchBlock(tryBlock, new TryCatchBlock.CatchBlock(null, undoBlock)));

            parent = tryBlock;

            tryBlock = new CodeBlock();
            tryBlock.appendStatement(reverseAssocLink.asStatement());
            undoBlock = new CodeBlock();
            undoBlock.appendStatement(forwardAssocUndo.asStatement());
            undoBlock.appendStatement(new ThrowStatement());
            parent.appendStatement(new TryCatchBlock(tryBlock, new TryCatchBlock.CatchBlock(null, undoBlock)));

        }

    }

    public void addRelationshipNav(final RelationshipSpecification spec, final Function navigator) {
        final Function relGetter = getRelationshipConstGetter(spec);

        getBodyFile().addFunctionDefinition(navigator);

        final Function navFunction = new Function(spec.isToAssociative() ? "navigateAssociative" : "navigate");

        final HalfRelationshipTranslator relatedObj = getTranslator(spec.getDestinationObject());
        final TypeUsage related = new TypeUsage(relatedObj.getMainClass());
        navFunction.setReturnType(related);

        final Statement code = new ReturnStatement(navFunction.asFunctionCall(relGetter.asFunctionCall(), false));
        navigator.getCode().appendStatement(code);
    }

    public void addRelationshipNav(final RelationshipSpecification spec,
                                   final Function navigator,
                                   final org.xtuml.masl.metamodel.expression.Expression predicate) {
        final Function relGetter = getRelationshipConstGetter(spec);

        getBodyFile().addFunctionDefinition(navigator);

        final Function navFunction = new Function(spec.isToAssociative() ? "navigateAssociative" : "navigate");

        final HalfRelationshipTranslator relatedObj = getTranslator(spec.getDestinationObject());
        final ObjectTranslator destObjTranslator = ObjectTranslator.getInstance(spec.getDestinationObject());
        final TypeUsage related = new TypeUsage(relatedObj.getMainClass());
        navFunction.setReturnType(related);

        final List<Expression> findArgs = new ArrayList<>(navigator.getParameters().size());
        for (final Variable param : navigator.getParameters()) {
            findArgs.add(param.asExpression());
        }

        final Function predicateFn = destObjTranslator.getFindPredicate(predicate);

        final List<org.xtuml.masl.cppgen.Expression> bindArgs = new ArrayList<>();
        bindArgs.add(predicateFn.asFunctionPointer());
        bindArgs.add(Boost.bind_1);

        // If too many parameters for boost bind to cope with (not forgetting
        // the bound object), then wrap in a tuple. Note that the predicate
        // function should already be done!
        if (findArgs.size() + 1 > Boost.MAX_BIND_PARAMS) {
            bindArgs.add(BigTuple.getMakeTuple(findArgs));
        } else {
            bindArgs.addAll(findArgs);
        }

        final Expression predicateArg = Boost.bind.asFunctionCall(bindArgs);

        final Statement
                code =
                new ReturnStatement(navFunction.asFunctionCall(relGetter.asFunctionCall(), false, predicateArg));
        navigator.getCode().appendStatement(code);
    }

    public void addRelationshipCount(final RelationshipSpecification spec, final Function counter) {
        final Function relGetter = getRelationshipConstGetter(spec);

        getBodyFile().addFunctionDefinition(counter);

        final Function countFunction = new Function("count");
        final Statement code = new ReturnStatement(countFunction.asFunctionCall(relGetter.asFunctionCall(), false));
        counter.getCode().appendStatement(code);
    }

    public CodeFile getBodyFile() {
        return concreteObj.getBodyFile();
    }

    public Class getMainClass() {
        return concreteObj.getMainClass();
    }

    public Function getRelationshipConstGetter(final RelationshipSpecification spec) {
        return relationshipConstGetters.get(spec);
    }

    public Function getRelationshipGetter(final RelationshipSpecification spec) {
        return relationshipGetters.get(spec);
    }

    protected abstract Class getToManyAssocClass(TypeUsage related, TypeUsage assoc);

    protected abstract Class getToManyRelClass(TypeUsage related);

    protected abstract Class getToOneAssocClass(TypeUsage related, TypeUsage assoc);

    protected abstract Class getToOneRelClass(TypeUsage related);

    Function addRelationshipGetter(final Class relClass,
                                   final Variable relAtt,
                                   final RelationshipSpecification spec,
                                   final boolean constant) {
        final String getterName = "get_" + relAtt.getName();

        final Function getter = getMainClass().createMemberFunction(relationships, getterName, Visibility.PUBLIC);
        getter.setReturnType(new TypeUsage(relClass, constant ? TypeUsage.ConstReference : TypeUsage.Reference));
        getter.setConst(constant);

        getter.getCode().appendStatement(new ReturnStatement(relAtt.asExpression()));
        getBodyFile().addFunctionDefinition(getter);

        return getter;
    }

    private final ConcreteObjectTranslator concreteObj;
    private final org.xtuml.masl.translate.main.object.ObjectTranslator mainObjectTranslator;
    private final Map<RelationshipSpecification, Function> relationshipConstGetters = new HashMap<>();

    private final Map<RelationshipSpecification, Function> relationshipGetters = new HashMap<>();

    private DeclarationGroup relationships;
    private DeclarationGroup relationshipVars;

}
