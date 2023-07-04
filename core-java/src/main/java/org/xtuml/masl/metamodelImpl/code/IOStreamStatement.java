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
import org.xtuml.masl.metamodelImpl.common.Positioned;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.type.DeviceType;
import org.xtuml.masl.utils.TextUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IOStreamStatement extends Statement implements org.xtuml.masl.metamodel.code.IOStreamStatement {

    public static IOExpression createExpression(final Position position,
                                                final ImplType type,
                                                final Expression expression) {
        return new IOExpression(position, type, expression);
    }

    public static IOStreamStatement create(final Expression expression, final List<IOExpression> args) {
        if (expression == null || args == null) {
            return null;
        }

        try {
            return new IOStreamStatement(expression, args);
        } catch (final SemanticError e) {
            e.report();
            return null;
        }
    }

    public enum ImplType {
        OUT("<<", Type.OUT), IN(">>", Type.IN), LINE_OUT("<<<", Type.LINE_OUT), LINE_IN(">>>", Type.LINE_IN);

        private final String operator;
        private final Type type;

        ImplType(final String operator, final Type type) {
            this.operator = operator;
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return operator;
        }
    }

    public static class IOExpression extends Positioned
            implements org.xtuml.masl.metamodel.code.IOStreamStatement.IOExpression {

        @Override
        public Expression getExpression() {
            return expression;
        }

        @Override
        public Type getType() {
            return type.getType();
        }

        private final Expression expression;
        private final ImplType type;

        public IOExpression(final Position position, final ImplType type, final Expression expression) {
            super(position);
            this.expression = expression;
            this.type = type;
        }

        @Override
        public String toString() {
            return " " + type + " " + expression;
        }

    }

    public IOStreamStatement(final Expression streamName, final List<IOExpression> arguments) throws SemanticError {
        super(streamName.getPosition());

        if (!DeviceType.createAnonymous().isAssignableFrom(streamName)) {
            throw new SemanticError(SemanticErrorCode.ExpectedDeviceForStream,
                                    streamName.getPosition(),
                                    streamName.getType());
        }

        this.streamName = streamName;
        this.arguments = arguments;
    }

    @Override
    public List<IOExpression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public Expression getStreamName() {
        return this.streamName;
    }

    private final Expression streamName;
    private final List<IOExpression> arguments;

    @Override
    public String toString() {
        return streamName + TextUtils.formatList(arguments, "", "", "") + ";";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitIOStreamStatement(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(streamName, arguments.stream().map(a -> a.expression).collect(Collectors.toList()));
    }

}
