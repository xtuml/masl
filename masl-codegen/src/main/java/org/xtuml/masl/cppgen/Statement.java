/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Superclass of all C++ Statements
 */
public abstract class Statement {

    /**
     * Writes the statement to the supplied Writer. It will be indented by the
     * suplied indent, and names will be resolved according to the current
     * namespace.
     *
     * @throws IOException
     */
    abstract void write(Writer writer, String indent, Namespace currentNamespace) throws IOException;

    /**
     * Calculates the set of forward declarations necessary for this statement to
     * compile
     *
     * @return the necessary declarations
     */
    Set<Declaration> getForwardDeclarations() {
        return new LinkedHashSet<>();
    }

    /**
     * Calculates the set of include files necessary for this statement to compile
     *
     * @return the necessary include files
     */
    Set<CodeFile> getIncludes() {
        return new LinkedHashSet<>();
    }

    void setParent(final Statement parent) {
        this.parent = parent;
    }

    void setParentFunction(final Function function) {
        this.function = function;
    }

    Statement getParent() {
        return parent;
    }

    Function getParentFunction() {
        if (function == null && parent != null) {
            return parent.getParentFunction();
        } else {
            return function;
        }
    }

    private Statement parent = null;

    private Function function = null;
}
