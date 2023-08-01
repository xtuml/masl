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
package org.xtuml.masl.translate.main;

import com.google.common.base.Suppliers;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.cppgen.EnumerationType.Enumerator;
import org.xtuml.masl.metamodel.common.Visibility;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.exception.ExceptionDeclaration;
import org.xtuml.masl.metamodel.object.ObjectDeclaration;
import org.xtuml.masl.metamodel.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodel.type.TypeDeclaration;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.building.BuildSet;
import org.xtuml.masl.translate.building.FileGroup;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Alias("Main")
@Default
public class DomainTranslator extends org.xtuml.masl.translate.DomainTranslator {

    private final Library library;
    private final Library interfaceLibrary;
    private final Library standaloneDeps;

    public static final String NativeStubsFile = "NativeStubs.cc";

    private static final String signalHandlerPragma = "signal_handler";
    private static final String startupPragma = "startup";
    private static final String processListenerPragma = "process_listener";

    public static DomainTranslator getInstance(final Domain domain) {
        return getInstance(DomainTranslator.class, domain);
    }

    private final Library standaloneExecutableSkeleton;

    private DomainTranslator(final Domain domain) {
        super(domain);
        buildSet = BuildSet.getBuildSet(domain);

        library =
                new SharedLibrary(domain.getName()).withDefaultHeaderPath(domain.getName() +
                                                                          "_OOA").withCCDefaultExtensions().inBuildSet(
                        getBuildSet());
        library.addDependency(Architecture.library);

        bodyFile = library.createBodyFile(Mangler.mangleFile(domain));

        domainHeader = library.createInterfaceHeader(Mangler.mangleFile(domain));
        terminatorsHeaderFile = library.createInterfaceHeader(Mangler.mangleFile(domain) + "_terminators");
        privateTypeBodyFile = library.createBodyFile(Mangler.mangleFile(domain) + "_private_types");
        privateTypeHeaderFile = library.createPrivateHeader(Mangler.mangleFile(domain) + "_private_types");

        if (domain.getPragmas().hasPragma("service_domain")) {
            interfaceLibrary = library;
            interfaceBodyFile = null;
            interfaceDomainHeader = domainHeader;
        } else {
            interfaceLibrary =
                    new SharedLibrary(domain.getName() + "_interface").withDefaultHeaderPath(domain.getName() +
                                                                                             "_OOA").withCCDefaultExtensions().inBuildSet(
                            getBuildSet());
            interfaceBodyFile = interfaceLibrary.createBodyFile(Mangler.mangleFile(domain) + "_interface");
            interfaceDomainHeader = interfaceLibrary.createInterfaceHeader(Mangler.mangleFile(domain) + "_interface");
            library.addDependency(interfaceLibrary);
        }

        interfaceLibrary.addDependency(Architecture.library);
        interfaceLibrary.addDependency(ASN1.library);
        publicTypeBodyFile = interfaceLibrary.createBodyFile(Mangler.mangleFile(domain) + "_types");
        publicTypeHeaderFile = interfaceLibrary.createInterfaceHeader(Mangler.mangleFile(domain) + "_types");

        publicServicesHeaderFile = interfaceLibrary.createInterfaceHeader(Mangler.mangleFile(domain) + "_services");
        privateServicesHeaderFile =
                interfaceLibrary.createPrivateHeader(Mangler.mangleFile(domain) + "_private_services");

        standaloneExecutableSkeleton = new Library(domain.getName() + "_standalone_skeleton").withCCDefaultExtensions();
        standaloneDeps =
                new InterfaceLibrary(domain.getName() + "_standalone_deps").withCCDefaultExtensions().inBuildSet(
                        getBuildSet());
        standaloneExecutableSkeleton.addDependency(standaloneDeps);
        standaloneDeps.addDependency(library);

        getDomain = new Function("getDomain", DomainNamespace.get(domain));
        interfaceDomainHeader.addFunctionDeclaration(getDomain);
        getDomainId = new Function("getId").asFunctionCall(getDomain.asFunctionCall(), false);

    }

    @Override
    public Domain getDomain() {
        return domain;
    }

    public BuildSet getBuildSet() {
        return buildSet;
    }

    private final BuildSet buildSet;

