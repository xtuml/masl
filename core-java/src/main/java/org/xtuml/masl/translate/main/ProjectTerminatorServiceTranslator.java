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

import org.xtuml.masl.cppgen.*;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.project.Project;
import org.xtuml.masl.metamodel.project.ProjectDomain;
import org.xtuml.masl.metamodel.project.ProjectTerminator;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.translate.main.code.CodeTranslator;

import java.util.ArrayList;
import java.util.List;

public class ProjectTerminatorServiceTranslator {

    private final ProjectTerminatorService service;
    private final Scope scope;
    private final List<ParameterTranslator> parameters = new ArrayList<ParameterTranslator>();
    private CodeTranslator code;
    private final Function function;
    private final ProjectTranslator projectTranslator;

    private final CodeFile bodyFile;

    public ProjectTerminatorServiceTranslator(final ProjectTranslator projectTranslator,
                                              final ProjectTerminatorService service) {
        this.service = service;
        this.projectTranslator = projectTranslator;
        scope = new Scope();
        scope.setProjectTerminatorService(service);

        final ProjectTerminator terminator = service.getTerminator();
        final ProjectDomain domain = terminator.getDomain();
        final Project project = domain.getProject();

        final String filename = Mangler.mangleFile(service);

        bodyFile = projectTranslator.getLibrary().createBodyFile(filename);
        final CodeFile headerFile = projectTranslator.getLibrary().createPrivateHeader(filename);

        final Namespace prjNamespace = projectTranslator.getNamespace();
        final Namespace domainNamespace = new Namespace(Mangler.mangleName(domain.getDomain()), prjNamespace);
        final Namespace
                termNamespace =
                new Namespace(Mangler.mangleName(terminator.getDomainTerminator()), domainNamespace);

        function = new Function(Mangler.mangleName(service), termNamespace);
        headerFile.addFunctionDeclaration(function);

        function.setReturnType(Types.getInstance().getType(service.getReturnType()));
        for (final ParameterDefinition param : service.getParameters()) {
            final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
            parameters.add(paramTrans);
            scope.addParameter(param, paramTrans.getVariable().asExpression());
        }

        // Register the project terminator with the domain
        final Function
                overrider =
                TerminatorServiceTranslator.getInstance(service.getDomainTerminatorService()).getRegisterOverride();
        final Variable
                register =
                new Variable(new TypeUsage(FundamentalType.BOOL),
                             "register_" + Mangler.mangleName(service),
                             termNamespace,
                             overrider.asFunctionCall(function.asFunctionPointer()));
        bodyFile.addVariableDefinition(register);
    }

    public CodeTranslator getCodeTranslator() {
        return code;
    }

    public Function getFunction() {
        return function;
    }

    public List<ParameterTranslator> getParameters() {
        return parameters;
    }

    public Scope getScope() {
        return scope;
    }

    void translateCode() {
        CodeFile file;
        if (service.getCode() != null || service.getDeclarationPragmas().hasPragma("generated_code")) {
            file = bodyFile;
        } else {
            file = projectTranslator.getNativeStubs();
        }
        file.addFunctionDefinition(function);

        // Code may be null if no implementation file was provided, eg for native
        // functions. In this case leave definition to third party library.
        if (service.getCode() != null) {
            code = CodeTranslator.createTranslator(service.getCode(), scope);
            function.getCode().appendStatement(code.getFullCode());
        }

    }

    public ProjectTerminatorService getService() {
        return service;
    }

    public static ProjectTerminatorServiceTranslator getInstance(final ProjectTerminatorService service) {
        return ProjectTranslator.getInstance(service.getTerminator().getDomain().getProject()).getServiceTranslator(
                service);
    }

}
