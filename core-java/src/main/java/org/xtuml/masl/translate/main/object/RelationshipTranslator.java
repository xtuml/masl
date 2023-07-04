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

import org.xtuml.masl.CommandLine;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.expression.FindParameterExpression;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.MultiplicityType;
import org.xtuml.masl.metamodel.relationship.RelationshipSpecification;
import org.xtuml.masl.metamodel.type.TypeDefinition.ActualType;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.DomainTranslator;
import org.xtuml.masl.translate.main.Types;
import org.xtuml.masl.translate.main.expression.PredicateNameMangler;

import java.util.*;

public class RelationshipTranslator {

    public class PublicAccessors {

        private PublicAccessors() {
        }

        public Function getAllUnlinkFunction() {
            return allUnlinker;
        }

        public Function getCountFunction() {
            return counter;
        }

        public Function getDeducedAssocAllUnlinkFunction() {
            return deducedAssocAllUnlinker;
        }

        public Function getDeducedAssocMultipleLinkFunction() {
            return checkedDeducedAssocMultipleLinker;
        }

        public Function getDeducedAssocMultipleUnlinkFunction() {
            return deducedAssocMultipleUnlinker;
        }

        public Function getDeducedAssocSingleLinkFunction() {
            return checkedDeducedAssocSingleLinker;
        }

        public Function getDeducedAssocSingleUnlinkFunction() {
            return deducedAssocSingleUnlinker;
        }

        public Function getMultipleCorrelateFunction() {
            return multipleCorrelator;
        }

        public Function getMultipleLinkFunction() {
            return checkedMultipleLinker;
        }

        public Function getMultipleUnlinkFunction() {
            return multipleUnlinker;
        }

        public Function getNavigateFunction() {
            return navigator;
        }

        public Function getNavigateFunction(final org.xtuml.masl.metamodel.expression.Expression predicate) {
            return RelationshipTranslator.this.getNavigateFunction(predicate);
        }

        public Function getSingleCorrelateFunction() {
            return singleCorrelator;
        }

        public Function getSingleLinkFunction() {
            return checkedSingleLinker;
        }

        public Function getSingleUnlinkFunction() {
            return singleUnlinker;
        }

    }

    public class SubclassOverrides {

        private SubclassOverrides() {
        }

        public Function getAllUnlinkFunction() {
            return allUnlinker;
        }

        public Function getCountFunction() {
            return counter;
        }

        public Function getDeducedAssocAllUnlinkFunction() {
            return deducedAssocAllUnlinker;
        }

        public Function getDeducedAssocMultipleLinkFunction() {
            return deducedAssocMultipleLinker;
        }

        public Function getDeducedAssocMultipleUnlinkFunction() {
            return deducedAssocMultipleUnlinker;
        }

        public Function getDeducedAssocSingleLinkFunction() {
            return deducedAssocSingleLinker;
        }

        public Function getDeducedAssocSingleUnlinkFunction() {
            return deducedAssocSingleUnlinker;
        }

        public Function getMultipleCorrelateFunction() {
            return multipleCorrelator;
        }

        public Function getMultipleLinkFunction() {
            return multipleLinker;
        }

        public Function getMultipleUnlinkFunction() {
            return multipleUnlinker;
        }

        public Function getNavigateFunction() {
            return navigator;
        }

        public Function getNavigateFunction(final org.xtuml.masl.metamodel.expression.Expression predicate) {
            return RelationshipTranslator.this.getNavigateFunction(predicate);
        }

        public Set<org.xtuml.masl.metamodel.expression.Expression> getConditionalNavigatePredicates() {
            return conditionalNavigators.keySet();
        }

        public Function getSingleCorrelateFunction() {
            return singleCorrelator;
        }

        public Function getSingleLinkFunction() {
            return singleLinker;
        }

        public Function getSingleUnlinkFunction() {
            return singleUnlinker;
        }

    }

    private final DomainTranslator domainTranslator;

    RelationshipTranslator(final ObjectTranslator objectTranslator, final RelationshipSpecification relSpec) {
        this.spec = relSpec;
        this.mainClass = objectTranslator.getMainClass();
        this.bodyFile = objectTranslator.getMain().getBodyFile();
        this.domainTranslator = objectTranslator.getDomainTranslator();

        specName =
                relSpec.getRelationship().getName() +
                "_" +
                (relSpec.getRole() == null ? "" : relSpec.getRole() + "_") +
                relSpec.getDestinationObject().getName();

        group = mainClass.createDeclarationGroup("Relationship " + relSpec);

        rhsType = ObjectTranslator.getInstance(relSpec.getDestinationObject()).getPointerType();
        rhsBagType = Architecture.bag(rhsType);
        rhsSetType = Architecture.set(rhsType);

        assocType =
                relSpec.getAssocSpec() == null ?
                null :
                ObjectTranslator.getInstance(relSpec.getAssocSpec().getDestinationObject()).getPointerType();
        assocSetType = relSpec.getAssocSpec() == null ? null : Architecture.set(assocType);
        assocBagType = relSpec.getAssocSpec() == null ? null : Architecture.bag(assocType);

        addNavigator();
        addCounter();
        addCorrelators();

        addLinkers();
        if (CommandLine.INSTANCE.isForTest() || domainTranslator.addRefIntegChecks()) {
            addCheckedLinkers();
        }
        addUnlinkers();

    }

