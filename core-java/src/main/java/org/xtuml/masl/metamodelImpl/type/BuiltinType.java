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
package org.xtuml.masl.metamodelImpl.type;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.NotFound;
import org.xtuml.masl.metamodelImpl.error.NotFoundGlobal;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BuiltinType extends BasicType implements org.xtuml.masl.metamodel.type.BuiltinType {

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitBuiltinType(this);
    }

    private enum Type {
        CHARACTER, WCHARACTER, STRING, WSTRING, BOOLEAN, BYTE, SMALLINTEGER, INTEGER, REAL, DEVICE, DURATION, TIMESTAMP, INSTANCE, EVENT, TIMER
    }

    private static final Map<String, Type> lookup = new HashMap<>();

    static {
        lookup.put("character", Type.CHARACTER);
        lookup.put("wcharacter", Type.WCHARACTER);
        lookup.put("string", Type.STRING);
        lookup.put("wstring", Type.WSTRING);
        lookup.put("boolean", Type.BOOLEAN);
        lookup.put("byte", Type.BYTE);
        lookup.put("integer", Type.SMALLINTEGER);
        lookup.put("long_integer", Type.INTEGER);
        lookup.put("real", Type.REAL);
        lookup.put("device", Type.DEVICE);
        lookup.put("duration", Type.DURATION);
        lookup.put("timestamp", Type.TIMESTAMP);
        lookup.put("instance", Type.INSTANCE);
        lookup.put("event", Type.EVENT);
        lookup.put("timer", Type.TIMER);

        // Alternative Names to support legacy MASL
        lookup.put("Text", Type.STRING);
        lookup.put("Real", Type.REAL);
        lookup.put("long_real", Type.REAL);
        lookup.put("Integer", Type.SMALLINTEGER);
        lookup.put("Boolean", Type.BOOLEAN);
        lookup.put("numeric", Type.SMALLINTEGER);
    }

    public static BuiltinType lookupName(final Position position, final String name, final boolean anonymous) {
        final Type type = lookup.get(name);
        if (type == null) {
            return null;
        } else {
            switch (type) {
                case CHARACTER:
                    return CharacterType.create(position, anonymous);
                case WCHARACTER:
                    return WCharacterType.create(position, anonymous);
                case STRING:
                    return StringType.create(position, anonymous);
                case WSTRING:
                    return WStringType.create(position, anonymous);
                case BOOLEAN:
                    return BooleanType.create(position, anonymous);
                case BYTE:
                    return ByteType.create(position, anonymous);
                case SMALLINTEGER:
                    return SmallIntegerType.create(position, anonymous);
                case INTEGER:
                    return IntegerType.create(position, anonymous);
                case REAL:
                    return RealType.create(position, anonymous);
                case DEVICE:
                    return DeviceType.create(position, anonymous);
                case DURATION:
                    return DurationType.create(position, anonymous);
                case TIMESTAMP:
                    return TimestampType.create(position, anonymous);
                case INSTANCE:
                    return AnyInstanceType.create(position, anonymous);
                case EVENT:
                    return EventType.create(position, anonymous);
                case TIMER:
                    return TimerType.create(position, anonymous);
                default:
                    assert false;
                    return null;
            }
        }
    }

    public static BuiltinType create(final Position position, final String name, final boolean anonymous) throws
                                                                                                          NotFound {
        final BuiltinType result = lookupName(position, name, anonymous);

        if (result == null) {
            throw new NotFoundGlobal(SemanticErrorCode.TypeNotFound, position, name);
        }
        return result;
    }

    protected BuiltinType(final Position position, final String text, final boolean anonymous) {
        super(position, anonymous);
        this.text = text;
    }

    @Override
    public boolean equals(final Object rhs) {
        return this.getClass().equals(rhs.getClass());
    }

    @Override
    public String getName() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return (isAnonymousType() ? "anonymous " : "") + text;
    }

    @Override
    public void setTypeDeclaration(final TypeDeclaration typeDeclaration) {
    }

    @Override
    public TypeDeclaration getTypeDeclaration() {
        // No declaration for builtin types
        return null;
    }

    @Override
    abstract public BasicType getPrimitiveType();

    @Override
    abstract public BuiltinType getBasicType();

    private final String text;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
