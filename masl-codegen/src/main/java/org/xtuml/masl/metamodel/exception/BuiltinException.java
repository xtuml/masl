/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel.exception;

public interface BuiltinException extends ExceptionReference {

    enum Type {
        PROGRAM_ERROR, DEADLOCK_ERROR, STORAGE_ERROR, CONSTRAINT_ERROR, RELATIONSHIP_ERROR, REFERENTIAL_ACCESS_ERROR, IOP_ERROR, IO_ERROR, OTHER_ERROR
    }

    Type getType();

}