    @Override
    public void translate() {
        getDomain.setReturnType(new TypeUsage(Architecture.domainClass, TypeUsage.Reference));

        final Variable
                domainVar =
                new Variable(new TypeUsage(Architecture.domainClass, TypeUsage.Reference),
                             "domain",
                             new Function("registerDomain").asFunctionCall(Architecture.process,
                                                                           false,
                                                                           Literal.createStringLiteral(domain.getName())));
        domainVar.setStatic(true);
        getDomain.getCode().appendStatement(new VariableDefinitionStatement(domainVar));
        getDomain.getCode().appendStatement(new ReturnStatement(domainVar.asExpression()));

        if (interfaceBodyFile == null) {
            bodyFile.addFunctionDefinition(getDomain);
            initialiseInterface = null;
        } else {
            interfaceBodyFile.addFunctionDefinition(getDomain);

            initialiseInterface = new Function("initialiseInterface", DomainNamespace.get(domain));
            initialiseInterface.setReturnType(new TypeUsage(FundamentalType.BOOL));

            final Variable
                    interfaceInitialised =
                    new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const),
                                 "interfaceInitialised",
                                 DomainNamespace.get(domain),
                                 initialiseInterface.asFunctionCall());
            interfaceBodyFile.addFunctionDefinition(initialiseInterface);
            interfaceBodyFile.addVariableDefinition(interfaceInitialised);
        }

        initialiseDomain = new Function("initialiseDomain", DomainNamespace.get(domain));
        initialiseDomain.setReturnType(new TypeUsage(FundamentalType.BOOL));

        final Variable
                initialised =
                new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const),
                             "domainInitialised",
                             DomainNamespace.get(domain),
                             initialiseDomain.asFunctionCall());
        bodyFile.addFunctionDefinition(initialiseDomain);
        bodyFile.addVariableDefinition(initialised);

        addObjects();
        addRelationships();
        addTypes();

        addServices();
        addExceptions();
        addTerminators();

        translateRelationships();
        translateObjects();
        translatePolymorphisms();
        translateTerminators();

        translateObjectCode();
        translateServiceCode();
        translateTerminatorCode();

        addInitCode();

        if (getProperties().getProperty("standalone", "true") == "true") {
            addStandaloneCode();
        }

        for (final Domain referenced : domain.getReferencedInterfaces()) {
            standaloneDeps.addDependency(DomainTranslator.getInstance(referenced).standaloneDeps);
        }

    }

    private void addStandaloneCode() {
        final Namespace namespace = new Namespace(Mangler.mangleName(domain));
        final Function initialiseProcess = new Function("initialiseProcess", namespace);
        initialiseProcess.setReturnType(new TypeUsage(FundamentalType.BOOL));

        final CodeFile bodyFile = standaloneExecutableSkeleton.createBodyFile("initStandalone");

        final Variable
                initialised =
                new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const),
                             "processInitialised",
                             namespace,
                             initialiseProcess.asFunctionCall());
        bodyFile.addFunctionDefinition(initialiseProcess);
        bodyFile.addVariableDefinition(initialised);

        initialiseProcess.getCode().appendStatement(new Function("setProjectName").asFunctionCall(Architecture.process,
                                                                                                  false,
                                                                                                  Literal.createStringLiteral(
                                                                                                          domain.getName())).asStatement());
        initialiseProcess.getCode().appendStatement(new ReturnStatement(Literal.TRUE));

        final Main main = new Main();
        bodyFile.addFunctionDefinition(main);
        main.getCode().appendStatement(new ReturnStatement(Architecture.main.asFunctionCall(main.getArgc(),
                                                                                            main.getArgv())));

    }

    private void addInitCode() {
        if (initialiseInterface != null) {
            initialiseInterface.getCode().appendExpression(getDomain.asFunctionCall());
            initialiseInterface.getCode().appendStatement(new ReturnStatement(Literal.TRUE));
        }
        initialiseDomain.getCode().appendExpression(new Function("setInterface").asFunctionCall(getDomain.asFunctionCall(),
                                                                                                false,
                                                                                                Literal.FALSE));

        for (final DomainService service : domain.getServices()) {
            final DomainServiceTranslator translator = getServiceTranslator(service);
            final Expression fnPtr = translator.getFunction().asFunctionPointer();

            if (service.isExternal()) {
                initialiseDomain.getCode().appendExpression(new Function("addExternal").asFunctionCall(getDomain.asFunctionCall(),
                                                                                                       false,
                                                                                                       new Literal(
                                                                                                               service.getExternalNo()),
                                                                                                       fnPtr));
            } else if (service.isScenario()) {
                initialiseDomain.getCode().appendExpression(new Function("addScenario").asFunctionCall(getDomain.asFunctionCall(),
                                                                                                       false,
                                                                                                       new Literal(
                                                                                                               service.getScenarioNo()),
                                                                                                       fnPtr));
            }

            final List<String> handlerFor = service.getDeclarationPragmas().getPragmaValues(signalHandlerPragma);

            if (handlerFor != null) {
                for (final String signal : handlerFor) {
                    Expression callback = null;
                    switch (service.getParameters().size()) {
                        case 0:
                            callback = Boost.bind.asFunctionCall(fnPtr);
                            break;
                        case 1:
                            callback = Boost.bind.asFunctionCall(fnPtr, Boost.bind_1);
                            break;
                        case 2:
                            callback = Boost.bind.asFunctionCall(fnPtr, Boost.bind_1, Boost.bind_2);
                            break;
                        default:
                            System.out.println("WARNING - Signature for service \"" +
                                               service.getName() +
                                               "\" incompatible with signal handler.");
                    }

                    if (callback != null) {
                        initialiseDomain.getCode().appendExpression(Architecture.registerSignalHandler(new Literal(
                                signal), callback));
                    }
                }
            }

            if (service.getParameters().size() == 0 &&
                service.getDeclarationPragmas().getPragmaValues(startupPragma) != null) {
                initialiseDomain.getCode().appendExpression(Architecture.registerStartupService(fnPtr));
            }

            final List<String> events = service.getDeclarationPragmas().getPragmaValues(processListenerPragma);
            if (service.getParameters().size() == 0 && events != null) {
                for (final String event : events) {
                    initialiseDomain.getCode().appendExpression(Architecture.registerProcessListener(event, fnPtr));
                }
            }

        }

        initialiseDomain.getCode().appendStatement(new ReturnStatement(new Literal("true")));
    }

    private void addRelationships() {

        final EnumerationType relsEnum = new EnumerationType("RelationshipIds", DomainNamespace.get(domain));

        domainHeader.addEnumerateDeclaration(relsEnum);

        for (final RelationshipDeclaration relationship : domain.getRelationships()) {
            final Enumerator relId = relsEnum.addEnumerator("relationshipId_" + relationship.getName(), null);

            final RelationshipTranslator translator = new RelationshipTranslator(relationship, relId.asExpression());
            relationshipTranslators.put(relationship, translator);
        }

    }

    private void translateRelationships() {
        for (final RelationshipDeclaration relationship : domain.getRelationships()) {
            getRelationshipTranslator(relationship).translateRelationship();
        }
    }

    public RelationshipTranslator getRelationshipTranslator(final RelationshipDeclaration relationship) {
        return relationshipTranslators.get(relationship);
    }

    public ObjectTranslator getObjectTranslator(final ObjectDeclaration object) {
        return objectTranslators.get(object);
    }

    public TerminatorTranslator getTerminatorTranslator(final DomainTerminator terminator) {
        return terminatorTranslators.get(terminator);
    }

    public DomainServiceTranslator getServiceTranslator(final DomainService service) {
        DomainServiceTranslator translator = serviceTranslators.get(service);
        if (translator == null) {
            // This is not the main domain, so just need the interface generating
            translator = new DomainServiceTranslator(service, null);
            serviceTranslators.put(service, translator);
        }
        return translator;
    }

    private void addObjects() {
        final EnumerationType objectsEnum = new EnumerationType("ObjectIds", DomainNamespace.get(domain));

        domainHeader.addEnumerateDeclaration(objectsEnum);

        // Create the object translators before running them, so that any circular
        // references are avoided.
        for (final ObjectDeclaration object : domain.getObjects()) {
            final Enumerator objectId = objectsEnum.addEnumerator("objectId_" + Mangler.mangleName(object), null);
            objectTranslators.put(object, new ObjectTranslator(object, objectId.asExpression()));
        }
    }

    private void translateObjects() {
        for (final ObjectDeclaration object : domain.getObjects()) {
            getObjectTranslator(object).translate();
        }
    }

    private void translatePolymorphisms() {
        for (final ObjectDeclaration object : domain.getObjects()) {
            getObjectTranslator(object).translatePolymorphisms();
        }
    }

    private void addTerminators() {
        final EnumerationType termsEnum = new EnumerationType("TerminatorIds", DomainNamespace.get(domain));

        interfaceDomainHeader.addEnumerateDeclaration(termsEnum);

        // Create the terminator translators before running them, so that any
        // circular
        // references are avoided.
        for (final DomainTerminator terminator : domain.getTerminators()) {
            final Enumerator
                    terminatorId =
                    termsEnum.addEnumerator("terminatorId_" + Mangler.mangleName(terminator), null);
            terminatorTranslators.put(terminator, new TerminatorTranslator(terminator, terminatorId.asExpression()));
        }
    }

    private void translateTerminators() {
        for (final DomainTerminator terminator : domain.getTerminators()) {
            getTerminatorTranslator(terminator).translate();
        }
    }

    private void translateObjectCode() {
        for (final ObjectDeclaration object : domain.getObjects()) {
            getObjectTranslator(object).translateCode();
        }
    }

    private void translateTerminatorCode() {
        for (final DomainTerminator terminator : domain.getTerminators()) {
            getTerminatorTranslator(terminator).translateCode();
        }
    }

    private void addServices() {
        servicesEnum = new EnumerationType("ServiceIds", DomainNamespace.get(domain));
        interfaceDomainHeader.addEnumerateDeclaration(servicesEnum);

        for (final DomainService service : domain.getServices()) {
            final String enumName = "serviceId_" + Mangler.mangleName(service);
            final Enumerator serviceId = servicesEnum.addEnumerator(enumName, null);

            final DomainServiceTranslator translator = new DomainServiceTranslator(service, serviceId.asExpression());
            serviceTranslators.put(service, translator);
        }
    }

    private void addExceptions() {

        for (final ExceptionDeclaration exception : domain.getExceptions()) {
            final ExceptionTranslator translator = new ExceptionTranslator(exception);

            exceptionTranslators.put(exception, translator);
        }
    }

    public Class getExceptionClass(final ExceptionDeclaration exception) {
        ExceptionTranslator translator = exceptionTranslators.get(exception);
        if (translator == null) {
            // This is not the main domain, so just need the interface generating
            translator = new ExceptionTranslator(exception);
            exceptionTranslators.put(exception, translator);
        }

        return translator.getExceptionClass();
    }

    private final HashMap<ExceptionDeclaration, ExceptionTranslator> exceptionTranslators = new HashMap<>();

    private void translateServiceCode() {
        for (final DomainService service : domain.getServices()) {
            final DomainServiceTranslator translator = serviceTranslators.get(service);

            translator.translateCode();
        }
    }

    private void addTypes() {
        for (final TypeDeclaration declaration : domain.getTypes()) {
            types.defineType(declaration);
        }
    }

    private final Map<DomainService, DomainServiceTranslator> serviceTranslators = new HashMap<>();

    private final Map<ObjectDeclaration, ObjectTranslator> objectTranslators = new HashMap<>();

    private final Map<DomainTerminator, TerminatorTranslator> terminatorTranslators = new HashMap<>();

    private final Map<RelationshipDeclaration, RelationshipTranslator> relationshipTranslators = new HashMap<>();

    private final Function getDomain;
    private Function initialiseDomain;
    private Function initialiseInterface;
    private final Expression getDomainId;
    private final CodeFile bodyFile;
    private final CodeFile interfaceBodyFile;
    private final CodeFile domainHeader;
    private final CodeFile interfaceDomainHeader;
    private final CodeFile publicTypeHeaderFile;
    private final CodeFile privateTypeHeaderFile;
    private final CodeFile publicTypeBodyFile;
    private final CodeFile privateTypeBodyFile;
    private final CodeFile publicServicesHeaderFile;
    private final CodeFile privateServicesHeaderFile;
    private final CodeFile terminatorsHeaderFile;
    private EnumerationType servicesEnum;

    public EnumerationType getServicesEnum() {
        return servicesEnum;
    }

    public Function getGetDomain() {
        return getDomain;
    }

    public Expression getDomainId() {
        return getDomainId;
    }

    public Library getLibrary() {
        return library;
    }

    public Library getInterfaceLibrary() {
        return interfaceLibrary;
    }

    public CodeFile getBodyFile() {
        return bodyFile;
    }

    public CodeFile getDomainHeader() {
        return domainHeader;
    }

    public CodeFile getTypeBodyFile(final Visibility visibility) {
        return visibility == Visibility.PUBLIC ? publicTypeBodyFile : privateTypeBodyFile;
    }

    public CodeFile getTypeHeaderFile(final Visibility visibility) {
        return visibility == Visibility.PUBLIC ? publicTypeHeaderFile : privateTypeHeaderFile;
    }

    public CodeFile getServicesHeaderFile(final Visibility visibility) {
        return visibility == Visibility.PUBLIC ? publicServicesHeaderFile : privateServicesHeaderFile;
    }

    public CodeFile getTerminatorsHeaderFile() {
        return terminatorsHeaderFile;
    }

    public FileGroup getStandaloneExecutableSkeleton() {
        return standaloneExecutableSkeleton;
    }

    public boolean addRefIntegChecks() {
        return Boolean.parseBoolean(getProperties(DomainTranslator.class).getProperty("checkrefinteg", "true"));
    }

    public CodeFile getNativeStubs() {
        return Suppliers.memoize(() -> new Library("native").inBuildSet(buildSet).createBodyFile(NativeStubsFile)).get();
    }
}