    public Function getNavigateFunction(final org.xtuml.masl.metamodel.expression.Expression predicate) {
        if (predicate == null) {
            return navigator;
        }
        Function conditionalNavigator = conditionalNavigators.get(predicate);
        if (conditionalNavigator == null) {
            conditionalNavigator = addConditionalNavigator(predicate);
            conditionalNavigators.put(predicate, conditionalNavigator);
        }
        return conditionalNavigator;
    }

    public PublicAccessors getPublicAccessors() {
        return publicAccessors;
    }

    public SubclassOverrides getSubclassOverrides() {
        return subclassOverrides;
    }

    void translate() {
        setCounterCode();
        if (CommandLine.INSTANCE.isForTest() || domainTranslator.addRefIntegChecks()) {
            setCheckedSingleLinkerCode();
            setCheckedMultipleLinkerCode();
            setCheckedDeducedAssocSingleLinkerCode();
            setCheckedDeducedAssocMultipleLinkerCode();
        }

        setMultiForwarderCode(multipleLinker, singleLinker);
        setMultiForwarderCode(multipleUnlinker, singleUnlinker);
        setAllUnlinkerCode();

        setAssocMultiForwarderCode(multipleCorrelator, singleCorrelator);

        setDeducedAssocSingleLinkerCode();
        setAssocMultiForwarderCode(deducedAssocMultipleLinker, deducedAssocSingleLinker);

        setDeducedAssocSingleUnlinkerCode();
        setAssocMultiForwarderCode(deducedAssocMultipleUnlinker, deducedAssocSingleUnlinker);
        setDeducedAssocAllUnlinkerCode();

    }

    private void addCorrelators() {
        if (assocType != null) {
            singleCorrelator = mainClass.createMemberFunction(group, "correlate_" + specName, Visibility.PUBLIC);
            singleCorrelator.setConst(true);
            singleCorrelator.setPure(true);
            singleCorrelator.createParameter(rhsType.getOptimalParameterType(), "rhs");
            singleCorrelator.setReturnType(assocType);

            multipleCorrelator = mainClass.createMemberFunction(group, "correlate_" + specName, Visibility.PUBLIC);
            multipleCorrelator.setConst(true);
            multipleCorrelator.setPure(true);
            multipleCorrelator.createParameter(new TypeUsage(rhsBagType, TypeUsage.ConstReference), "rhs");
            multipleCorrelator.setReturnType(new TypeUsage(assocBagType));

        }
    }

    private void addCounter() {
        counter = mainClass.createMemberFunction(group, "count_" + specName, Visibility.PUBLIC);
        counter.setConst(true);
        counter.setPure(true);
        counter.setReturnType(new TypeUsage(Std.size_t));
    }

    private void addLinkers() {
        if (!spec.isToAssociative() && !spec.isFromAssociative()) {
            singleLinker = mainClass.createMemberFunction(group, "link_" + specName, Visibility.PUBLIC);
            singleLinker.setPure(true);
            singleLinker.createParameter(rhsType.getOptimalParameterType(), "rhs");
            checkedSingleLinker = singleLinker;

            if (assocType == null) {
                if (spec.getCardinality() == MultiplicityType.MANY) {
                    multipleLinker = mainClass.createMemberFunction(group, "link_" + specName, Visibility.PUBLIC);
                    multipleLinker.setPure(true);
                    multipleLinker.createParameter(new TypeUsage(rhsBagType, TypeUsage.ConstReference), "rhs");
                    checkedMultipleLinker = multipleLinker;
                }
            } else {
                singleLinker.createParameter(assocType.getOptimalParameterType(), "assoc");

                if (canDeduceAssociative()) {
                    deducedAssocSingleLinker =
                            mainClass.createMemberFunction(group, "link_" + specName, Visibility.PUBLIC);
                    deducedAssocSingleLinker.setPure(true);
                    deducedAssocSingleLinker.createParameter(rhsType.getOptimalParameterType(), "rhs");
                    deducedAssocSingleLinker.setReturnType(assocType);
                    checkedDeducedAssocSingleLinker = deducedAssocSingleLinker;

                    if (spec.getCardinality() == MultiplicityType.MANY) {
                        deducedAssocMultipleLinker =
                                mainClass.createMemberFunction(group, "link_" + specName, Visibility.PUBLIC);
                        deducedAssocMultipleLinker.setPure(true);
                        deducedAssocMultipleLinker.createParameter(new TypeUsage(rhsBagType, TypeUsage.ConstReference),
                                                                   "rhs");
                        deducedAssocMultipleLinker.setReturnType(new TypeUsage(assocSetType));
                        checkedDeducedAssocMultipleLinker = deducedAssocMultipleLinker;
                    }
                }

            }

        }
    }

