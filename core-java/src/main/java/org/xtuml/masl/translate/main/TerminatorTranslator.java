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

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.cppgen.EnumerationType.Enumerator;
import org.xtuml.masl.metamodel.domain.DomainTerminator;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;

import java.util.HashMap;
import java.util.Map;

public class TerminatorTranslator {

    public TerminatorTranslator(final DomainTerminator terminator,
                                final org.xtuml.masl.cppgen.Expression terminatorId) {
        domainTranslator = DomainTranslator.getInstance(terminator.getDomain());
        this.terminator = terminator;
        this.terminatorId = terminatorId;

        headerFile = domainTranslator.getTerminatorsHeaderFile();
        bodyFile = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(terminator));

        namespace = DomainNamespace.get(terminator.getDomain());

        name = Mangler.mangleName(terminator);
        mainClass = new Class(name, namespace);

        terminatorServices = mainClass.createDeclarationGroup("Terminator Services");
        serviceRegistration = mainClass.createDeclarationGroup("Service Registration");
        overrideChecks = mainClass.createDeclarationGroup("Override Checks");

        singletonGroup = mainClass.createDeclarationGroup("Singleton");
        domainServices = mainClass.createDeclarationGroup("Domain Defined Services");
        overriddenServices = mainClass.createDeclarationGroup("Overriden Services");
        idEnums = mainClass.createDeclarationGroup("Id Enumerations");

        headerFile.addClassDeclaration(mainClass);

        addGetInstance();
        constructor = mainClass.createConstructor(singletonGroup, Visibility.PRIVATE);
        bodyFile.addFunctionDefinition(constructor);

    }

    void addGetInstance() {
        getInstance = mainClass.createStaticFunction(singletonGroup, "getInstance", Visibility.PRIVATE);
        getInstance.setReturnType(new TypeUsage(mainClass, TypeUsage.Reference));
        final Variable instance = new Variable(new TypeUsage(mainClass), "instance");
        instance.setStatic(true);

        getInstance.getCode().appendStatement(instance.asStatement());
        getInstance.getCode().appendStatement(new ReturnStatement(instance.asExpression()));
        bodyFile.addFunctionDefinition(getInstance);
    }

    Function getConstructor() {
        return constructor;
    }

    private Function getInstance;
    private final CodeFile bodyFile;
    private final DeclarationGroup terminatorServices;
    private final DeclarationGroup serviceRegistration;
    private final DeclarationGroup domainServices;
    private final DeclarationGroup overriddenServices;
    private final DeclarationGroup overrideChecks;
    private final DeclarationGroup singletonGroup;
    private final DeclarationGroup idEnums;
    private final CodeFile headerFile;
    private final Function constructor;

    private final String name;
    private final Class mainClass;
    private final Namespace namespace;

    private final org.xtuml.masl.cppgen.Expression terminatorId;

    public org.xtuml.masl.cppgen.Expression getTerminatorId() {
        return terminatorId;
    }

    public void translate() {
        addServices();
    }

    public void translateCode() {
        translateServiceCode();
    }

    private void translateServiceCode() {
        for (final DomainTerminatorService service : terminator.getServices()) {
            serviceTranslators.get(service).translateCode();
        }
    }

    private void addServices() {
        final EnumerationType servicesEnum = new EnumerationType("ServiceIds");
        mainClass.addEnumeration(idEnums, servicesEnum, Visibility.PUBLIC);

        for (final DomainTerminatorService service : terminator.getServices()) {
            final Enumerator serviceId = servicesEnum.addEnumerator("serviceId_" + Mangler.mangleName(service), null);

            final TerminatorServiceTranslator
                    translator =
                    new TerminatorServiceTranslator(this, service, serviceId.asExpression());
            serviceTranslators.put(service, translator);
        }
    }

    public TerminatorServiceTranslator getServiceTranslator(final DomainTerminatorService service) {
        return serviceTranslators.get(service);
    }

    private final Map<DomainTerminatorService, TerminatorServiceTranslator> serviceTranslators = new HashMap<>();
    private final DomainTranslator domainTranslator;
    private final DomainTerminator terminator;

    public DomainTranslator getDomainTranslator() {
        return domainTranslator;
    }

    public static TerminatorTranslator getInstance(final DomainTerminator terminator) {
        return DomainTranslator.getInstance(terminator.getDomain()).getTerminatorTranslator(terminator);
    }

    public CodeFile getBodyFile() {
        return bodyFile;
    }

    public CodeFile getHeaderFile() {
        return headerFile;
    }

    public Class getMainClass() {
        return mainClass;
    }

    DeclarationGroup getTerminatorServices() {
        return terminatorServices;
    }

    DeclarationGroup getServiceRegistration() {
        return serviceRegistration;
    }

    DeclarationGroup getOverriddenServices() {
        return overriddenServices;
    }

    DeclarationGroup getDomainServices() {
        return domainServices;
    }

    Expression getGetInstance() {
        return getInstance.asFunctionCall();
    }

    public DeclarationGroup getOverrideChecks() {
        return overrideChecks;
    }

}
