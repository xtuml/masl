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
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.domain.DomainService;

import java.util.List;

public class DomainFunctionInvocation extends FunctionInvocation<DomainService>
        implements org.xtuml.masl.metamodel.expression.DomainFunctionInvocation {

    public DomainFunctionInvocation(final Position position,
                                    final DomainService service,
                                    final List<Expression> arguments) {
        super(position, service, arguments);
    }

    @Override
    public Expression getFindSkeletonInner() {
        return new DomainFunctionInvocation(getPosition(), getService(), getFindSkeletonArguments());
    }

    @Override
    protected String getCallPrefix() {
        return getService().getQualifiedName();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DomainFunctionInvocation obj2)) {
            return false;
        } else {

            return getService().equals(obj2.getService()) && getArguments().equals(obj2.getArguments());
        }
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDomainFunctionInvocation(this);
    }

}
