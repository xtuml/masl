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
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.translate.main.code.CodeTranslator;

import java.util.*;

public class DomainServiceTranslator {

    final DomainTranslator domainTranslator;

    public static DomainServiceTranslator getInstance(final DomainService service) {
        return DomainTranslator.getInstance(service.getDomain()).getServiceTranslator(service);
    }

    public DomainServiceTranslator(final DomainService service, final Expression serviceId) {
        this.domainTranslator = DomainTranslator.getInstance(service.getDomain());
        this.service = service;
        this.serviceId = serviceId;
        headerFile = domainTranslator.getServicesHeaderFile(service.getVisibility());
        scope = new Scope();
        scope.setDomainService(service);

        namespace = DomainNamespace.get(service.getDomain());
        name = Mangler.mangleName(service);
        function = new Function(name, getNamespace());

        function.setReturnType(domainTranslator.getTypes().getType(service.getReturnType()));
        headerFile.addFunctionDeclaration(function);

        for (final ParameterDefinition param : service.getParameters()) {
            final ParameterTranslator paramTrans = new ParameterTranslator(param, function);
            parameters.put(param, paramTrans);
            scope.addParameter(param, paramTrans.getVariable().asExpression());
        }

        if (!service.isFunction() && !service.isExternal() && !service.isScenario()) {
            final Class interceptorTagClass = new Class(function.getName() + "_tag", getNamespace());
            headerFile.addClassDeclaration(interceptorTagClass);
            final Class interceptorClass = Architecture.createServiceInterceptor(interceptorTagClass, function);

            final String interceptorName = "interceptor_" + Mangler.mangleName(service);
            interceptorType =
                    new TypedefType(interceptorName, getNamespace(), new TypeUsage(interceptorClass), headerFile);
            headerFile.addTypedefDeclaration(interceptorType);
        }
    }

    private final Expression serviceId;

    public CodeTranslator getCodeTranslator() {
        return code;
    }

    public Function getFunction() {
        return function;
    }

    public List<ParameterTranslator> getParameters() {
        return new ArrayList<ParameterTranslator>(parameters.values());
    }

    public ParameterTranslator getParameter(final ParameterDefinition param) {
        return parameters.get(param);
    }

    public Scope getScope() {
        return scope;
    }

    public void addLocalServiceRegistration(final CodeFile file, final Function localFn, final Namespace namespace) {
        addServiceRegistration(file, "local", new Function("registerLocal"), localFn, namespace);
    }

    public void addRemoteServiceRegistration(final CodeFile file, final Function remoteFn, final Namespace namespace) {
        addServiceRegistration(file, "remote", new Function("registerRemote"), remoteFn, namespace);
    }

    private void addServiceRegistration(final CodeFile file,
                                        final String varNamePrefix,
                                        final Function registerFunction,
                                        final Function forwardFunction,
                                        final Namespace registrationNamespace) {
        Variable registrationVar = null;
        if (interceptorType != null) {
            final Expression interceptorFnCall = interceptorType.asClass().callStaticFunction("instance");
            final Expression
                    initialValue =
                    registerFunction.asFunctionCall(interceptorFnCall, false, forwardFunction.asFunctionPointer());
            registrationVar =
                    new Variable(new TypeUsage(FundamentalType.BOOL, TypeUsage.Const),
                                 varNamePrefix + "ServiceRegistration_" + name,
                                 registrationNamespace,
                                 initialValue);
            registrationVar.setStatic(true);
            file.addVariableDefinition(registrationVar);
        }
    }

    void translateCode() {
        // Code may be null if no implementation file was provided, eg for native
        // functions. In this case leave definition to third party library.
        final CodeFile file;

        if (service.getCode() != null || service.getDeclarationPragmas().hasPragma("generated_code")) {
            file = domainTranslator.getLibrary().createBodyFile(Mangler.mangleFile(service));
        } else {
            file = domainTranslator.getNativeStubs();
        }

        file.addFunctionDefinition(function);
        addLocalServiceRegistration(file, function, getNamespace());

        if (service.getCode() != null) {
            code = CodeTranslator.createTranslator(service.getCode(), scope);
            function.getCode().appendStatement(code.getFullCode());
        }
    }

    private Expression getInvocation(final Function callFunction, final List<Expression> args) {
        if (interceptorType != null) {
            final Expression instanceFnCall = interceptorType.asClass().callStaticFunction("instance");
            final Expression getServiceFnCall = callFunction.asFunctionCall(instanceFnCall, false);
            return new FunctionObjectCall(getServiceFnCall, args);
        } else {
            return function.asFunctionCall(args);
        }
    }

    public Expression getInvocation(final List<Expression> args) {
        return getInvocation(new Function("callService"), args);
    }

    public Expression getInvocation(final Expression... args) {
        return getInvocation(Arrays.asList(args));
    }

    public Expression getLocalInvocation(final List<Expression> args) {
        return getInvocation(new Function("callLocal"), args);
    }

    public Expression getLocalInvocation(final Expression... args) {
        return getLocalInvocation(Arrays.asList(args));
    }

    public Expression getRemoteInvocation(final List<Expression> args) {
        return getInvocation(new Function("callRemote"), args);
    }

    public Expression getRemoteInvocation(final Expression... args) {
        return getRemoteInvocation(Arrays.asList(args));
    }

    public DomainService getService() {
        return service;
    }

    public TypedefType getServiceInterceptor() {
        return interceptorType;
    }

    public Expression getServiceId() {
        return serviceId;
    }

    private final DomainService service;

    private final Scope scope;

    private final Map<ParameterDefinition, ParameterTranslator>
            parameters =
            new LinkedHashMap<ParameterDefinition, ParameterTranslator>();

    private final Function function;

    private CodeTranslator code;

    private final CodeFile headerFile;

    private final String name;

    private TypedefType interceptorType;

    private final Namespace namespace;

    public DomainTranslator getDomainTranslator() {
        return domainTranslator;
    }

    public Namespace getNamespace() {
        return namespace;
    }

}
