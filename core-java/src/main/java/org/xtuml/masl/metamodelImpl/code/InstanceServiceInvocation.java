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

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.object.ObjectService;

import java.util.List;

public class InstanceServiceInvocation extends ServiceInvocation<ObjectService>
        implements org.xtuml.masl.metamodel.code.InstanceServiceInvocation {

    private final Expression instance;

    public InstanceServiceInvocation(final Position position,
                                     final Expression instance,
                                     final ObjectService service,
                                     final List<Expression> arguments) {
        super(position, service, arguments);
        this.instance = instance;
    }

    @Override
    public Expression getInstance() {
        return this.instance;
    }

    @Override
    protected String getCallPrefix() {
        return instance + "." + getService().getName();
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        return v.visitInstanceServiceInvocation(this, p);
    }

}
