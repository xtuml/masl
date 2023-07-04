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
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * A group of C++ statements. This is a grouping purely for convenience, and the
 * generated code will be no different to a similar sequence of individual
 * statements. The advantage of using a group is that there is a single
 * reference to the whole group, and extra statements can be added easily to the
 * beginning or end of the group.
 */
public class StatementGroup extends Statement {

    /**
     * Creates a group of statements.
     */
    public StatementGroup() {
        this(null);
    }

    /**
     * Creates a group of statements, putting the supplied comment at the top.
     * <p>
     * <p>
     * a comment to appear at the top of the group
     */
    public StatementGroup(final Comment comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    /**
     * Adds the supplied statement to the end of the group.
     * <p>
     * <p>
     * the statement to add
     */
    public void appendStatement(final Statement statement) {
        statement.setParent(this);
        statements.addLast(statement);
    }

    /**
     * Adds the supplied statement to the beginning of the group.
     * <p>
     * <p>
     * the statement to add
     */
    public void prependStatement(final Statement statement) {
        statement.setParent(this);
        statements.addFirst(statement);
    }

    /**
     * Gets the number of statements in the group, not including the header comment.
     *
     * @return the number of statements in the group
     */
    public int size() {
        return statements.size();
    }

    @Override
    Set<Declaration> getForwardDeclarations() {
        final Set<Declaration> result = new LinkedHashSet<>();
        for (final Statement statement : statements) {
            result.addAll(statement.getForwardDeclarations());
        }
        return result;
    }

    @Override
    Set<CodeFile> getIncludes() {
        final Set<CodeFile> result = new LinkedHashSet<>();
        for (final Statement statement : statements) {
            result.addAll(statement.getIncludes());
        }
        return result;
    }

    @Override
    void write(final Writer writer, final String indent, final Namespace currentNamespace) throws IOException {
        if (comment != null) {
            writer.write("\n");
            comment.write(writer, indent, currentNamespace);
            writer.write("\n");
        }
        for (final Statement statement : statements) {
            statement.write(writer, indent, currentNamespace);
            if (!(statement instanceof StatementGroup)) {
                writer.write("\n");
            }
        }

    }

    private final Comment comment;
    private final LinkedList<Statement> statements = new LinkedList<>();

}
