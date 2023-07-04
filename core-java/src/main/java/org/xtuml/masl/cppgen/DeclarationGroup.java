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

import org.xtuml.masl.utils.TextUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows child declarations in a class to be grouped together. Each group may
 * have a mixture of public, protected and private members, and is headed by a
 * comment if required. Instances of this class are created by
 * {@link Class#createDeclarationGroup()} or
 * {@link Class#createDeclarationGroup(String)}.
 */
public class DeclarationGroup {

    /**
     * Creates a declaration group ready for declarations to be added.
     */
    DeclarationGroup() {
        this(null);
    }

    /**
     * Creates a declaration group with the supplied comment
     * <p>
     * <p>
     * the comment to display at the top of the group. No comment will be displayed
     * if this is null.
     */
    DeclarationGroup(final String comment) {
        if (comment != null) {
            startComment = Comment.createComment(comment);
        }
    }

    @Override
    public String toString() {
        final Writer writer = new StringWriter();
        try {
            write(writer, "", null);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Adds a declaration to the group. Declarations will be written in the same
     * order that they were added.
     */
    void add(final Declaration declaration) {
        declarations.add(declaration);
    }

    /**
     * Adds a variable to the group. The variable list is maintained in the same
     * order as the declarations, and is used by the constructor code to initialise
     * the variable values.
     * <p>
     * <p>
     * The variable to add to the list
     */
    void addVariable(final Variable variable) {
        variables.add(variable);
    }

    /**
     * Gets the list of declarations that were added to this group in the order they
     * were added.
     *
     * @return the list of declarations
     */
    List<Declaration> getDeclarations() {
        return new ArrayList<>(declarations);
    }

    /**
     * Finds all the forward declarations required to make all the child
     * declarations.
     *
     * @return a set of required include files
     */
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = new LinkedHashSet<>();

        for (final Declaration dec : declarations) {
            result.addAll(dec.getForwardDeclarations());
        }

        return result;
    }

    /**
     * Finds all the include files required to make all the child declarations.
     *
     * @return a set of required include files
     */
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = new LinkedHashSet<>();

        for (final Declaration dec : declarations) {
            result.addAll(dec.getIncludes());
        }

        return result;
    }

    /**
     * Gets the list of variables declared in this group, which is used by the
     * constructor code to initialise the variable values.
     *
     * @return The list of variables in this group
     */
    List<Variable> getVariables() {
        return variables;
    }

    /**
     * Writes the group of declarations to the supplied writer at the required
     * indent level. The declarations will be written in the order that they were
     * added to the group. The visibility will be written each time it is different
     * to the previous declaration's visibility.
     * <p>
     * <p>
     * the writer to write to
     * <p>
     * indetentation for each declaration
     * <p>
     * the namspace that is currently open to write the declaration group into
     *
     * @throws IOException
     */
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        if (declarations.size() == 0) {
            return;
        }

        if (startComment != null) {
            startComment.write(writer, indent, currentNamespace);
            writer.write("\n");
        }

        Visibility oldVisibility = null;

        for (final Declaration declaration : declarations) {
            // Check to see whether the visibility has changed, and start a new
            // section if necessary.
            if (declaration.getVisibility() != oldVisibility) {
                oldVisibility = declaration.getVisibility();
                writer.write(indent + declaration.getVisibility() + ":\n");
            }

            declaration.writeDeclaration(writer, indent + TextUtils.getIndent(), currentNamespace);
        }
        writer.write("\n\n");
    }

    /**
     * A list of the delcarations.
     */
    private final List<Declaration> declarations = new ArrayList<>();
    /**
     * A comment to display at the top of the group
     */
    private Comment startComment = null;

    /**
     * List of variables declared in this group
     */
    private final List<Variable> variables = new ArrayList<>();

}
