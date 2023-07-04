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
package org.xtuml.masl.metamodelImpl.exception;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodelImpl.common.Position;
import org.xtuml.masl.metamodelImpl.error.NotFoundGlobal;
import org.xtuml.masl.metamodelImpl.error.SemanticError;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuiltinException extends ExceptionReference
        implements org.xtuml.masl.metamodel.exception.BuiltinException {

    private enum ImplType {
        PROGRAM_ERROR("program_error", Type.PROGRAM_ERROR), DEADLOCK_ERROR("deadlock_error",
                                                                           Type.DEADLOCK_ERROR), STORAGE_ERROR(
                "storage_error",
                Type.STORAGE_ERROR), CONSTRAINT_ERROR("constraint_error", Type.CONSTRAINT_ERROR), RELATIONSHIP_ERROR(
                "relationship_error",
                Type.RELATIONSHIP_ERROR), REFERENTIAL_ACCESS_ERROR("referential_access_error",
                                                                   Type.REFERENTIAL_ACCESS_ERROR), IOP_ERROR("iop_error",
                                                                                                             Type.IOP_ERROR), IO_ERROR(
                "io_error",
                Type.IO_ERROR);

        ImplType(final String name, final Type type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return name;
        }

        String getName() {
            return name;
        }

        Type getType() {
            return type;
        }

        private final String name;
        private final Type type;
    }

    static Map<String, ImplType> lookup = new HashMap<>();

    static {
        for (final ImplType type : ImplType.values()) {
            lookup.put(type.getName(), type);
        }
    }

    public static BuiltinException create(final Position position, final String name) throws SemanticError {
        final ImplType type = lookup.get(name);
        if (type == null) {
            throw new NotFoundGlobal(SemanticErrorCode.ExceptionNotFound, position, name);
        } else {
            return new BuiltinException(position, type);
        }
    }

    private BuiltinException(final Position position, final ImplType type) {
        super(position);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type.getType();
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    ImplType type;

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
