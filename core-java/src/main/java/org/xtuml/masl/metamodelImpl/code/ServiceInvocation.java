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
package org.xtuml.masl.metamodelImpl.code;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.common.ParameterDefinition;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.common.Service;
import org.xtuml.masl.metamodelImpl.common.ServiceOverload;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.domain.DomainService;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminator;
import org.xtuml.masl.metamodelImpl.domain.DomainTerminatorService;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectService;
import org.xtuml.masl.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ServiceInvocation<ServiceType extends Service> extends Statement {

    public static ServiceInvocation<? extends Service> create(final Position position,
                                                              final Expression lhs,
                                                              final List<Expression> args) {
        if (lhs == null || args == null || args.contains(null)) {
            return null;
        }

        try {
            if (lhs instanceof Domain.ServiceExpression) {
                final ServiceOverload<DomainService> service = ((Domain.ServiceExpression) lhs).getOverload();
                final DomainService
                        matchingService =
                        service.getCallable(position, ServiceOverload.ServiceType.Service, args);
                final List<Expression> newArgs = new ArrayList<>();
                for (int i = 0; i < args.size(); ++i) {
                    if (args.get(i) != null) {
                        newArgs.add(args.get(i).resolve(matchingService.getParameters().get(i).getType()));
                    }
                }

                return new DomainServiceInvocation(position, matchingService, newArgs);
            } else if (lhs instanceof DomainTerminator.ServiceExpression) {
                final ServiceOverload<DomainTerminatorService>
                        service =
                        ((DomainTerminator.ServiceExpression) lhs).getOverload();
                final DomainTerminatorService
                        matchingService =
                        service.getCallable(position, ServiceOverload.ServiceType.Service, args);
                final List<Expression> newArgs = new ArrayList<>();
                for (int i = 0; i < args.size(); ++i) {
                    if (args.get(i) != null) {
                        newArgs.add(args.get(i).resolve(matchingService.getParameters().get(i).getType()));
                    }
                }

                return new TerminatorServiceInvocation(position, matchingService, newArgs);
            } else if (lhs instanceof ObjectDeclaration.ServiceExpression) {
                final ObjectDeclaration.ServiceExpression service = ((ObjectDeclaration.ServiceExpression) lhs);
                final ObjectService
                        matchingService =
                        service.getOverload().getCallable(position, ServiceOverload.ServiceType.Service, args);

                final Expression instance = service.getLhs();

                final List<Expression> newArgs = new ArrayList<>();
                for (int i = 0; i < args.size(); ++i) {
                    if (args.get(i) != null) {
                        newArgs.add(args.get(i).resolve(matchingService.getParameters().get(i).getType()));
                    }
                }
                if (instance == null) {
                    return new ObjectServiceInvocation(position, matchingService, newArgs);
                } else {
                    return new InstanceServiceInvocation(position, instance, matchingService, newArgs);
                }
            } else {
                throw new SemanticError(SemanticErrorCode.NoServiceCall, position);
            }
        } catch (final SemanticError e) {
            e.report();
            return null;
        }

    }

    public static void create() {
    }

    private final List<Expression> arguments;
    private final ServiceType service;

    public ServiceInvocation(final Position position, final ServiceType service, final List<Expression> arguments) {
        super(position);
        this.service = service;
        this.arguments = arguments;

        checkOutParams();

    }

    public List<Expression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public ServiceType getService() {
        return service;
    }

    protected abstract String getCallPrefix();

    @Override
    public String toString() {
        return getCallPrefix() + "(" + TextUtils.formatList(arguments, "", ", ", "") + ")";
    }

    protected void checkOutParams() {
        int i = 0;
        for (final ParameterDefinition parameter : service.getParameters()) {
            if (parameter.getMode() == ParameterDefinition.Mode.OUT) {
                final Expression arg = arguments.get(i);
                try {
                    arg.checkWriteable(arg.getPosition());
                } catch (final SemanticError e) {
                    e.report();
                }
            }
            ++i;
        }
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(arguments);
    }

}
