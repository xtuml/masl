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
package org.xtuml.masl.cppgen;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The superclass of all C++ declarations
 */
abstract class Declaration {

    @Override
    public String toString() {
        final Writer writer = new StringWriter();
        try {
            writeDeclaration(writer, "", null);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Sets the file that this class is declared in. This may be set when the
     * declaration is added to a code file.
     * <p>
     * <p>
     * the include file
     */
    void addDeclaredIn(final CodeFile declaredIn) {
        this.usageIncludes.add(declaredIn);
    }

    /**
     * Sets the files that this class is declared in. This may be set when the
     * declaration is added to a code file.
     * <p>
     * <p>
     * the include files
     */
    void addDeclaredIn(final Set<CodeFile> declaredIn) {
        this.usageIncludes.addAll(declaredIn);
    }

    /**
     * Calculates the set of forward declarations required to compile this
     * declaration.
     *
     * @return the required forward declarations
     */
    Set<Declaration> getForwardDeclarations() {
        return new LinkedHashSet<>();
    }

    /**
     * Returns the set of include files needed to compile this declaration.
     *
     * @return the required include files
     */
    Set<CodeFile> getIncludes() {
        return new LinkedHashSet<>();
    }

    /**
     * If this declaration is a member of a class, then returns that class.
     * Otherwise returns null.
     *
     * @return the class containing this declaration
     */
    Class getParentClass() {
        return parentClass;
    }

    /**
     * If this declaration is a member of a class, returns the namespace defined by
     * that class. If the declaration is contained in a namespace, returns that
     * namespace.
     *
     * @return the namespace containing this declaration
     */
    Namespace getParentNamespace() {
        return parentNamespace;
    }

    /**
     * Calculates the set of include files needed to make use of this declaration.
     * This would typically just be the incldue file containing the actual
     * declaration. If this declaration is a member of a class, then the declaration
     * of that class is needed, and hence its include file should be returned.
     *
     * @return the include files needed
     */
    Set<CodeFile> getUsageIncludes() {
        if (parentClass != null) {
            return parentClass.getDeclaration().getUsageIncludes();
        }
        return new LinkedHashSet<>(usageIncludes);
    }

    /**
     * Returns the visibility of the declaration. Only relevant to member
     * declarations.
     *
     * @return the visibility
     */
    Visibility getVisibility() {
        return visibility;
    }

    /**
     * Sets this declaration as a member of a class
     * <p>
     * <p>
     * the class the declaration is a member of
     */
    void setParentClass(final Class parentClass) {
        this.parentClass = parentClass;
        this.parentNamespace = parentClass.getNamespace();
    }

    /**
     * Sets this declaration as a member of a namespace
     * <p>
     * <p>
     * the declaration is a member of
     */
    void setParentNamespace(final Namespace namespace) {
        parentNamespace = namespace;
    }

    /**
     * Sets the visibility of the declaration.
     * <p>
     * <p>
     * the required visibility
     */
    void setVisibility(final Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * Writes the declaration to the supplied writer.
     * <p>
     * <p>
     * the writer to write to
     * <p>
     * the initial indentation level for the code
     * <p>
     * the namespace that the declaration is being written to.
     *
     * @throws IOException
     */
    abstract void writeDeclaration(Writer writer, String indent, Namespace currentNamespace) throws IOException;

    /**
     * Writes a forward declaration of the current declaration to the supplied
     * writer.
     * <p>
     * <p>
     * the writer to write to
     * <p>
     * the initial indentation level for the code
     * <p>
     * the namespace that the declaration is being written to.
     *
     * @throws IOException
     */
    abstract void writeForwardDeclaration(Writer writer, String indent, Namespace currentNamespace) throws IOException;

    /**
     * The class that this declaration is a member of
     */
    private Class parentClass;

    /**
     * The namespace containing this declaration
     */
    private Namespace parentNamespace = null;

    /**
     * The set of include files needed to make use of this declaration. This would
     * typically just be the incldue file containing the actual declaration.
     */
    private final Set<CodeFile> usageIncludes = new LinkedHashSet<>();

    /**
     * The visibility of this declaration. Only relevant if it is a member class.
     */
    private Visibility visibility = Visibility.PUBLIC;

}
