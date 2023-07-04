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
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.DurationType;

import java.util.ArrayList;
import java.util.List;

public class DelayStatement extends Statement implements org.xtuml.masl.metamodel.code.DelayStatement {

    private final Expression duration;

    public static DelayStatement create(final Position position, final Expression duration) {
        if (duration == null) {
            return null;
        }
        try {
            return new DelayStatement(position, duration);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public DelayStatement(final Position position, final Expression duration) throws SemanticError {
        super(position);

        if (!DurationType.createAnonymous().isAssignableFrom(duration)) {
            throw new SemanticError(SemanticErrorCode.DelayParameterNotDuration, duration.getPosition());
        }

        this.duration = duration;
    }

    @Override
    public Expression getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "delay " + duration + ";";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitDelayStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        final List<ASTNode> result = new ArrayList();
        result.add(duration);
        return result;
    }

}