    private void addCheckedLinkers() {
        if (!spec.isToAssociative() && !spec.isFromAssociative()) {
            if (needsCheckedVersions()) {
                checkedSingleLinker =
                        mainClass.createMemberFunction(group, "checked_link_" + specName, Visibility.PUBLIC);
                checkedSingleLinker.createParameter(rhsType.getOptimalParameterType(), "rhs");

                if (assocType == null) {
                    if (spec.getCardinality() == MultiplicityType.MANY) {
                        checkedMultipleLinker =
                                mainClass.createMemberFunction(group, "checked_link_" + specName, Visibility.PUBLIC);
                        checkedMultipleLinker.createParameter(new TypeUsage(rhsBagType, TypeUsage.ConstReference),
                                                              "rhs");
                    }
                } else {
                    checkedSingleLinker.createParameter(assocType.getOptimalParameterType(), "assoc");

                    if (needsCheckedDeducedAssocVersions()) {
                        checkedDeducedAssocSingleLinker =
                                mainClass.createMemberFunction(group, "checked_link_" + specName, Visibility.PUBLIC);
                        checkedDeducedAssocSingleLinker.createParameter(rhsType.getOptimalParameterType(), "rhs");
                        checkedDeducedAssocSingleLinker.setReturnType(assocType);

                        if (spec.getCardinality() == MultiplicityType.MANY) {
                            checkedDeducedAssocMultipleLinker =
                                    mainClass.createMemberFunction(group,
                                                                   "checked_link_" + specName,
                                                                   Visibility.PUBLIC);
                            checkedDeducedAssocMultipleLinker.createParameter(new TypeUsage(rhsBagType,
                                                                                            TypeUsage.ConstReference),
                                                                              "rhs");
                            checkedDeducedAssocMultipleLinker.setReturnType(new TypeUsage(assocSetType));
                        }
                    }
                }
            }
        }
    }

    private void addNavigator() {
        navigator = mainClass.createMemberFunction(group, "navigate_" + specName, Visibility.PUBLIC);
        navigator.setConst(true);
        navigator.setPure(true);
        navigator.setReturnType(spec.getCardinality() == MultiplicityType.ONE ? rhsType : new TypeUsage(rhsSetType));
    }

    private Function addConditionalNavigator(final org.xtuml.masl.metamodel.expression.Expression predicate) {
        final Function
                condNavigator =
                mainClass.createMemberFunction(group,
                                               "navigate_" +
                                               specName +
                                               "_" +
                                               PredicateNameMangler.createMangler(predicate).getName(),
                                               Visibility.PUBLIC);
        condNavigator.setConst(true);
        condNavigator.setVirtual(true);
        final TypeUsage
                resultType =
                spec.getCardinality() == MultiplicityType.ONE ? rhsType : new TypeUsage(rhsSetType);

        condNavigator.setReturnType(resultType);

        final List<Expression> findArgs = new ArrayList<>(condNavigator.getParameters().size());
        for (final FindParameterExpression maslParam : predicate.getFindParameters()) {
            findArgs.add(condNavigator.createParameter(domainTranslator.getTypes().getType(maslParam.getType()).getOptimalParameterType(),
                                                       maslParam.getName()).asExpression());
        }

        bodyFile.addFunctionDefinition(condNavigator);

        final ObjectTranslator destObjTranslator = ObjectTranslator.getInstance(spec.getDestinationObject());
        final Function predicateFn = destObjTranslator.getFindPredicate(predicate);

        if (spec.getCardinality() == MultiplicityType.ONE) {
            final Variable fullNav = new Variable(resultType, "fullNav", navigator.asFunctionCall());
            condNavigator.getCode().appendStatement(fullNav.asStatement());

            final Expression
                    valid =
                    new BinaryExpression(fullNav.asExpression(),
                                         BinaryOperator.AND,
                                         predicateFn.asFunctionCall(fullNav.asExpression(), true, findArgs));
            final Expression
                    result =
                    new ConditionalExpression(valid, fullNav.asExpression(), resultType.getType().callConstructor());
            condNavigator.getCode().appendStatement(new ReturnStatement(result));
        } else {
            final Expression predicateArg = destObjTranslator.getBoundPredicate(predicate, findArgs);
            condNavigator.getCode().appendStatement(new ReturnStatement(new Function("find").asFunctionCall(navigator.asFunctionCall(),
                                                                                                            false,
                                                                                                            predicateArg)));
        }

        return condNavigator;
    }

    private void addUnlinkers() {
        if (!spec.isToAssociative() && !spec.isFromAssociative()) {
            singleUnlinker = mainClass.createMemberFunction(group, "unlink_" + specName, Visibility.PUBLIC);
            singleUnlinker.setPure(true);
            singleUnlinker.createParameter(rhsType.getOptimalParameterType(), "rhs");

            if (assocType == null) {
                if (spec.getCardinality() == MultiplicityType.MANY) {
                    multipleUnlinker = mainClass.createMemberFunction(group, "unlink_" + specName, Visibility.PUBLIC);
                    multipleUnlinker.setPure(true);
                    multipleUnlinker.createParameter(new TypeUsage(rhsBagType, TypeUsage.ConstReference), "rhs");
                }

                allUnlinker = mainClass.createMemberFunction(group, "unlink_" + specName, Visibility.PUBLIC);
                allUnlinker.setPure(true);

            } else {
                singleUnlinker.createParameter(assocType.getOptimalParameterType(), "assoc");

                deducedAssocSingleUnlinker =
                        mainClass.createMemberFunction(group, "unlink_" + specName, Visibility.PUBLIC);
                deducedAssocSingleUnlinker.setPure(true);
                deducedAssocSingleUnlinker.createParameter(rhsType.getOptimalParameterType(), "rhs");
                deducedAssocSingleUnlinker.setReturnType(assocType);

                if (spec.getCardinality() == MultiplicityType.MANY) {
                    deducedAssocMultipleUnlinker =
                            mainClass.createMemberFunction(group, "unlink_" + specName, Visibility.PUBLIC);
                    deducedAssocMultipleUnlinker.setPure(true);
                    deducedAssocMultipleUnlinker.createParameter(new TypeUsage(rhsBagType, TypeUsage.ConstReference),
                                                                 "rhs");
                    deducedAssocMultipleUnlinker.setReturnType(new TypeUsage(assocSetType));
                }

                deducedAssocAllUnlinker =
                        mainClass.createMemberFunction(group, "unlink_" + specName, Visibility.PUBLIC);
                deducedAssocAllUnlinker.setPure(true);
                deducedAssocAllUnlinker.setReturnType(spec.getCardinality() == MultiplicityType.MANY ?
                                                      new TypeUsage(assocSetType) :
                                                      assocType);
            }

        }
    }

