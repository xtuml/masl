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

import org.xtuml.masl.metamodel.ASTNodeVisitor;

public class InternalType extends BasicType {

    public final static InternalType OBJECT = new InternalType("object");
    public final static InternalType SERVICE = new InternalType("service");
    public final static InternalType TYPE = new InternalType("type");
    public final static InternalType AMBIGUOUS_ENUM = new InternalType("enum");
    public final static InternalType STREAM_MODIFIER = new InternalType("stream_modifier");
    public static final InternalType TERMINATOR = new InternalType("terminator");
    public static final InternalType SPLIT = new InternalType("split");
    public static final InternalType CHARACTERISTIC = new InternalType("characteristic");

    private InternalType(final String name) {
        super(null, true);
        this.name = name;
    }

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public InternalType getBasicType() {
        return this;
    }

    @Override
    public InternalType getPrimitiveType() {
        return this;
    }

    @Override
    public ActualType getActualType() {
        return null;
    }

    @Override
    public <R, P> R accept(final ASTNodeVisitor<R, P> v, final P p) throws Exception {
        throw new IllegalStateException("Cannot Visit Internal Type");
    }

}
