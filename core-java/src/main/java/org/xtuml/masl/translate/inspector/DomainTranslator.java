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
package org.xtuml.masl.translate.inspector;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.AssociativeRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.NormalRelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.relationship.SubtypeRelationshipDeclaration;
import org.xtuml.masl.metamodel.type.EnumerateType;
import org.xtuml.masl.metamodel.type.StructureElement;
import org.xtuml.masl.metamodel.type.StructureType;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.main.*;

import java.util.*;

@Alias("Inspector")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    private final org.xtuml.masl.translate.main.DomainTranslator mainTranslator;

    private final Namespace domainNamespace;

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    public Namespace getNamespace() {
        return domainNamespace;
    }

    private DomainTranslator(final Domain domain) {
        super(domain);
        mainTranslator = org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain);
        this.domainNamespace = new Namespace(Mangler.mangleName(domain), Inspector.inspectorNamespace);

        this.library =
                new SharedLibrary(mainTranslator.getLibrary().getName() +
                                  "_inspector").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        this.interfaceLibrary =
                new SharedLibrary(mainTranslator.getLibrary().getName() +
                                  "_if_inspector").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
        library.addDependency(interfaceLibrary);
        library.addDependency(Inspector.library);
        interfaceLibrary.addDependency(Inspector.library);

    }

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.DomainTranslator> getPrerequisites() {
        return Collections.<org.xtuml.masl.translate.DomainTranslator>singletonList(mainTranslator);
    }

    @Override
    public void translate() {
        this.codeFile = library.createBodyFile("Inspector" + Mangler.mangleFile(domain));
        this.typesCodeFile = interfaceLibrary.createBodyFile("Inspector_types" + Mangler.mangleFile(domain));
        this.privateTypesCodeFile = library.createBodyFile("Inspector_private_types" + Mangler.mangleFile(domain));

        for (final TypeDeclaration type : domain.getTypes()) {
            final CodeFile
                    codeFile =
                    type.getVisibility() == org.xtuml.masl.metamodel.common.Visibility.PUBLIC ?
                    typesCodeFile :
                    privateTypesCodeFile;
            if (type.getTypeDefinition() instanceof StructureType) {
                codeFile.addFunctionDefinition(addStructureWriter((StructureType) type.getTypeDefinition()));
                codeFile.addFunctionDefinition(addStructureReader((StructureType) type.getTypeDefinition()));
            } else if (type.getTypeDefinition() instanceof EnumerateType) {
                codeFile.addFunctionDefinition(addEnumerationWriter((EnumerateType) type.getTypeDefinition()));
                codeFile.addFunctionDefinition(addEnumerationReader((EnumerateType) type.getTypeDefinition()));
            }
        }

        for (final ObjectDeclaration object : domain.getObjects()) {
            final ObjectTranslator trans = new ObjectTranslator(object);
            objectTranslators.put(object, trans);
            trans.translate();
        }

        for (final DomainTerminator term : domain.getTerminators()) {
            final TerminatorTranslator trans = new TerminatorTranslator(term);
            terminatorTranslators.put(term, trans);
            trans.translate();
        }

        for (final DomainService service : domain.getServices()) {
            final ActionTranslator trans = new ActionTranslator(service, this);
            serviceTranslators.put(service, trans);
            trans.translate();
        }

        addDomainClass();

        addRegistration();

    }

    TerminatorTranslator getTerminatorTranslator(final DomainTerminator terminator) {
        return terminatorTranslators.get(terminator);
    }

    ObjectTranslator getObjectTranslator(final ObjectTranslator terminator) {
        return objectTranslators.get(terminator);
    }

    private final Map<ObjectDeclaration, ObjectTranslator>
            objectTranslators =
            new HashMap<ObjectDeclaration, ObjectTranslator>();
    private final Map<DomainTerminator, TerminatorTranslator>
            terminatorTranslators =
            new HashMap<DomainTerminator, TerminatorTranslator>();
    private final Map<DomainService, ActionTranslator>
            serviceTranslators =
            new HashMap<DomainService, ActionTranslator>();

    private DeclarationGroup group;

    private void addDomainClass() {
        domainHandlerClass = new Class(Mangler.mangleName(domain) + "Handler", domainNamespace);
        domainHandlerClass.addSuperclass(Inspector.domainHandlerClass, Visibility.PUBLIC);
        codeFile.addClassDeclaration(domainHandlerClass);

        group = domainHandlerClass.createDeclarationGroup();

        final Function constructor = domainHandlerClass.createConstructor(group, Visibility.PUBLIC);

        codeFile.addFunctionDefinition(constructor);

        final Class objPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.genericObjectHandlerClass));
        final Function registerObjectHandler = new Function("registerObjectHandler");

        for (final ObjectDeclaration object : domain.getObjects()) {
            final ObjectTranslator objTrans = objectTranslators.get(object);
            final Expression id = mainTranslator.getObjectTranslator(object).getObjectId();
            final Class objectHandlerClass = objTrans.getHandlerClass();

            constructor.getCode().appendExpression(registerObjectHandler.asFunctionCall(id,
                                                                                        objPtrType.callConstructor(new NewExpression(
                                                                                                new TypeUsage(
                                                                                                        objectHandlerClass)))));

        }

        final Class termPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.terminatorHandlerClass));
        final Function registerTerminatorHandler = new Function("registerTerminatorHandler");
        for (final DomainTerminator terminator : domain.getTerminators()) {
            final TerminatorTranslator termTrans = terminatorTranslators.get(terminator);
            final Expression id = mainTranslator.getTerminatorTranslator(terminator).getTerminatorId();
            final Class terminatorHandlerClass = termTrans.getHandlerClass();

            constructor.getCode().appendExpression(registerTerminatorHandler.asFunctionCall(id,
                                                                                            termPtrType.callConstructor(
                                                                                                    new NewExpression(
                                                                                                            new TypeUsage(
                                                                                                                    terminatorHandlerClass)))));

        }

        final Class srvPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.actionHandlerClass));
        final Function registerServiceHandler = new Function("registerServiceHandler");
        for (final DomainService service : domain.getServices()) {
            final ActionTranslator servTrans = serviceTranslators.get(service);
            // If a service uses a prohibited parameter type, then an Action
            // translator will not be produced. Detect this case and do not
            // allow any sebsequent code to be generated.
            if (servTrans != null) {
                final Expression id = mainTranslator.getServiceTranslator(service).getServiceId();
                final Class actionHandlerClass = servTrans.getHandlerClass();

                constructor.getCode().appendExpression(registerServiceHandler.asFunctionCall(id,
                                                                                             srvPtrType.callConstructor(
                                                                                                     new NewExpression(
                                                                                                             new TypeUsage(
                                                                                                                     actionHandlerClass)))));
            }

        }

        addCreateRelationships();

    }

    private void addCreateRelationships() {
        final Function
                relCreator =
                domainHandlerClass.createMemberFunction(group, "createRelationship", Visibility.PUBLIC);
        codeFile.addFunctionDefinition(relCreator);

        final Expression
                channel =
                relCreator.createParameter(new TypeUsage(Inspector.commChannel, TypeUsage.Reference),
                                           "channel").asExpression();
        final Expression relId = relCreator.createParameter(new TypeUsage(FundamentalType.INT), "relId").asExpression();

        final List<SwitchStatement.CaseCondition> relHandlers = new ArrayList<SwitchStatement.CaseCondition>();

        for (final RelationshipDeclaration rel : domain.getRelationships()) {
            final Expression id = RelationshipTranslator.getInstance(rel).getRelationshipId();

            relHandlers.add(new SwitchStatement.CaseCondition(id, getRelationshipCreator(rel, channel)));
        }

        relCreator.getCode().appendStatement(new SwitchStatement(relId, relHandlers));

    }

    CodeBlock getRelationshipCreator(final RelationshipDeclaration rel, final Expression channel) {
        final CodeBlock group = new CodeBlock();

        if (rel instanceof NormalRelationshipDeclaration nrel) {

            final Variable lhsId = new Variable(new TypeUsage(Architecture.ID_TYPE), "lhsId");
            final Variable rhsId = new Variable(new TypeUsage(Architecture.ID_TYPE), "rhsId");
            group.appendStatement(lhsId.asStatement());
            group.appendStatement(rhsId.asStatement());

            group.appendStatement(new BinaryExpression(new BinaryExpression(channel,
                                                                            BinaryOperator.RIGHT_SHIFT,
                                                                            lhsId.asExpression()),
                                                       BinaryOperator.RIGHT_SHIFT,
                                                       rhsId.asExpression()).asStatement());

            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    lhsTrans =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(nrel.getLeftObject());
            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    rhsTrans =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(nrel.getRightObject());
            final Expression lhsObj = lhsTrans.getGetInstance().asFunctionCall(lhsId.asExpression());
            final Expression rhsObj = rhsTrans.getGetInstance().asFunctionCall(rhsId.asExpression());

            final Function
                    linker =
                    lhsTrans.getRelationshipTranslator(nrel.getLeftToRightSpec()).getPublicAccessors().getSingleLinkFunction();

            group.appendStatement(linker.asFunctionCall(lhsObj, true, rhsObj).asStatement());

            group.appendStatement(new BreakStatement());
        } else if (rel instanceof AssociativeRelationshipDeclaration arel) {

            final Variable lhsId = new Variable(new TypeUsage(Architecture.ID_TYPE), "lhsId");
            final Variable rhsId = new Variable(new TypeUsage(Architecture.ID_TYPE), "rhsId");
            final Variable assocId = new Variable(new TypeUsage(Architecture.ID_TYPE), "assocId");

            group.appendStatement(lhsId.asStatement());
            group.appendStatement(rhsId.asStatement());
            group.appendStatement(assocId.asStatement());

            group.appendStatement(new BinaryExpression(new BinaryExpression(new BinaryExpression(channel,
                                                                                                 BinaryOperator.RIGHT_SHIFT,
                                                                                                 lhsId.asExpression()),
                                                                            BinaryOperator.RIGHT_SHIFT,
                                                                            rhsId.asExpression()),
                                                       BinaryOperator.RIGHT_SHIFT,
                                                       assocId.asExpression()).asStatement());

            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    lhsTrans =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(arel.getLeftObject());
            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    rhsTrans =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(arel.getRightObject());
            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    assocTrans =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(arel.getAssocObject());
            final Expression lhsObj = lhsTrans.getGetInstance().asFunctionCall(lhsId.asExpression());
            final Expression rhsObj = rhsTrans.getGetInstance().asFunctionCall(rhsId.asExpression());
            final Expression assocObj = assocTrans.getGetInstance().asFunctionCall(assocId.asExpression());

            final Function
                    linker =
                    lhsTrans.getRelationshipTranslator(arel.getLeftToRightSpec()).getPublicAccessors().getSingleLinkFunction();

            group.appendStatement(linker.asFunctionCall(lhsObj, true, rhsObj, assocObj).asStatement());

            group.appendStatement(new BreakStatement());
        } else if (rel instanceof SubtypeRelationshipDeclaration srel) {

            final Variable supId = new Variable(new TypeUsage(Architecture.ID_TYPE), "supId");
            final Variable subIndex = new Variable(new TypeUsage(FundamentalType.INT), "subObjIndex");
            final Variable subId = new Variable(new TypeUsage(Architecture.ID_TYPE), "subId");

            group.appendStatement(supId.asStatement());
            group.appendStatement(subIndex.asStatement());
            group.appendStatement(subId.asStatement());

            group.appendStatement(new BinaryExpression(new BinaryExpression(new BinaryExpression(channel,
                                                                                                 BinaryOperator.RIGHT_SHIFT,
                                                                                                 supId.asExpression()),
                                                                            BinaryOperator.RIGHT_SHIFT,
                                                                            subIndex.asExpression()),
                                                       BinaryOperator.RIGHT_SHIFT,
                                                       subId.asExpression()).asStatement());

            final org.xtuml.masl.translate.main.object.ObjectTranslator
                    supTrans =
                    org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(srel.getSupertype());
            final Variable
                    supObj =
                    new Variable(supTrans.getPointerType(),
                                 "supObj",
                                 supTrans.getGetInstance().asFunctionCall(supId.asExpression()));

            group.appendStatement(supObj.asStatement());

            final List<SwitchStatement.CaseCondition> subHandlers = new ArrayList<SwitchStatement.CaseCondition>();

            int index = 0;
            for (final ObjectDeclaration subObjDecl : srel.getSubtypes()) {
                final StatementGroup objGroup = new StatementGroup();
                final org.xtuml.masl.translate.main.object.ObjectTranslator
                        subTrans =
                        org.xtuml.masl.translate.main.object.ObjectTranslator.getInstance(subObjDecl);

                final Expression subObj = subTrans.getGetInstance().asFunctionCall(subId.asExpression());

                final Function
                        linker =
                        supTrans.getRelationshipTranslator(srel.getSuperToSubSpec(subObjDecl)).getPublicAccessors().getSingleLinkFunction();
                objGroup.appendStatement(linker.asFunctionCall(supObj.asExpression(), true, subObj).asStatement());
                objGroup.appendStatement(new BreakStatement());

                subHandlers.add(new SwitchStatement.CaseCondition(new Literal(index++), objGroup));
            }

            final SwitchStatement objChoice = new SwitchStatement(subIndex.asExpression(), subHandlers);
            group.appendStatement(objChoice);
            group.appendStatement(new BreakStatement());
        }

        return group;
    }

    private void addRegistration() {
        final Namespace namespace = new Namespace("");

        final Expression processHandler = Inspector.processHandlerClass.callStaticFunction("getInstance");

        final Expression getId = getDomainId(domain.getName());

        final Function registerDomainFunc = new Function("registerDomainHandler");

        final Class ptrType = Boost.getSharedPtrType(new TypeUsage(Inspector.domainHandlerClass));

        final Expression
                registerDomain =
                registerDomainFunc.asFunctionCall(processHandler,
                                                  false,
                                                  getId,
                                                  ptrType.callConstructor(new NewExpression(new TypeUsage(
                                                          domainHandlerClass))));

        final Variable
                registered =
                new Variable(new TypeUsage(FundamentalType.BOOL),
                             Mangler.mangleName(domain) + "_registered",
                             namespace,
                             registerDomain);
        codeFile.addVariableDefinition(registered);

    }

    private Function addStructureWriter(final StructureType struct) {
        final Structure structTrans = mainTranslator.getTypes().getStructureTranslator(struct.getTypeDeclaration());
        final Class mainClass = structTrans.getMainClass();

        final Function
                write =
                Inspector.bufferedOutputStream.specialiseMemberFunction("write",
                                                                        TemplateSpecialisation.create(new TypeUsage(
                                                                                mainClass)));

        final Expression
                value =
                write.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "value").asExpression();

        for (final StructureElement element : struct.getElements()) {
            write.getCode().appendStatement(writeElement(structTrans, element, value));
        }

        return write;
    }

    private Function addStructureReader(final StructureType struct) {
        final Structure structTrans = mainTranslator.getTypes().getStructureTranslator(struct.getTypeDeclaration());
        final Class mainClass = structTrans.getMainClass();

        final Function
                read =
                Inspector.bufferedInputStream.specialiseMemberFunction("read",
                                                                       TemplateSpecialisation.create(new TypeUsage(
                                                                               mainClass)));

        final Expression
                value =
                read.createParameter(new TypeUsage(mainClass, TypeUsage.Reference), "value").asExpression();

        for (final StructureElement element : struct.getElements()) {
            read.getCode().appendStatement(readElement(structTrans, element, value));
        }

        return read;
    }

    private Function addEnumerationWriter(final EnumerateType enumerate) {
        final EnumerationTranslator
                enumTrans =
                mainTranslator.getTypes().getEnumerateTranslator(enumerate.getTypeDeclaration());
        final Class mainClass = enumTrans.getMainClass();

        final Function
                write =
                Inspector.bufferedOutputStream.specialiseMemberFunction("write",
                                                                        TemplateSpecialisation.create(new TypeUsage(
                                                                                mainClass)));

        final Expression
                value =
                write.createParameter(new TypeUsage(mainClass, TypeUsage.ConstReference), "value").asExpression();

        write.getCode().appendStatement(new Function("write").asFunctionCall(Std.static_cast(new TypeUsage(
                FundamentalType.INT)).asFunctionCall(enumTrans.getGetIndex().asFunctionCall(value,
                                                                                            false))).asStatement());

        return write;
    }

    private Function addEnumerationReader(final EnumerateType enumerate) {
        final EnumerationTranslator
                enumTrans =
                mainTranslator.getTypes().getEnumerateTranslator(enumerate.getTypeDeclaration());
        final Class mainClass = enumTrans.getMainClass();

        final Function
                read =
                Inspector.bufferedInputStream.specialiseMemberFunction("read",
                                                                       TemplateSpecialisation.create(new TypeUsage(
                                                                               mainClass)));

        final Expression
                value =
                read.createParameter(new TypeUsage(mainClass, TypeUsage.Reference), "value").asExpression();

        final Variable index = new Variable(new TypeUsage(FundamentalType.INT), "index");
        read.getCode().appendStatement(index.asStatement());
        read.getCode().appendStatement(new Function("read").asFunctionCall(index.asExpression()).asStatement());
        final Expression
                newValue =
                enumTrans.getMainClass().callConstructor(enumTrans.getIndexEnum().callConstructor(index.asExpression()));
        read.getCode().appendExpression(new BinaryExpression(value, BinaryOperator.ASSIGN, newValue));

        return read;
    }

    private Expression getDomainId(final String domainName) {
        return new Function("getId").asFunctionCall(new Function("getDomain").asFunctionCall(Architecture.process,
                                                                                             false,
                                                                                             Literal.createStringLiteral(
                                                                                                     domainName)),
                                                    false);
    }

    private Statement writeElement(final Structure structTrans,
                                   final StructureElement element,
                                   final Expression value) {
        final Function write = new Function("write");
        final Statement
                result =
                write.asFunctionCall(structTrans.getGetter(element).asFunctionCall(value, false)).asStatement();
        return result;
    }

    private Statement readElement(final Structure structTrans, final StructureElement element, final Expression value) {
        final Function read = new Function("read");
        final Statement
                result =
                read.asFunctionCall(structTrans.getSetter(element).asFunctionCall(value, false)).asStatement();
        return result;
    }

    public Library getLibrary() {
        return library;
    }

    public FileGroup getInterfaceLibrary() {
        return interfaceLibrary;
    }

    private CodeFile codeFile;
    private CodeFile typesCodeFile;
    private CodeFile privateTypesCodeFile;

    private Class domainHandlerClass;
    private final Library library;
    private final Library interfaceLibrary;

    public org.xtuml.masl.translate.main.DomainTranslator getMainTranslator() {
        return mainTranslator;
    }

    public CodeFile getCodeFile() {
        return codeFile;
    }
}
