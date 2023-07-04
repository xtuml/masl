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
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.Domain;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.Alias;
import org.xtuml.masl.translate.Default;
import org.xtuml.masl.translate.build.FileGroup;
import org.xtuml.masl.translate.main.Mangler;
import org.xtuml.masl.translate.main.TerminatorServiceTranslator;

import java.util.*;
import java.util.stream.Collectors;

import static org.xtuml.masl.translate.metadata.Architecture.*;

@Alias("MetaData")
@Default
public class ProjectTranslator extends org.xtuml.masl.translate.ProjectTranslator {

    private final org.xtuml.masl.translate.main.ProjectTranslator mainProjectTranslator;

    public static ProjectTranslator getInstance(final Project project) {
        return getInstance(ProjectTranslator.class, project);
    }

    private ProjectTranslator(final Project project) {
        super(project);
        mainProjectTranslator = org.xtuml.masl.translate.main.ProjectTranslator.getInstance(project);
        this.library =
                new SharedLibrary(mainProjectTranslator.getProject().getProjectName() + "_metadata").inBuildSet(
                        mainProjectTranslator.getBuildSet()).withCCDefaultExtensions();

        library.addDependency(Architecture.metaDataLib);
    }

    /**
     * @return
     * @see org.xtuml.masl.translate.Translator#getPrerequisites()
     */
    @Override
    public Collection<org.xtuml.masl.translate.ProjectTranslator> getPrerequisites() {
        return Collections.<org.xtuml.masl.translate.ProjectTranslator>singletonList(mainProjectTranslator);
    }

    @Override
    public void translate() {
        addRegistration();

        Set<Domain> fullDomains = project.getDomains().stream().map(d -> d.getDomain()).collect(Collectors.toSet());
        Set<Domain>
                interfaceDomains =
                project.getDomains().stream().flatMap(d -> d.getDomain().getReferencedInterfaces().stream()).collect(
                        Collectors.toSet());
        interfaceDomains.removeAll(fullDomains);

        for (final Domain interfaceDomain : interfaceDomains) {
            getLibrary().addDependency(DomainTranslator.getInstance(interfaceDomain).getInterfaceLibrary());
        }

        for (final Domain fullDomain : fullDomains) {
            getLibrary().addDependency(DomainTranslator.getInstance(fullDomain).getLibrary());
        }
    }

    private void addRegistration() {
        codeFile = library.createBodyFile("MetaData" + Mangler.mangleFile(project));

        final Namespace namespace = new Namespace("init_" + Mangler.mangleName(project), new Namespace(""));

        final Function initMetaData = new Function("initProcessMetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(FundamentalType.BOOL));

        initMetaData.getCode().appendStatement(initialisationCode);
        initMetaData.getCode().appendStatement(new ReturnStatement(Literal.TRUE));
        codeFile.addFunctionDefinition(initMetaData);

        final Variable
                initialised =
                new Variable(new TypeUsage(FundamentalType.BOOL),
                             "initialised",
                             namespace,
                             initMetaData.asFunctionCall());
        codeFile.addVariableDefinition(initialised);

        initialisationCode.appendStatement(new Function("setName").asFunctionCall(processInstance,
                                                                                  false,
                                                                                  Literal.createStringLiteral(project.getProjectName())).asStatement());

        for (final ProjectDomain domain : project.getDomains()) {
            final Namespace domainNamespace = new Namespace(Mangler.mangleName(domain.getDomain()), namespace);
            final Expression
                    domainId =
                    org.xtuml.masl.translate.main.DomainTranslator.getInstance(domain.getDomain()).getDomainId();

            for (final ProjectTerminator terminator : domain.getTerminators()) {
                final Namespace
                        termNamespace =
                        new Namespace(Mangler.mangleName(terminator.getDomainTerminator()), domainNamespace);
                final Expression
                        terminatorId =
                        org.xtuml.masl.translate.main.TerminatorTranslator.getInstance(terminator.getDomainTerminator()).getTerminatorId();
                for (final ProjectTerminatorService service : terminator.getServices()) {

                    final TerminatorServiceTranslator
                            mainTranslator =
                            TerminatorServiceTranslator.getInstance(service.getDomainTerminatorService());

                    initialisationCode.appendStatement(new IfStatement(mainTranslator.getCheckOverride().asFunctionCall(),
                                                                       overrideTerminatorService.asFunctionCall(
                                                                               processInstance,
                                                                               false,
                                                                               domainId,
                                                                               terminatorId,
                                                                               getTerminatorServiceMetaData(service,
                                                                                                            termNamespace)).asStatement()));

                }
            }
        }

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

    private Expression getTerminatorServiceMetaData(final ProjectTerminatorService service, final Namespace namespace) {

        final Function initMetaData = new Function("get_" + Mangler.mangleName(service) + "_MetaData", namespace);
        initMetaData.setReturnType(new TypeUsage(serviceMetaData));

        codeFile.addFunctionDeclaration(initMetaData);
        codeFile.addFunctionDefinition(initMetaData);

        final CodeBlock serviceBlock = initMetaData.getCode();

        final TerminatorServiceTranslator
                mainTranslator =
                TerminatorServiceTranslator.getInstance(service.getDomainTerminatorService());

        final List<Expression> params = new ArrayList<Expression>();
        params.add(mainTranslator.getServiceId());
        params.add(projectTerminatorServiceFlag);
        params.add(Literal.createStringLiteral(service.getName()));
        if (service.getReturnType() != null) {
            params.add(Literal.createStringLiteral(service.getReturnType().toString()));
            params.add(TypeTranslator.getTypeMetaData(service.getReturnType()));
        }

        final List<Expression> lines = new ArrayList<Expression>();
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

        final Variable serviceTemp = new Variable(new TypeUsage(serviceMetaData), "service", params);

        serviceBlock.appendStatement(serviceTemp.asStatement());

        for (final ParameterDefinition param : service.getParameters()) {
            serviceBlock.appendStatement(addParameter.asFunctionCall(serviceTemp.asExpression(),
                                                                     false,
                                                                     TypeTranslator.getParameterMetaData(param)).asStatement());
        }

        for (final VariableDefinition variable : service.getLocalVariables()) {
            serviceBlock.appendStatement(addLocalVar.asFunctionCall(serviceTemp.asExpression(),
                                                                    false,
                                                                    TypeTranslator.getLocalVarMetaData(variable)).asStatement());
        }

        serviceBlock.appendStatement(new ReturnStatement(serviceTemp.asExpression()));

        return initMetaData.asFunctionCall();
    }

    private final StatementGroup initialisationCode = new StatementGroup();

    private final Library library;

    private CodeFile codeFile;

    public FileGroup getLibrary() {
        return library;
    }

    public CodeFile getCodeFile() {
        return codeFile;
    }

}
