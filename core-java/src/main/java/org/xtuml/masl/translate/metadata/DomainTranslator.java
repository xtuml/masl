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
package org.xtuml.masl.translate.metadata;

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.PreOrderASTNodeVisitor;
import org.xtuml.masl.metamodel.code.DomainServiceInvocation;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.common.Service;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.expression.DomainFunctionInvocation;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodel.relationship.*;
import org.xtuml.masl.metamodel.statemodel.EventDeclaration;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.metamodel.type.*;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.main.*;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.object.ObjectServiceTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;
import org.xtuml.masl.translate.main.object.StateActionTranslator;

import java.util.*;

@Alias("MetaData")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private DomainTranslator(final Domain domain) {
        super(domain);
        mainDomainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);

        library =
                new SharedLibrary(mainDomainTranslator.getLibrary().getName() +
                                  "_metadata").withCCDefaultExtensions().withDefaultHeaderPath(domain.getName() +
                                                                                               "_OOA").inBuildSet(
                        mainDomainTranslator.getBuildSet());
        codeFile = library.createBodyFile("MetaData" + Mangler.mangleFile(domain));

        commonHeaders =
                new InterfaceLibrary(mainDomainTranslator.getLibrary().getName() +
                                     "_common_metadata").withCCDefaultExtensions().withDefaultHeaderPath(domain.getName() +
                                                                                                         "_OOA").inBuildSet(
                        mainDomainTranslator.getBuildSet());
        library.addDependency(Architecture.metaDataLib);

        if (domain.getPragmas().hasPragma("service_domain")) {
            interfaceLibrary = library;
            interfaceCodeFile = null;
        } else {
            interfaceLibrary =
                    new SharedLibrary(mainDomainTranslator.getLibrary().getName() +
                                      "_if_metadata").withCCDefaultExtensions().withDefaultHeaderPath(domain.getName() +
                                                                                                      "_OOA").inBuildSet(
                            mainDomainTranslator.getBuildSet());
            interfaceCodeFile = interfaceLibrary.createBodyFile("MetaData__if" + Mangler.mangleFile(domain));
            interfaceLibrary.addDependency(Architecture.metaDataLib);
        }

        headerFile = commonHeaders.createInterfaceHeader("MetaData" + Mangler.mangleFile(domain));

        typeIds = new EnumerationType("TypeIds", DomainNamespace.get(domain));
        headerFile.addEnumerateDeclaration(typeIds);

    }

    Expression getTypeId(final TypeDeclaration type) {
        Expression typeId = typeIdLookup.get(type);
        if (typeId == null) {
            typeId = typeIds.addEnumerator("typeId_" + Mangler.mangleName(type), null).asExpression();
            typeIdLookup.put(type, typeId);
        }
        return typeId;
    }

    private final EnumerationType typeIds;
    private final Map<TypeDeclaration, Expression> typeIdLookup = new HashMap<>();

    public CodeFile getCodeFile() {
        return codeFile;
    }

    public CodeFile getHeaderFile() {
        return headerFile;
    }

    public CodeFile getInterfaceCodeFile() {
        return interfaceCodeFile;
    }

    public FileGroup getInterfaceLibrary() {
        return interfaceLibrary;
    }

    public FileGroup getLibrary() {
        return library;
    }

    @Override
    public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites() {
        return Collections.singletonList(mainDomainTranslator);
    }

    class DomainServiceFinder extends PreOrderASTNodeVisitor {
        @Override
        public void visitDomainServiceInvocation(final DomainServiceInvocation node) {
            final Domain depDomain = node.getService().getDomain();
            if (depDomain != domain) {
                library.addDependency(getInstance(node.getService().getDomain()).interfaceLibrary);
            }
        }

        @Override
        public void visitDomainFunctionInvocation(final DomainFunctionInvocation node) {
            final Domain depDomain = node.getService().getDomain();
            if (depDomain != domain) {
                library.addDependency(getInstance(node.getService().getDomain()).interfaceLibrary);
            }
        }

    }

    @Override
    public void translate() {
        addRegistration();
        if (interfaceCodeFile != null) {
            translateInterface();
        }
        new DomainServiceFinder().visit(domain);
    }

    private Expression getEnumMetaData(final TypeDeclaration enumDeclaration,
                                       final CodeFile codeFile,
                                       final Namespace namespace) {
        final Function
                initMetaData =
                new Function("get_" + Mangler.mangleName(enumDeclaration) + "_MetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.enumMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock objectBlock = initMetaData.getCode();

        final EnumerateType enumerate = (EnumerateType) enumDeclaration.getTypeDefinition();

        final Variable
                enumTemp =
                new Variable(new TypeUsage(Architecture.enumMetaData),
                             "enumeration",
                             getTypeId(enumerate.getTypeDeclaration()),
                             Literal.createStringLiteral(enumDeclaration.getName()));
        objectBlock.appendStatement(enumTemp.asStatement());

        final EnumerationTranslator
                enumTranslator =
                mainDomainTranslator.getTypes().getEnumerateTranslator(enumerate.getTypeDeclaration());

        for (final EnumerateItem element : enumerate.getItems()) {
            objectBlock.appendStatement(Architecture.addValue.asFunctionCall(enumTemp.asExpression(),
                                                                             false,
                                                                             getEnumeratorMetaData(element,
                                                                                                   enumTranslator)).asStatement());
        }
        objectBlock.appendStatement(new ReturnStatement(enumTemp.asExpression()));

        return initMetaData.asFunctionCall();
    }

    private Expression getStructureMetaData(final TypeDeclaration structDeclaration,
                                            final CodeFile codeFile,
                                            final Namespace namespace) {
        final Function
                initMetaData =
                new Function("get_" + Mangler.mangleName(structDeclaration) + "_MetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.structMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock objectBlock = initMetaData.getCode();

        final StructureType struct = (StructureType) structDeclaration.getTypeDefinition();

        final Variable
                structTemp =
                new Variable(new TypeUsage(Architecture.structMetaData),
                             "structure",
                             getTypeId(struct.getTypeDeclaration()),
                             Literal.createStringLiteral(structDeclaration.getName()));
        objectBlock.appendStatement(structTemp.asStatement());

        for (final StructureElement element : struct.getElements()) {
            objectBlock.appendStatement(Architecture.addAttribute.asFunctionCall(structTemp.asExpression(),
                                                                                 false,
                                                                                 getElementMetaData(element)).asStatement());
        }

        objectBlock.appendStatement(new ReturnStatement(structTemp.asExpression()));

        return initMetaData.asFunctionCall();

    }

    private void translateInterface() {

        final Namespace namespace = new Namespace("init_interface_" + Mangler.mangleName(domain), new Namespace(""));

        final Expression getId = mainDomainTranslator.getDomainId();

        final Function initMetaData = new Function("initDomainMetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.domainMetaData));
        final Variable
                interfaceVar =
                new Variable(new TypeUsage(Architecture.domainMetaData),
                             "domain",
                             getId,
                             Literal.createStringLiteral(domain.getName()),
                             Literal.TRUE);

        final StatementGroup initialisationCode = new StatementGroup();

        final Expression interfaceInstance = interfaceVar.asExpression();
        initMetaData.getCode().appendStatement(interfaceVar.asStatement());
        initMetaData.getCode().appendStatement(initialisationCode);

        initMetaData.getCode().appendStatement(new ReturnStatement(interfaceInstance));
        interfaceCodeFile.addFunctionDefinition(initMetaData);

        final Function getMetaData = new Function("getDomainMetaData", namespace);
        getMetaData.setReturnType(new TypeUsage(Architecture.domainMetaData, TypeUsage.Reference));
        final Variable
                instance =
                new Variable(new TypeUsage(Architecture.domainMetaData), "domain", initMetaData.asFunctionCall());
        instance.setStatic(true);
        getMetaData.getCode().appendStatement(instance.asStatement());
        getMetaData.getCode().appendStatement(new ReturnStatement(instance.asExpression()));

        interfaceCodeFile.addFunctionDefinition(getMetaData);

        final Expression
                addDomain =
                new Function("addDomain").asFunctionCall(Architecture.processInstance,
                                                         false,
                                                         getId,
                                                         getMetaData.asFunctionPointer());

        final Variable
                registered =
                new Variable(new TypeUsage(FundamentalType.BOOL), "registered", namespace, addDomain);
        interfaceCodeFile.addVariableDefinition(registered);

        for (final TypeDeclaration type : domain.getTypes()) {
            if (type.getVisibility() == org.xtuml.masl.metamodel.common.Visibility.PUBLIC) {
                if (type.getTypeDefinition() instanceof EnumerateType) {
                    initialisationCode.appendStatement(Architecture.addEnumerate.asFunctionCall(interfaceInstance,
                                                                                                false,
                                                                                                getEnumMetaData(type,
                                                                                                                interfaceCodeFile,
                                                                                                                namespace)).asStatement());
                } else if (type.getTypeDefinition() instanceof StructureType) {
                    initialisationCode.appendStatement(Architecture.addStructure.asFunctionCall(interfaceInstance,
                                                                                                false,
                                                                                                getStructureMetaData(
                                                                                                        type,
                                                                                                        interfaceCodeFile,
                                                                                                        namespace)).asStatement());
                }
            }
        }
    }

    private void addRegistration() {
        final Namespace namespace = new Namespace("init_" + Mangler.mangleName(domain), new Namespace(""));

        final Expression getId = mainDomainTranslator.getDomainId();

        final Function initMetaData = new Function("initDomainMetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.domainMetaData));
        final Variable
                domainVar =
                new Variable(new TypeUsage(Architecture.domainMetaData),
                             "domain",
                             getId,
                             Literal.createStringLiteral(domain.getName()),
                             Literal.FALSE);
        final StatementGroup initialisationCode = new StatementGroup();

        final Expression domainInstance = domainVar.asExpression();
        initMetaData.getCode().appendStatement(domainVar.asStatement());
        initMetaData.getCode().appendStatement(initialisationCode);
        initMetaData.getCode().appendStatement(new ReturnStatement(domainInstance));
        codeFile.addFunctionDefinition(initMetaData);

        final Function getMetaData = new Function("getDomainMetaData", namespace);
        getMetaData.setReturnType(new TypeUsage(Architecture.domainMetaData, TypeUsage.Reference));
        final Variable
                instanceRef =
                new Variable(new TypeUsage(Architecture.domainMetaData), "domain", initMetaData.asFunctionCall());
        instanceRef.setStatic(true);
        getMetaData.getCode().appendStatement(instanceRef.asStatement());
        getMetaData.getCode().appendStatement(new ReturnStatement(instanceRef.asExpression()));

        codeFile.addFunctionDefinition(getMetaData);

        final Expression
                addDomain =
                new Function("addDomain").asFunctionCall(Architecture.processInstance,
                                                         false,
                                                         getId,
                                                         getMetaData.asFunctionPointer());

        final Variable
                registered =
                new Variable(new TypeUsage(FundamentalType.BOOL), "registered", namespace, addDomain);

        codeFile.addVariableDefinition(registered);

        for (final TypeDeclaration type : domain.getTypes()) {
            if (type.getTypeDefinition() instanceof EnumerateType) {
                initialisationCode.appendStatement(Architecture.addEnumerate.asFunctionCall(domainInstance,
                                                                                            false,
                                                                                            getEnumMetaData(type,
                                                                                                            codeFile,
                                                                                                            namespace)).asStatement());

            } else if (type.getTypeDefinition() instanceof StructureType) {
                initialisationCode.appendStatement(Architecture.addStructure.asFunctionCall(domainInstance,
                                                                                            false,
                                                                                            getStructureMetaData(type,
                                                                                                                 codeFile,
                                                                                                                 namespace)).asStatement());
            }
        }

        for (final DomainService service : domain.getServices()) {
            initialisationCode.appendStatement(Architecture.addService.asFunctionCall(domainInstance,
                                                                                      false,
                                                                                      getDomainServiceMetaData(service,
                                                                                                               namespace)).asStatement());
        }

        for (final RelationshipDeclaration rel : domain.getRelationships()) {
            if (rel instanceof SubtypeRelationshipDeclaration) {

                initialisationCode.appendStatement(Architecture.addSuperSubtype.asFunctionCall(domainInstance,
                                                                                               false,
                                                                                               getDomainRelationshipMetaData(
                                                                                                       rel,
                                                                                                       namespace)).asStatement());
            } else {
                initialisationCode.appendStatement(Architecture.addRelationship.asFunctionCall(domainInstance,
                                                                                               false,
                                                                                               getDomainRelationshipMetaData(
                                                                                                       rel,
                                                                                                       namespace)).asStatement());

            }
        }

        for (final DomainTerminator terminator : domain.getTerminators()) {
            initialisationCode.appendStatement(Architecture.addTerminator.asFunctionCall(domainInstance,
                                                                                         false,
                                                                                         getTerminatorMetaData(
                                                                                                 terminator,
                                                                                                 namespace)).asStatement());

        }

        for (final ObjectDeclaration object : domain.getObjects()) {
            initialisationCode.appendStatement(Architecture.addObject.asFunctionCall(domainInstance,
                                                                                     false,
                                                                                     getObjectMetaData(object,
                                                                                                       namespace)).asStatement());
        }

    }

    private Expression getAddObjRelationship(final RelationshipSpecification spec, final Namespace namespace) {
        if (spec.getRelationship() instanceof SubtypeRelationshipDeclaration) {
            return Architecture.objRelMetaData.callConstructor(Literal.createStringLiteral(spec.getRelationship().getName()),
                                                               spec.getConditional() ? Literal.TRUE : Literal.FALSE,
                                                               mainDomainTranslator.getObjectTranslator(spec.getDestinationObject()).getObjectId());

        } else {
            return Architecture.objRelMetaData.callConstructor(Literal.createStringLiteral(spec.getRelationship().getName()),
                                                               Literal.createStringLiteral(spec.getRole()),
                                                               spec.getCardinality() == MultiplicityType.MANY ?
                                                               Literal.TRUE :
                                                               Literal.FALSE,
                                                               spec.getConditional() ? Literal.TRUE : Literal.FALSE,
                                                               mainDomainTranslator.getObjectTranslator(spec.getDestinationObject()).getObjectId());
        }
    }

    private Expression getAttributeMetaData(final AttributeDeclaration att,
                                            final ObjectTranslator objectTranslator,
                                            final Namespace namespace) {
        final List<Expression> params = new ArrayList<>();
        params.add(Literal.createStringLiteral(att.getName()));
        params.add(att.isIdentifier() ? Literal.TRUE : Literal.FALSE);

        params.add(Literal.createStringLiteral(att.getType().toString()));
        params.add(TypeTranslator.getTypeMetaData(att.getType()));

        if (att.isReferential()) {
            params.add(RelationshipTranslator.getInstance(att.getRefAttDefs().get(0).getRelationship().getRelationship()).getRelationshipId());
        }

        if (att.isReferential()) {
            final Function initMetaData = new Function("get" + Mangler.mangleName(att) + "MetaData", namespace);
            initMetaData.setReturnType(new TypeUsage(Architecture.attributeMetaData));

            codeFile.addFunctionDeclaration(initMetaData);
            codeFile.addFunctionDefinition(initMetaData);

            final CodeBlock block = initMetaData.getCode();

            final Variable
                    attributeTemp =
                    new Variable(new TypeUsage(Architecture.attributeMetaData), "attribute", params);
            block.appendStatement(attributeTemp.asStatement());

            for (final ReferentialAttributeDefinition refAtt : att.getRefAttDefs()) {
                block.appendStatement(Architecture.addReferential.asFunctionCall(attributeTemp.asExpression(),
                                                                                 false,
                                                                                 getRefAttMetaData(refAtt)).asStatement());

            }

            block.appendStatement(new ReturnStatement(attributeTemp.asExpression()));

            return initMetaData.asFunctionCall();

        } else {
            if (att.getDefault() != null) {
                final ExpressionTranslator exp = ExpressionTranslator.createTranslator(att.getDefault(), null);
                params.add(Boost.lexicalCast(new TypeUsage(Std.string), exp.getReadExpression()));
            }
            final Expression metaData = Architecture.attributeMetaData.callConstructor(params);
            return metaData;
        }
    }

    private Expression getDomainRelationshipMetaData(final RelationshipDeclaration relationship,
                                                     final Namespace namespace) {
        final Expression id = RelationshipTranslator.getInstance(relationship).getRelationshipId();
        if (relationship instanceof NormalRelationshipDeclaration) {
            final NormalRelationshipDeclaration rel = (NormalRelationshipDeclaration) relationship;
            final List<Expression> params = new ArrayList<>();

            params.add(id);
            params.add(Literal.createStringLiteral(rel.getName()));

            params.add(mainDomainTranslator.getObjectTranslator(rel.getLeftObject()).getObjectId());
            params.add(Literal.createStringLiteral(rel.getRightRole()));
            params.add(rel.getRightMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
            params.add(rel.getRightConditional() ? Literal.TRUE : Literal.FALSE);

            params.add(mainDomainTranslator.getObjectTranslator(rel.getRightObject()).getObjectId());
            params.add(Literal.createStringLiteral(rel.getLeftRole()));
            params.add(rel.getLeftMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
            params.add(rel.getLeftConditional() ? Literal.TRUE : Literal.FALSE);

            return Architecture.relationshipMetaData.callConstructor(params);
        } else if (relationship instanceof AssociativeRelationshipDeclaration) {
            final AssociativeRelationshipDeclaration rel = (AssociativeRelationshipDeclaration) relationship;
            final List<Expression> params = new ArrayList<>();

            params.add(id);
            params.add(Literal.createStringLiteral(rel.getName()));

            params.add(mainDomainTranslator.getObjectTranslator(rel.getLeftObject()).getObjectId());
            params.add(Literal.createStringLiteral(rel.getRightRole()));
            params.add(rel.getRightMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
            params.add(rel.getRightConditional() ? Literal.TRUE : Literal.FALSE);

            params.add(mainDomainTranslator.getObjectTranslator(rel.getRightObject()).getObjectId());
            params.add(Literal.createStringLiteral(rel.getLeftRole()));
            params.add(rel.getLeftMult() == MultiplicityType.MANY ? Literal.TRUE : Literal.FALSE);
            params.add(rel.getLeftConditional() ? Literal.TRUE : Literal.FALSE);

            params.add(mainDomainTranslator.getObjectTranslator(rel.getAssocObject()).getObjectId());

            return Architecture.relationshipMetaData.callConstructor(params);
        } else {
            final SubtypeRelationshipDeclaration rel = (SubtypeRelationshipDeclaration) relationship;

            final Function initMetaData = new Function("get_" + relationship.getName() + "_MetaData", namespace);
            initMetaData.setReturnType(new TypeUsage(Architecture.superSubtypeMetaData));

            codeFile.addFunctionDeclaration(initMetaData);
            codeFile.addFunctionDefinition(initMetaData);

            final CodeBlock code = initMetaData.getCode();

            final Variable
                    ssRel =
                    new Variable(new TypeUsage(Architecture.superSubtypeMetaData),
                                 "supersub",
                                 id,
                                 Literal.createStringLiteral(rel.getName()),
                                 mainDomainTranslator.getObjectTranslator(rel.getSupertype()).getObjectId());
            code.appendStatement(ssRel.asStatement());

            for (final ObjectDeclaration subtype : rel.getSubtypes()) {
                code.appendExpression(Architecture.addSubObject.asFunctionCall(ssRel.asExpression(),
                                                                               false,
                                                                               mainDomainTranslator.getObjectTranslator(
                                                                                       subtype).getObjectId()));
            }

            code.appendStatement(new ReturnStatement(ssRel.asExpression()));

            return initMetaData.asFunctionCall();
        }
    }

    private Expression getDomainServiceMetaData(final DomainService service, final Namespace namespace) {
        return getServiceMetaData(service,
                                  mainDomainTranslator.getServiceTranslator(service).getServiceId(),
                                  service.isExternal() ?
                                  Architecture.externalFlag :
                                  (service.isScenario() ? Architecture.scenarioFlag : Architecture.domainServiceFlag),
                                  namespace);
    }

    private Expression getElementMetaData(final StructureElement att) {
        final List<Expression> params = new ArrayList<>();
        params.add(Literal.createStringLiteral(att.getName()));
        params.add(Literal.FALSE);

        params.add(Literal.createStringLiteral(att.getType().toString()));
        params.add(TypeTranslator.getTypeMetaData(att.getType()));

        if (att.getDefault() != null) {
            final ExpressionTranslator exp = ExpressionTranslator.createTranslator(att.getDefault(), null);
            params.add(Boost.lexicalCast(new TypeUsage(Std.string), exp.getReadExpression()));
        }

        return Architecture.attributeMetaData.callConstructor(params);
    }

    private List<Expression> getEnumeratorMetaData(final EnumerateItem enumerator,
                                                   final EnumerationTranslator enumTranslator) {
        final List<Expression> params = new ArrayList<>();
        params.add(new Function("getValue").asFunctionCall(enumTranslator.getEnumerator(enumerator), false));
        params.add(Literal.createStringLiteral(enumerator.getName()));

        return params;
    }

    private Expression getEventMetaData(final EventDeclaration event,
                                        final ObjectTranslator objectTranslator,
                                        final Namespace namespace) {

        final Function initMetaData = new Function("get_" + Mangler.mangleName(event) + "_MetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.eventMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock eventBlock = initMetaData.getCode();

        Expression type;
        switch (event.getType()) {
            case ASSIGNER:
                type = Architecture.assignerEventFlag;
                break;
            case NORMAL:
                type = Architecture.normalEventFlag;
                break;
            case CREATION:
                type = Architecture.creationEventFlag;
                break;
            default:
                type = null;
        }

        final Expression parentObjectId = ObjectTranslator.getInstance(event.getParentObject()).getObjectId();

        final Variable
                eventTemp =
                new Variable(new TypeUsage(Architecture.eventMetaData),
                             "event",
                             objectTranslator.getEventId(event),
                             parentObjectId,
                             type,
                             Literal.createStringLiteral(event.getName()));
        eventBlock.appendStatement(eventTemp.asStatement());

        for (final ParameterDefinition param : event.getParameters()) {
            eventBlock.appendStatement(Architecture.addParameter.asFunctionCall(eventTemp.asExpression(),
                                                                                false,
                                                                                TypeTranslator.getParameterMetaData(
                                                                                        param)).asStatement());
        }

        eventBlock.appendStatement(new ReturnStatement(eventTemp.asExpression()));

        return initMetaData.asFunctionCall();

    }

    private Expression getObjectMetaData(final ObjectDeclaration object, final Namespace namespace) {
        final Namespace objNamespace = new Namespace(Mangler.mangleName(object), namespace);

        final Function initMetaData = new Function("getMetaData", objNamespace);

        initMetaData.setReturnType(new TypeUsage(Architecture.objectMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock objectBlock = initMetaData.getCode();

        final ObjectTranslator objectTranslator = mainDomainTranslator.getObjectTranslator(object);
        final Variable
                objectTemp =
                new Variable(new TypeUsage(Architecture.objectMetaData),
                             "object",
                             objectTranslator.getObjectId(),
                             Literal.createStringLiteral(object.getName()),
                             Literal.createStringLiteral(object.getName()));
        objectBlock.appendStatement(objectTemp.asStatement());

        for (final AttributeDeclaration att : object.getAttributes()) {
            objectBlock.appendStatement(Architecture.addAttribute.asFunctionCall(objectTemp.asExpression(),
                                                                                 false,
                                                                                 getAttributeMetaData(att,
                                                                                                      objectTranslator,
                                                                                                      objNamespace)).asStatement());

        }

        for (final RelationshipSpecification rel : object.getRelationships()) {
            objectBlock.appendStatement(Architecture.addRelationship.asFunctionCall(objectTemp.asExpression(),
                                                                                    false,
                                                                                    getAddObjRelationship(rel,
                                                                                                          objNamespace)).asStatement());
        }

        for (final ObjectService service : object.getServices()) {
            objectBlock.appendStatement(Architecture.addService.asFunctionCall(objectTemp.asExpression(),
                                                                               false,
                                                                               getObjectServiceMetaData(service,
                                                                                                        objNamespace)).asStatement());
        }

        for (final State state : object.getStates()) {
            objectBlock.appendStatement(Architecture.addState.asFunctionCall(objectTemp.asExpression(),
                                                                             false,
                                                                             getStateMetaData(state,
                                                                                              objectTranslator,
                                                                                              objNamespace)).asStatement());

        }

        for (final EventDeclaration event : object.getAllEvents()) {
            objectBlock.appendStatement(Architecture.addEvent.asFunctionCall(objectTemp.asExpression(),
                                                                             false,
                                                                             getEventMetaData(event,
                                                                                              objectTranslator,
                                                                                              objNamespace)).asStatement());
        }

        objectBlock.appendStatement(new ReturnStatement(objectTemp.asExpression()));

        return initMetaData.asFunctionCall();

    }

    private Expression getObjectServiceMetaData(final ObjectService service, final Namespace namespace) {
        return getServiceMetaData(service,
                                  ObjectServiceTranslator.getInstance(service).getServiceId(),
                                  service.isInstance() ?
                                  Architecture.instanceServiceFlag :
                                  Architecture.objectServiceFlag,
                                  namespace);
    }

    private Expression getRefAttMetaData(final ReferentialAttributeDefinition refAtt) {
        return RelationshipTranslator.getInstance(refAtt.getRelationship().getRelationship()).getRelationshipId();
    }

    private void findCodeLines(final List<Expression> lines, final org.xtuml.masl.metamodel.code.Statement statement) {
        // Cope with native services
        if (statement == null) {
            return;
        }

        lines.add(new Literal(statement.getLineNumber()));
        for (final org.xtuml.masl.metamodel.code.Statement child : statement.getChildStatements()) {
            findCodeLines(lines, child);
        }

    }

    private Expression getServiceMetaData(final Service service,
                                          final Expression serviceId,
                                          final Expression serviceType,
                                          final Namespace namespace) {
        final Function initMetaData = new Function("get_" + Mangler.mangleName(service) + "_MetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.serviceMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock serviceBlock = initMetaData.getCode();

        final List<Expression> params = new ArrayList<>();
        params.add(serviceId);
        params.add(serviceType);
        params.add(Literal.createStringLiteral(service.getName()));
        if (service.getReturnType() != null) {
            params.add(Literal.createStringLiteral(service.getReturnType().toString()));
            params.add(TypeTranslator.getTypeMetaData(service.getReturnType()));
        }

        final List<Expression> lines = new ArrayList<>();
        findCodeLines(lines, service.getCode());
        final Variable
                linesVar =
                new Variable(new TypeUsage(FundamentalType.INT), "lines", new AggregateInitialiser(lines));
        linesVar.setArraySize(0);
        params.add(Std.vector(new TypeUsage(FundamentalType.INT)).callConstructor(linesVar.asExpression(),
                                                                                  new BinaryExpression(linesVar.asExpression(),
                                                                                                       BinaryOperator.PLUS,
                                                                                                       new Literal(lines.size()))));
        serviceBlock.appendStatement(linesVar.asStatement());

        params.add(Literal.createStringLiteral(service.getFileName() == null ? "" : service.getFileName()));
        params.add(Literal.createStringLiteral(service.getFileHash() == null ? "" : service.getFileHash()));

        final Variable serviceTemp = new Variable(new TypeUsage(Architecture.serviceMetaData), "service", params);

        serviceBlock.appendStatement(serviceTemp.asStatement());

        for (final ParameterDefinition param : service.getParameters()) {
            serviceBlock.appendStatement(Architecture.addParameter.asFunctionCall(serviceTemp.asExpression(),
                                                                                  false,
                                                                                  TypeTranslator.getParameterMetaData(
                                                                                          param)).asStatement());
        }

        for (final VariableDefinition variable : service.getLocalVariables()) {
            serviceBlock.appendStatement(Architecture.addLocalVar.asFunctionCall(serviceTemp.asExpression(),
                                                                                 false,
                                                                                 TypeTranslator.getLocalVarMetaData(
                                                                                         variable)).asStatement());
        }

        serviceBlock.appendStatement(new ReturnStatement(serviceTemp.asExpression()));

        return initMetaData.asFunctionCall();
    }

    private Expression getStateMetaData(final State state,
                                        final ObjectTranslator objectTranslator,
                                        final Namespace namespace) {

        final Function initMetaData = new Function("get_" + Mangler.mangleName(state) + "_MetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.stateMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock stateBlock = initMetaData.getCode();

        final StateActionTranslator mainActionTranslator = objectTranslator.getStateActionTranslator(state);

        Expression type;
        switch (state.getType()) {
            case ASSIGNER:
                type = Architecture.assignerStateFlag;
                break;
            case ASSIGNER_START:
                type = Architecture.startStateFlag;
                break;
            case NORMAL:
                type = Architecture.normalStateFlag;
                break;
            case CREATION:
                type = Architecture.creationStateFlag;
                break;
            case TERMINAL:
                type = Architecture.terminalStateFlag;
                break;
            default:
                type = null;
        }

        final List<Expression> params = new ArrayList<>();
        params.add(mainActionTranslator.getStateId());
        params.add(type);
        params.add(Literal.createStringLiteral(state.getName()));

        final List<Expression> lines = new ArrayList<>();
        findCodeLines(lines, state.getCode());
        final Variable
                linesVar =
                new Variable(new TypeUsage(FundamentalType.INT), "lines", new AggregateInitialiser(lines));
        linesVar.setArraySize(0);
        params.add(Std.vector(new TypeUsage(FundamentalType.INT)).callConstructor(linesVar.asExpression(),
                                                                                  new BinaryExpression(linesVar.asExpression(),
                                                                                                       BinaryOperator.PLUS,
                                                                                                       new Literal(lines.size()))));
        stateBlock.appendStatement(linesVar.asStatement());

        params.add(Literal.createStringLiteral(state.getFileName() == null ? "" : state.getFileName()));

        params.add(Literal.createStringLiteral(state.getFileHash() == null ? "" : state.getFileHash()));

        final Variable stateTemp = new Variable(new TypeUsage(Architecture.stateMetaData), "state", params);
        stateBlock.appendStatement(stateTemp.asStatement());

        for (final ParameterDefinition param : state.getParameters()) {
            stateBlock.appendStatement(Architecture.addParameter.asFunctionCall(stateTemp.asExpression(),
                                                                                false,
                                                                                TypeTranslator.getParameterMetaData(
                                                                                        param)).asStatement());
        }

        for (final VariableDefinition variable : state.getLocalVariables()) {
            stateBlock.appendStatement(Architecture.addLocalVar.asFunctionCall(stateTemp.asExpression(),
                                                                               false,
                                                                               TypeTranslator.getLocalVarMetaData(
                                                                                       variable)).asStatement());
        }

        stateBlock.appendStatement(new ReturnStatement(stateTemp.asExpression()));

        return initMetaData.asFunctionCall();

    }

    private Expression getTerminatorMetaData(final DomainTerminator terminator, final Namespace namespace) {
        final Namespace termNamespace = new Namespace(Mangler.mangleName(terminator), namespace);

        final Function initMetaData = new Function("getMetaData", termNamespace);
        initMetaData.setReturnType(new TypeUsage(Architecture.terminatorMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock termBlock = initMetaData.getCode();

        final TerminatorTranslator termTranslator = mainDomainTranslator.getTerminatorTranslator(terminator);
        final Variable
                termTemp =
                new Variable(new TypeUsage(Architecture.terminatorMetaData),
                             "terminator",
                             termTranslator.getTerminatorId(),
                             Literal.createStringLiteral(terminator.getName()),
                             Literal.createStringLiteral(terminator.getName()));
        termBlock.appendStatement(termTemp.asStatement());

        for (final DomainTerminatorService service : terminator.getServices()) {
            termBlock.appendStatement(Architecture.addService.asFunctionCall(termTemp.asExpression(),
                                                                             false,
                                                                             getTerminatorServiceMetaData(service,
                                                                                                          termNamespace)).asStatement());

        }

        termBlock.appendStatement(new ReturnStatement(termTemp.asExpression()));

        return initMetaData.asFunctionCall();

    }

    private Expression getTerminatorServiceMetaData(final DomainTerminatorService service, final Namespace namespace) {
        return getServiceMetaData(service,
                                  TerminatorServiceTranslator.getInstance(service).getServiceId(),
                                  Architecture.terminatorServiceFlag,
                                  namespace);
    }

    private final org.xtuml.masl.translate.main.DomainTranslator mainDomainTranslator;

    private final Library library;

    private final Library interfaceLibrary;
    private final Library commonHeaders;

    private final CodeFile codeFile;

    private final CodeFile headerFile;

    private final CodeFile interfaceCodeFile;

}
