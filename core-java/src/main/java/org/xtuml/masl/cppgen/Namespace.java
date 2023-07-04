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
import java.io.Writer;
import java.util.*;

/**
 * A C++ namespace. As well as representing a namspace created with a
 * <code>namespace</code> declaration, this will also be used to represent the
 * implicit namespace created by a class declaration.
 */
public class Namespace {

    /**
     * Writes code to open a namespace, ready for declarations to be written into.
     * If a namespace declaration is already open it is closed down level by level
     * until a namespace that is an ancestor of the required namspace is found.
     * Once all namespaces that are not ancestors of the required namespace have
     * been closed, namespace declarations are opened to the level of the required
     * namespace.
     * <p>
     * For example, given a current namespace of <code>A::B1::C1</code> and a
     * required namespace of <code>A::B2::C2</code>, namespaces <code>C1</code>
     * and <code>B1</code> would be closed and code>B2</code> and <code>C2
     * </code> would be
     * opened.
     * <p>
     * <p>
     * the writer to write code to
     * <p>
     * the current indent level
     * <p>
     * the namespace currently open. <code>null</code> if no namespace is
     * open.
     * <p>
     * the namepace required to be open. <code>null</code> if all
     * namespaces are to be closed.
     *
     * @return the new indent level
     * @throws IOException
     */
    static int openDeclaration(final Writer writer,
                               int indentLevel,
                               final Namespace currentNamespace,
                               final Namespace requiredNamespace) throws IOException {
        Iterator<String> reqIt = null;
        String reqName = null;
        String curName = null;
        Iterator<String> curIt = null;

        if (requiredNamespace != null) {
            reqIt = requiredNamespace.getNamespaceList().iterator();
            reqName = reqIt.next();
        }

        if (currentNamespace != null) {
            curIt = currentNamespace.getNamespaceList().iterator();
            curName = curIt.next();
        }

        // Find common ground between two namespaces
        while (curName == reqName && curName != null) {
            reqName = reqIt.hasNext() ? reqIt.next() : null;
            curName = curIt.hasNext() ? curIt.next() : null;
        }

        // Close off any unused namespaces in current.
        while (curName != null) {
            writer.write(TextUtils.getIndent(--indentLevel) + "}\n");
            curName = curIt.hasNext() ? curIt.next() : null;
        }

        // Open any required namespaces
        while (reqName != null) {
            final String indent = TextUtils.getIndent(indentLevel++);
            writer.write(indent + "namespace " + reqName + "\n" + indent + "{\n");
            reqName = reqIt.hasNext() ? reqIt.next() : null;
        }
        return indentLevel;
    }

    private final String name;
    private Namespace parentNamespace;

    /**
     * Creates a namespace with the given name
     * <p>
     * <p>
     * the name for the namespace
     */
    public Namespace(final String name) {
        this(name, null);
    }

    /**
     * Creates a namespace with the given name contained within another namespace
     * <p>
     * <p>
     * the name for the namespace
     * <p>
     * the parent namespace
     */
    public Namespace(final String name, final Namespace parentNamespace) {
        this.name = name;
        this.parentNamespace = parentNamespace;
    }

    @Override
    public boolean equals(final Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (rhs instanceof Namespace rhsNs) {
            return name.equals(rhsNs.name) &&
                   templateSpecialisations.equals(rhsNs.templateSpecialisations) &&
                   (Objects.equals(parentNamespace, rhsNs.parentNamespace));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^
               (parentNamespace == null ? 0 : parentNamespace.hashCode()) ^
               templateSpecialisations.hashCode();
    }

    /**
     * Gets the hierachy of namespaces containing this namespace, starting with
     * the outermost namespace, ending with this namspace.
     *
     * @return a hierachy of namespaces containing this one.
     */
    private List<String> getNamespaceList() {
        final LinkedList<String> result = new LinkedList<String>();
        Namespace current = this;
        while (current != null) {
            result.addFirst(current.getName());
            current = current.parentNamespace;
        }
        return result;
    }

    public void addTemplateSpecialisation(final Expression param) {
        templateSpecialisations.add(TemplateSpecialisation.create(param));
    }

    public void addTemplateSpecialisation(final TypeUsage param) {
        templateSpecialisations.add(TemplateSpecialisation.create(param));
    }

    protected String getName() {
        return name;
    }

    protected Namespace getParentNamespace() {
        return parentNamespace;
    }

    /**
     * Gets the fully qualified name of this namespace. The fully qualified name
     * is the name required to uniquely identify this namespace when referencing
     * it from outside a namespace.
     *
     * @return the fully qualified namespace name
     */
    String getQualifiedName() {
        return getQualifiedName(null);
    }

    /**
     * Gets the qualified name of this namespace. The qualified name is the
     * shortest name needed to reference a namespace from within a given
     * namespace. For example the qualified name of <code>A::B::C::D</code> from
     * inside <code>A::B</code> would be <code>C::D</code>, but from inside
     * <code>E::F<code> would be <code>::A::B::C::D</code>
     * <p>
     * <p>
     * the namespace to reference this namespace from
     *
     * @return the qualified name of this namsespace
     */
    String getQualifiedName(final Namespace currentNamespace) {
        String qualName = null;
        if (parentNamespace == null) {
            if (currentNamespace != null) {
                if (name.length() > 0) {
                    qualName = "::" + name;
                } else {
                    qualName = "";
                }
            } else {
                qualName = name;
            }
        } else if (parentNamespace.contains(currentNamespace)) {
            qualName = name;
        } else {
            final String parent = parentNamespace.getQualifiedName(currentNamespace);
            if (name.length() > 0) {
                qualName = parent + "::" + name;
            } else {
                qualName = parent;
            }
        }
        final List<String> params = new ArrayList<String>();
        for (final TemplateSpecialisation param : templateSpecialisations) {
            params.add(param.getValue(currentNamespace));
        }
        // Avoid using '<:', as this parses as a digraph
        final String startTemplate = params.size() > 0 && ((params.get(0)).startsWith(":")) ? "< " : "<";

        // Avoid using '>>' as this parses as the right shift operator
        final String endTemplate = params.size() > 0 && ((params.get(params.size() - 1)).endsWith(">")) ? " >" : ">";

        return qualName + TextUtils.formatList(params, startTemplate, ",", endTemplate);

    }

    /**
     * Determines whether the supplied namespace has this namespace as an
     * ancestor.
     *
     * @return <code>true</code> if this namespace contains the supplied namespace
     */
    boolean contains(final Namespace currentNamespace) {
        Namespace ns = currentNamespace;
        while (ns != null) {
            if (this.equals(ns)) {
                return true;
            } else {
                ns = ns.getParentNamespace();
            }
        }
        return false;
    }

    void setParentNamespace(final Namespace namespace) {
        parentNamespace = namespace;
    }

    @Override
    public String toString() {
        return getQualifiedName();
    }

    private final List<TemplateSpecialisation> templateSpecialisations = new ArrayList<TemplateSpecialisation>();

}