    private Statement getCheck(final Expression condition) {
        final CodeBlock action = new CodeBlock();
        final Statement
                throwError =
                new ThrowStatement(Architecture.programError.callConstructor(Literal.createStringLiteral(
                        "referential integrity failure")));
        action.appendStatement(throwError);

        return new IfStatement(condition, action);
    }

    private Statement getRefIntegChecks(final Expression rhs) {
        final ObjectTranslator lhsTranslator = ObjectTranslator.getInstance(spec.getFromObject());
        final ObjectTranslator rhsTranslator = ObjectTranslator.getInstance(spec.getDestinationObject());

        if (spec.isFormalisingEnd()) {
            return getCheck(getIntegrityCheck(null, lhsTranslator, spec, rhsTranslator, rhs));
        } else {
            return getCheck(getIntegrityCheck(rhs, rhsTranslator, spec.getReverseSpec(), lhsTranslator, null));
        }
    }

    private Statement getAssocRefIntegChecks(final Expression rhs, final Expression assoc) {
        final ObjectTranslator lhsTranslator = ObjectTranslator.getInstance(spec.getFromObject());
        final ObjectTranslator rhsTranslator = ObjectTranslator.getInstance(spec.getDestinationObject());
        final ObjectTranslator
                assocTranslator =
                ObjectTranslator.getInstance(spec.getAssocSpec().getDestinationObject());

        final Expression
                lhsCheck =
                getIntegrityCheck(assoc, assocTranslator, spec.getAssocSpec().getReverseSpec(), lhsTranslator, null);
        final Expression
                rhsCheck =
                getIntegrityCheck(assoc,
                                  assocTranslator,
                                  spec.getReverseSpec().getAssocSpec().getReverseSpec(),
                                  rhsTranslator,
                                  rhs);

        final StatementGroup result = new StatementGroup();

        if (lhsCheck != null) {
            result.appendStatement(getCheck(lhsCheck));
        }
        if (rhsCheck != null) {
            result.appendStatement(getCheck(rhsCheck));
        }

        return result;

    }

    private Statement getDeducedAssocRefIntegChecks(final Expression rhs) {
        return getCheck(getDeducedAssocIntegrityCheck(rhs));
    }

    private Expression getDeducedAssocIntegrityCheck(final Expression rhs) {
        Expression check = null;
        final ObjectTranslator lhsTranslator = ObjectTranslator.getInstance(spec.getFromObject());
        final ObjectTranslator rhsTranslator = ObjectTranslator.getInstance(spec.getDestinationObject());

        // Check that all of the collapsed associative referential attributes are
        // equal on both objects to be linked.
        for (final AttributeDeclaration att : spec.getAssocSpec().getDestinationObject().getAttributes()) {
            if (att.getRefAttDefs().size() > 1) {
                ReferentialAttributeDefinition lhsRefAtt = null;
                ReferentialAttributeDefinition rhsRefAtt = null;
                for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                    if (refAtt.getRelationship() == spec.getAssocSpec().getReverseSpec()) {
                        lhsRefAtt = refAtt;
                    } else if (refAtt.getRelationship() == spec.getReverseSpec().getAssocSpec().getReverseSpec()) {
                        rhsRefAtt = refAtt;
                    }
                }
                if (lhsRefAtt != null && rhsRefAtt != null) {
                    final Expression
                            lhsAtt =
                            lhsTranslator.getAttributeGetter(lhsRefAtt.getDestinationAttribute()).asFunctionCall();
                    final Expression
                            rhsAtt =
                            rhsTranslator.getAttributeGetter(rhsRefAtt.getDestinationAttribute()).asFunctionCall(rhs,
                                                                                                                 true);

                    final Expression newCheck = new BinaryExpression(lhsAtt, BinaryOperator.NOT_EQUAL, rhsAtt);

                    if (check == null) {
                        check = newCheck;
                    } else {
                        check = new BinaryExpression(check, BinaryOperator.OR, newCheck);
                    }
                }
            }
        }

