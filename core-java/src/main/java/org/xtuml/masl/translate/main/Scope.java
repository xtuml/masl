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

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.metamodel.code.VariableDefinition;
import org.xtuml.masl.metamodel.common.ParameterDefinition;
import org.xtuml.masl.metamodel.domain.DomainService;
import org.xtuml.masl.metamodel.domain.DomainTerminatorService;
import org.xtuml.masl.metamodel.expression.FindParameterExpression;
import org.xtuml.masl.metamodel.object.ObjectService;
import org.xtuml.masl.metamodel.project.ProjectTerminatorService;
import org.xtuml.masl.metamodel.statemodel.State;
import org.xtuml.masl.translate.main.object.ObjectTranslator;

import java.util.HashMap;
import java.util.Map;

public final class Scope {

    private final Scope parentScope;
    private final ObjectTranslator parentObject;

    public Scope() {
        this.parentScope = null;
        this.parentObject = null;
    }

    public Scope(final Scope parentScope) {
        this.parentScope = parentScope;
        this.parentObject = null;
    }

    public Scope(final ObjectTranslator parentObject) {
        this.parentScope = null;
        this.parentObject = parentObject;
    }

    public ObjectTranslator getParentObject() {
        if (parentObject == null && parentScope != null) {
            return parentScope.getParentObject();
        }
        return parentObject;
    }

    public void addVariable(final VariableDefinition var, final Expression variable) {
        variables.put(var, variable);
    }

    public void addParameter(final ParameterDefinition param, final Expression parameter) {
        parameters.put(param, parameter);
    }

    public void addFindParameter(final FindParameterExpression param, final Expression parameter) {
        findParameters.put(param, parameter);
    }

    public Expression resolveVariable(final VariableDefinition def) {
        Expression var = variables.get(def);
        if (var == null && parentScope != null) {
            var = parentScope.resolveVariable(def);
        }
        return var;
    }

    public Expression resolveParameter(final ParameterDefinition param) {
        Expression par = parameters.get(param);
        if (par == null && parentScope != null) {
            par = parentScope.resolveParameter(param);
        }
        return par;
    }

    public Expression resolveFindParameter(final FindParameterExpression param) {
        Expression par = findParameters.get(param);
        if (par == null && parentScope != null) {
            par = parentScope.resolveFindParameter(param);
        }
        return par;
    }

    private final Map<VariableDefinition, Expression> variables = new HashMap<VariableDefinition, Expression>();
    private final Map<ParameterDefinition, Expression> parameters = new HashMap<ParameterDefinition, Expression>();
    private final Map<FindParameterExpression, Expression>
            findParameters =
            new HashMap<FindParameterExpression, Expression>();

    public void setObjectService(final ObjectService service) {
        objectService = service;
    }

    public ObjectService getObjectService() {
        if (objectService == null && parentScope != null) {
            return parentScope.getObjectService();
        } else {
            return objectService;
        }

    }

    private ObjectService objectService;

    public void setDomainService(final DomainService service) {
        domainService = service;
    }

    public DomainService getDomainService() {
        if (domainService == null && parentScope != null) {
            return parentScope.getDomainService();
        } else {
            return domainService;
        }

    }

    private DomainService domainService;

    public void setTerminatorService(final DomainTerminatorService service) {
        terminatorService = service;
    }

    public DomainTerminatorService getTerminatorService() {
        if (terminatorService == null && parentScope != null) {
            return parentScope.getTerminatorService();
        } else {
            return terminatorService;
        }

    }

    private DomainTerminatorService terminatorService;

    public void setProjectTerminatorService(final ProjectTerminatorService service) {
        projectTerminatorService = service;
    }

    public ProjectTerminatorService getProjectTerminatorService() {
        if (projectTerminatorService == null && parentScope != null) {
            return parentScope.getProjectTerminatorService();
        } else {
            return projectTerminatorService;
        }

    }

    private ProjectTerminatorService projectTerminatorService;

    public void setState(final State state) {
        this.state = state;
    }

    public State getState() {
        if (state == null && parentScope != null) {
            return parentScope.getState();
        } else {
            return state;
        }

    }

    private State state;

}
