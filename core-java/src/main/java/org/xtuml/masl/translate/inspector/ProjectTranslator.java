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
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.main.Boost;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;

import java.util.Collection;
import java.util.Collections;

@Alias("Inspector")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator {

    private final org.xtuml.masl.translate.main.ProjectTranslator mainTranslator;

    private final Namespace projectNamespace;

    public static ProjectTranslator getInstance(final Project project) {
        return getInstance(ProjectTranslator.class, project);
    }

    public Namespace getNamespace() {
        return projectNamespace;
    }

    private ProjectTranslator(final Project project) {
        super(project);
        mainTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
        this.projectNamespace = new Namespace(Mangler.mangleName(project), Inspector.inspectorNamespace);
        this.library =
                new SharedLibrary(project.getProjectName() +
                                  "_inspector").inBuildSet(mainTranslator.getBuildSet()).withCCDefaultExtensions();
    }

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites() {
        return Collections.singletonList(mainTranslator);
    }

    @Override
    public void translate() {

        library.addDependency(Inspector.library);

        addRegistration();
        final Class actionPtrType = Boost.getSharedPtrType(new TypeUsage(Inspector.actionHandlerClass));

        for (final ProjectDomain domain : project.getDomains()) {
            final Namespace domainNamespace = new Namespace(Mangler.mangleName(domain.getDomain()), projectNamespace);
            final org.xtuml.masl.translate.main.DomainTranslator
                    mainDomainTranslator =
                    org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain.getDomain());

            for (final ProjectTerminator terminator : domain.getTerminators()) {
                final Namespace
                        termNamespace =
                        new Namespace(Mangler.mangleName(terminator.getDomainTerminator()), domainNamespace);
                final org.xtuml.masl.translate.main.TerminatorTranslator
                        mainTerminatorTranslator =
                        org.xtuml.masl.translate.main.TerminatorTranslator.getInstance(terminator.getDomainTerminator());

                final Expression
                        termHandler =
                        Inspector.getTerminatorHandler(mainDomainTranslator.getDomainId(),
                                                       mainTerminatorTranslator.getTerminatorId());

                for (final ProjectTerminatorService service : terminator.getServices()) {
                    final TerminatorServiceTranslator
                            mainTranslator =
                            TerminatorServiceTranslator.getInstance(service.getDomainTerminatorService());
                    final ActionTranslator trans = new ActionTranslator(service, this, termNamespace);
                    trans.translate();
                    final CodeBlock registerBlock = new CodeBlock();
                    final Statement
                            ifOverridden =
                            new IfStatement(mainTranslator.getCheckOverride().asFunctionCall(), registerBlock);

                    registerBlock.appendStatement(new Function("overrideServiceHandler").asFunctionCall(termHandler,
                                                                                                        false,
                                                                                                        mainTranslator.getServiceId(),
                                                                                                        actionPtrType.callConstructor(
                                                                                                                new NewExpression(
                                                                                                                        new TypeUsage(
                                                                                                                                trans.getHandlerClass())))).asStatement());

                    initialisationCode.appendStatement(ifOverridden);

                }
            }

            for (final Domain interfaceDomain : mainTranslator.getInterfaceDomains()) {
                getLibrary().addDependency(DomainTranslator.getInstance(interfaceDomain).getInterfaceLibrary());
            }

            for (final Domain fullDomain : mainTranslator.getFullDomains()) {
                getLibrary().addDependency(DomainTranslator.getInstance(fullDomain).getLibrary());
            }
        }

    }

    private void addRegistration() {
        codeFile = library.createBodyFile("Inspector" + Mangler.mangleFile(project));

        final Namespace namespace = new Namespace("");

        final Function initInspector = new Function("initProcessInspector", namespace);
        initInspector.setReturnType(new TypeUsage(FundamentalType.BOOL));

        initInspector.getCode().appendStatement(initialisationCode);
        initInspector.getCode().appendStatement(new ReturnStatement(Literal.TRUE));
        codeFile.addFunctionDefinition(initInspector);

        final Variable
                initialised =
                new Variable(new TypeUsage(FundamentalType.BOOL),
                             "initialised",
                             namespace,
                             initInspector.asFunctionCall());
        codeFile.addVariableDefinition(initialised);

    }

    private final StatementGroup initialisationCode = new StatementGroup();

    public Library getLibrary() {
        return library;
    }

    private CodeFile codeFile;
    private final Library library;

    public org.xtuml.masl.translate.main.ProjectTranslator getMainTranslator() {
        return mainTranslator;
    }

    public CodeFile getCodeFile() {
        return codeFile;
    }

}