        return check;

    }

    private static Expression getIntegrityCheck(final Expression from,
                                                final ObjectTranslator fromTranslator,
                                                final RelationshipSpecification relSpec,
                                                final ObjectTranslator destTranslator,
                                                final Expression dest) {
        Expression check = null;

        for (final AttributeDeclaration att : relSpec.getFromObject().getAttributes()) {
            if (att.isIdentifier() || att.getRefAttDefs().size() > 1) {
                ReferentialAttributeDefinition thisRefAtt = null;
                for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                    if (refAtt.getRelationship() == relSpec) {
                        thisRefAtt = refAtt;
                    }
                }

                if (thisRefAtt != null) {
                    final Expression
                            fromAtt =
                            from == null ?
                            fromTranslator.getAttributeGetter(att).asFunctionCall() :
                            fromTranslator.getAttributeGetter(att).asFunctionCall(from, true);

                    final Expression
                            destAtt =
                            dest == null ?
                            destTranslator.getAttributeGetter(thisRefAtt.getDestinationAttribute()).asFunctionCall() :
                            destTranslator.getAttributeGetter(thisRefAtt.getDestinationAttribute()).asFunctionCall(dest,
                                                                                                                   true);

                    if (att.isIdentifier()) {
                        // It's an identifier, so ref integ against attributes in other
                        // objects will have been checked when they were linked. Just need
                        // to check that the value we have is the same as in the dest
                        // object.
                        final Expression newCheck = new BinaryExpression(fromAtt, BinaryOperator.NOT_EQUAL, destAtt);

                        if (check == null) {
                            check = newCheck;
                        } else {
                            check = new BinaryExpression(check, BinaryOperator.OR, newCheck);
                        }
                    } else {
                        // Not an identifer, so need to check that value in the destination
                        // object matches the one that we have from any other objects that
                        // are linked via this collapsed referential atttribute. Need to
                        // check that at least one of the links is valid before trying the
                        // read.

                        Expression validCheck = null;
                        for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                            if (refAtt.getRelationship() != relSpec) {
                                final Function
                                        nav =
                                        fromTranslator.getRelationshipTranslator(refAtt.getRelationship()).getPublicAccessors().getNavigateFunction();
                                final Expression
                                        thatInstance =
                                        from == null ? nav.asFunctionCall() : nav.asFunctionCall(from, true);
                                if (validCheck == null) {
                                    validCheck = thatInstance;
                                } else {
                                    validCheck = new BinaryExpression(validCheck, BinaryOperator.OR, thatInstance);
                                }
                            }
                        }

                        final Expression
                                newCheck =
                                new BinaryExpression(validCheck,
                                                     BinaryOperator.AND,
                                                     new BinaryExpression(fromAtt, BinaryOperator.NOT_EQUAL, destAtt));

                        if (check == null) {
                            check = newCheck;
                        } else {
                            check = new BinaryExpression(check, BinaryOperator.OR, newCheck);
                        }
                    }
                }
            }
        }
        return check;
    }

    static private boolean needsCheck(final RelationshipSpecification relSpec) {
        // Check if any identifier attributes or collapsed referentials are
        // referential for this relationship
        for (final AttributeDeclaration att : relSpec.getFromObject().getAttributes()) {
            if (att.isIdentifier() || att.getRefAttDefs().size() > 1) {
                for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                    if (refAtt.getRelationship() == relSpec) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean needsCheckedVersions() {
        if (assocType == null) {
            if (spec.isFormalisingEnd()) {
                return needsCheck(spec);
            } else {
                return needsCheck(spec.getReverseSpec());
            }
        } else {
            return needsCheck(spec.getAssocSpec().getReverseSpec()) ||
                   needsCheck(spec.getReverseSpec().getAssocSpec().getReverseSpec());
        }
    }

    private boolean canDeduceAssociative() {
        if (assocType != null) {
            if (spec.getAssocSpec().getDestinationObject().hasCurrentState()) {
                return false;
            }

            // Check whether any of the associative referential attributes are
            // collapsed on this relationship
            for (final AttributeDeclaration att : spec.getAssocSpec().getDestinationObject().getAttributes()) {
                if (att.isIdentifier() && !att.isUnique()) {
                    boolean ok = false;
                    for (final ReferentialAttributeDefinition refAttDef : att.getRefAttDefs()) {
                        if (refAttDef.getDestinationAttribute().getParentObject() == spec.getDestinationObject() ||
                            refAttDef.getDestinationAttribute().getParentObject() == spec.getFromObject()) {
                            ok = true;
                            break;
                        }
                    }
                    if (!ok) {
                        return false;
                    }
                }
            }
            return true;
        }

        return false;
    }

    private boolean needsCheckedDeducedAssocVersions() {
        if (canDeduceAssociative()) {
            // Check whether any of the associative referential attributes are
            // collapsed on this relationship
            for (final AttributeDeclaration att : spec.getAssocSpec().getDestinationObject().getAttributes()) {
                if (att.getRefAttDefs().size() > 1) {
                    boolean lhsMatch = false;
                    boolean rhsMatch = false;
                    for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                        if (refAtt.getRelationship() == spec.getAssocSpec().getReverseSpec()) {
                            lhsMatch = true;
                        } else if (refAtt.getRelationship() == spec.getReverseSpec().getAssocSpec().getReverseSpec()) {
                            rhsMatch = true;
                        }
                    }
                    if (lhsMatch && rhsMatch) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void setAllUnlinkerCode() {
        if (allUnlinker != null) {
            allUnlinker.setPure(false);
            bodyFile.addFunctionDefinition(allUnlinker);

            if (multipleUnlinker == null) {
                // Need to check for null for single instance navigate
                final Variable rhs = new Variable(rhsType.getConstReferenceType(), "rhs", navigator.asFunctionCall());
                allUnlinker.getCode().appendStatement(rhs.asStatement());
                allUnlinker.getCode().appendStatement(new IfStatement(rhs.asExpression(),
                                                                      singleUnlinker.asFunctionCall(rhs.asExpression()).asStatement()));
            } else {
                allUnlinker.getCode().appendStatement(multipleUnlinker.asFunctionCall(navigator.asFunctionCall()).asStatement());
            }
        }
    }

    private void setDeducedAssocAllUnlinkerCode() {
        if (deducedAssocAllUnlinker != null) {
            deducedAssocAllUnlinker.setPure(false);
            bodyFile.addFunctionDefinition(deducedAssocAllUnlinker);
            if (deducedAssocMultipleUnlinker == null) {
                // Need to check for null for single instance navigate
                final Variable rhs = new Variable(rhsType.getConstReferenceType(), "rhs", navigator.asFunctionCall());
                deducedAssocAllUnlinker.getCode().appendStatement(rhs.asStatement());
                deducedAssocAllUnlinker.getCode().appendStatement(new IfStatement(rhs.asExpression(),
                                                                                  new ReturnStatement(
                                                                                          deducedAssocSingleUnlinker.asFunctionCall(
                                                                                                  rhs.asExpression())),
                                                                                  new ReturnStatement(Architecture.nullPointer)));
            } else {
                deducedAssocAllUnlinker.getCode().appendStatement(new ReturnStatement(deducedAssocMultipleUnlinker.asFunctionCall(
                        navigator.asFunctionCall())));
            }

        }
    }

    private void setAssocMultiForwarderCode(final Function multiFunction, final Function singleFunction) {
        if (multiFunction != null) {
            multiFunction.setPure(false);
            bodyFile.addFunctionDefinition(multiFunction);

            // <assocSetType> result;
            // result.reserve(rhs.size());
            // for ( <destSetType>::const_iterator it = rhs.begin(); it != rhs.end();
            // ++it )
            // {
            // result += singleFunction(*it);
            // }
            // return result;

            final Variable resultVar = new Variable(multiFunction.getReturnType(), "result");

            final TypeUsage iteratorType = new TypeUsage(rhsBagType.referenceNestedType("const_iterator"));

            final Expression rhs = multiFunction.getParameters().get(0).asExpression();

            final Variable it = new Variable(iteratorType, "it", new Function("begin").asFunctionCall(rhs, false));
            final Expression
                    endCondition =
                    new BinaryExpression(it.asExpression(),
                                         BinaryOperator.NOT_EQUAL,
                                         new Function("end").asFunctionCall(rhs, false));
            final CodeBlock addToResult = new CodeBlock();

            final ForStatement
                    calcResult =
                    new ForStatement(it.asStatement(),
                                     endCondition,
                                     new UnaryExpression(UnaryOperator.PREINCREMENT, it.asExpression()),
                                     addToResult);

            addToResult.appendStatement(new BinaryExpression(resultVar.asExpression(),
                                                             BinaryOperator.PLUS_ASSIGN,
                                                             singleFunction.asFunctionCall(new UnaryExpression(
                                                                     UnaryOperator.DEREFERENCE,
                                                                     it.asExpression()))).asStatement());

            final StatementGroup result = new StatementGroup();
            result.appendStatement(resultVar.asStatement());
            result.appendStatement(new Function("reserve").asFunctionCall(resultVar.asExpression(),
                                                                          false,
                                                                          new Function("size").asFunctionCall(rhs,
                                                                                                              false)).asStatement());
            result.appendStatement(calcResult);
            result.appendStatement(new Function("forceUnique").asFunctionCall(resultVar.asExpression(),
                                                                              false).asStatement());
            result.appendStatement(new ReturnStatement(resultVar.asExpression()));

            multiFunction.getCode().appendStatement(result);

        }
    }

    private void setCheckedMultipleLinkerCode() {
        if (needsCheckedVersions() && checkedMultipleLinker != null) {
            bodyFile.addFunctionDefinition(checkedMultipleLinker);
            checkedMultipleLinker.setPure(false);

            final TypeUsage iteratorType = new TypeUsage(rhsBagType.referenceNestedType("const_iterator"));

            final Expression rhs = checkedMultipleLinker.getParameters().get(0).asExpression();

            final Variable it = new Variable(iteratorType, "it", new Function("begin").asFunctionCall(rhs, false));
            final Expression
                    endCondition =
                    new BinaryExpression(it.asExpression(),
                                         BinaryOperator.NOT_EQUAL,
                                         new Function("end").asFunctionCall(rhs, false));

            final CodeBlock check = new CodeBlock();
            check.appendStatement(getRefIntegChecks(new UnaryExpression(UnaryOperator.DEREFERENCE, it.asExpression())));
            checkedMultipleLinker.getCode().appendStatement(new ForStatement(it.asStatement(),
                                                                             endCondition,
                                                                             new UnaryExpression(UnaryOperator.PREINCREMENT,
                                                                                                 it.asExpression()),
                                                                             check));

            checkedMultipleLinker.getCode().appendStatement(multipleLinker.asFunctionCall(rhs).asStatement());
        }
    }

    private void setCheckedSingleLinkerCode() {
        if (needsCheckedVersions() && checkedSingleLinker != null) {
            bodyFile.addFunctionDefinition(checkedSingleLinker);
            checkedSingleLinker.setPure(false);

            final Expression rhs = checkedSingleLinker.getParameters().get(0).asExpression();
            if (assocType == null) {
                checkedSingleLinker.getCode().appendStatement(getRefIntegChecks(rhs));
                checkedSingleLinker.getCode().appendStatement(singleLinker.asFunctionCall(rhs).asStatement());
            } else {
                final Expression assoc = checkedSingleLinker.getParameters().get(1).asExpression();
                checkedSingleLinker.getCode().appendStatement(getAssocRefIntegChecks(rhs, assoc));
                checkedSingleLinker.getCode().appendStatement(singleLinker.asFunctionCall(rhs, assoc).asStatement());

            }
        }
    }

    private void setCheckedDeducedAssocSingleLinkerCode() {
        if (needsCheckedDeducedAssocVersions() && checkedDeducedAssocSingleLinker != null) {
            bodyFile.addFunctionDefinition(checkedDeducedAssocSingleLinker);
            checkedDeducedAssocSingleLinker.setPure(false);

            final Expression rhs = checkedDeducedAssocSingleLinker.getParameters().get(0).asExpression();
            checkedDeducedAssocSingleLinker.getCode().appendStatement(getDeducedAssocRefIntegChecks(rhs));
            checkedDeducedAssocSingleLinker.getCode().appendStatement(new ReturnStatement(deducedAssocSingleLinker.asFunctionCall(
                    rhs)));
        }
    }

    private void setCheckedDeducedAssocMultipleLinkerCode() {
        if (needsCheckedDeducedAssocVersions() && checkedDeducedAssocMultipleLinker != null) {
            bodyFile.addFunctionDefinition(checkedDeducedAssocMultipleLinker);
            checkedDeducedAssocMultipleLinker.setPure(false);

            final TypeUsage iteratorType = new TypeUsage(rhsBagType.referenceNestedType("const_iterator"));

            final Expression rhs = checkedDeducedAssocMultipleLinker.getParameters().get(0).asExpression();

            final Variable it = new Variable(iteratorType, "it", new Function("begin").asFunctionCall(rhs, false));
            final Expression
                    endCondition =
                    new BinaryExpression(it.asExpression(),
                                         BinaryOperator.NOT_EQUAL,
                                         new Function("end").asFunctionCall(rhs, false));

            final CodeBlock check = new CodeBlock();
            check.appendStatement(getDeducedAssocRefIntegChecks(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                                    it.asExpression())));
            checkedDeducedAssocMultipleLinker.getCode().appendStatement(new ForStatement(it.asStatement(),
                                                                                         endCondition,
                                                                                         new UnaryExpression(
                                                                                                 UnaryOperator.PREINCREMENT,
                                                                                                 it.asExpression()),
                                                                                         check));

            checkedDeducedAssocMultipleLinker.getCode().appendStatement(new ReturnStatement(deducedAssocMultipleLinker.asFunctionCall(
                    rhs)));
        }
    }

    private void setCounterCode() {
        if (counter != null) {
            bodyFile.addFunctionDefinition(counter);
            counter.setPure(false);

            if (spec.getCardinality() == MultiplicityType.ONE) {
                counter.getCode().appendStatement(new ReturnStatement(new ConditionalExpression(new BinaryExpression(
                        navigator.asFunctionCall(),
                        BinaryOperator.EQUAL,
                        Architecture.nullPointer), Literal.ZERO, Literal.ONE)));
            } else {
                counter.getCode().appendStatement(new ReturnStatement(new Function("size").asFunctionCall(navigator.asFunctionCall(),
                                                                                                          false)));
            }
        }
    }

    private void setDeducedAssocSingleLinkerCode() {
        if (deducedAssocSingleLinker != null) {
            deducedAssocSingleLinker.setPure(false);
            bodyFile.addFunctionDefinition(deducedAssocSingleLinker);

            final ObjectTranslator lhsTranslator = ObjectTranslator.getInstance(spec.getFromObject());
            final ObjectTranslator rhsTranslator = ObjectTranslator.getInstance(spec.getDestinationObject());
            final ObjectTranslator
                    assocTranslator =
                    ObjectTranslator.getInstance(spec.getAssocSpec().getDestinationObject());

            final Expression rhs = deducedAssocSingleLinker.getParameters().get(0).asExpression();

            final List<Expression> params = new ArrayList<>();

            for (final AttributeDeclaration att : spec.getAssocSpec().getDestinationObject().getAttributes()) {
                if (att.isIdentifier() || !att.isReferential()) {
                    if (att.isUnique()) {
                        params.add(assocTranslator.getGetUniqueId(att).asFunctionCall());
                    } else if (att.getType().getBasicType().getActualType() == ActualType.TIMER) {
                        params.add(Architecture.Timer.createTimer);
                    } else {
                        if (att.isIdentifier()) {
                            // Must be able to deduce from linked objects (or semantic
                            // checks would have thrown it out)
                            for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                                if (refAtt.getRelationship() == spec.getAssocSpec().getReverseSpec()) {
                                    params.add(lhsTranslator.getAttributeGetter(refAtt.getDestinationAttribute()).asFunctionCall());
                                    break; // Don't want to add twice if it is id in both objects
                                } else if (refAtt.getRelationship() ==
                                           spec.getReverseSpec().getAssocSpec().getReverseSpec()) {
                                    params.add(rhsTranslator.getAttributeGetter(refAtt.getDestinationAttribute()).asFunctionCall(
                                            rhs,
                                            true));
                                    break; // Don't want to add twice if it is id in both objects
                                }
                            }
                        } else {
                            final TypeUsage type = Types.getInstance().getType(att.getType());
                            params.add(type.getDefaultValue());
                        }
                    }
                }
            }

            final Variable
                    assoc =
                    new Variable(assocType, "assoc", assocTranslator.getCreateInstance().asFunctionCall(params));
            deducedAssocSingleLinker.getCode().appendStatement(assoc.asStatement());

            final CodeBlock tryLink = new CodeBlock();
            tryLink.appendStatement(singleLinker.asFunctionCall(rhs, assoc.asExpression()).asStatement());
            tryLink.appendStatement(new ReturnStatement(assoc.asExpression()));

            final CodeBlock failedLink = new CodeBlock();
            failedLink.appendStatement(new Function("deleteInstance").asFunctionCall(assoc.asExpression(),
                                                                                     false).asStatement());
            failedLink.appendStatement(new ThrowStatement());

            deducedAssocSingleLinker.getCode().appendStatement(new TryCatchBlock(tryLink,
                                                                                 new TryCatchBlock.CatchBlock(null,
                                                                                                              failedLink)));

        }

    }

    private void setDeducedAssocSingleUnlinkerCode() {
        if (deducedAssocSingleUnlinker != null) {
            deducedAssocSingleUnlinker.setPure(false);
            bodyFile.addFunctionDefinition(deducedAssocSingleUnlinker);

            final Expression rhs = deducedAssocSingleUnlinker.getParameters().get(0).asExpression();

            final Variable
                    assoc =
                    new Variable(assocType.getConstReferenceType(), "assoc", singleCorrelator.asFunctionCall(rhs));
            deducedAssocSingleUnlinker.getCode().appendStatement(assoc.asStatement());
            deducedAssocSingleUnlinker.getCode().appendExpression(singleUnlinker.asFunctionCall(rhs,
                                                                                                assoc.asExpression()));
            deducedAssocSingleUnlinker.getCode().appendStatement(new ReturnStatement(assoc.asExpression()));

        }
    }

    private void setMultiForwarderCode(final Function multipleLinker, final Function singleLinker) {
        if (multipleLinker != null) {
            multipleLinker.setPure(false);
            bodyFile.addFunctionDefinition(multipleLinker);

            // for ( <destSetType>::const_iterator it = rhs.begin(); it != rhs.end();
            // ++it )
            // {
            // link(*it);
            // }

            final TypeUsage iteratorType = new TypeUsage(rhsBagType.referenceNestedType("const_iterator"));

            final Expression rhs = multipleLinker.getParameters().get(0).asExpression();

            final Variable it = new Variable(iteratorType, "it", new Function("begin").asFunctionCall(rhs, false));
            final Expression
                    endCondition =
                    new BinaryExpression(it.asExpression(),
                                         BinaryOperator.NOT_EQUAL,
                                         new Function("end").asFunctionCall(rhs, false));
            final Statement
                    linkSingle =
                    singleLinker.asFunctionCall(new UnaryExpression(UnaryOperator.DEREFERENCE,
                                                                    it.asExpression())).asStatement();

            multipleLinker.getCode().appendStatement(new ForStatement(it.asStatement(),
                                                                      endCondition,
                                                                      new UnaryExpression(UnaryOperator.PREINCREMENT,
                                                                                          it.asExpression()),
                                                                      linkSingle));

        }
    }

    private final PublicAccessors publicAccessors = new PublicAccessors();
    private final SubclassOverrides subclassOverrides = new SubclassOverrides();

    private Function counter;
    private Function navigator;
    private final Map<org.xtuml.masl.metamodel.expression.Expression, Function>
            conditionalNavigators =
            new LinkedHashMap<>();
    private Function singleCorrelator;
    private Function multipleCorrelator;

    private Function singleLinker;
    private Function multipleLinker;

    private Function deducedAssocSingleLinker;
    private Function deducedAssocMultipleLinker;

    private Function checkedSingleLinker;
    private Function checkedMultipleLinker;

    private Function checkedDeducedAssocSingleLinker;
    private Function checkedDeducedAssocMultipleLinker;

    private Function singleUnlinker;
    private Function multipleUnlinker;
    private Function allUnlinker;

    private Function deducedAssocSingleUnlinker;
    private Function deducedAssocMultipleUnlinker;
    private Function deducedAssocAllUnlinker;

    private final RelationshipSpecification spec;

    private final org.xtuml.masl.cppgen.Class mainClass;
    private final String specName;
    private final TypeUsage rhsType;
    private final TypeUsage assocType;
    private final Class rhsBagType;
    private final Class rhsSetType;
    private final Class assocSetType;
    private final Class assocBagType;
    private final CodeFile bodyFile;
    private final DeclarationGroup group;

}
