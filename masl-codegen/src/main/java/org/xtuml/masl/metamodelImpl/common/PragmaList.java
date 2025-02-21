/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodelImpl.common;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.ASTNodeVisitor;

import java.util.*;

public final class PragmaList implements org.xtuml.masl.metamodel.common.PragmaList {

    private final List<PragmaDefinition> pragmas = new ArrayList<>();
    private final Map<String, List<String>> pragmaLookup = new LinkedHashMap<>();

    static public final String EXTERNAL = "external";
    static public final String SCENARIO = "scenario";
    static public final String FILENAME = "filename";
    static public final String TEST_ONLY = "test_only";

    public PragmaList(final List<PragmaDefinition> pragmas) {
        addPragmas(pragmas);
    }

    public PragmaList() {
        this(Collections.emptyList());
    }

    @Override
    public List<String> getPragmaValues(final String name) {
        return pragmaLookup.get(name);
    }

    @Override
    public List<String> getPragmaValues(final String name, final boolean allowValueList) {
        List<String> pragmaValues = getPragmaValues(name);
        if (pragmaValues != null && allowValueList) {
            // Each pragma value might actual contain a list of values
            // i.e. soa_pass_parameter("context,attribute,properties")
            // This needs to be flattened out so the returned list contains
            // the individual values.
            final List<String> flattenedValues = new ArrayList<>();
            for (final String pragmaValue : pragmaValues) {
                // split on non-word character
                final String[] individualValues = pragmaValue.split("\\W");
                Collections.addAll(flattenedValues, individualValues);
            }
            pragmaValues = flattenedValues;
        }
        return pragmaValues;
    }

    @Override
    public String getValue(final String name) {
        final List<String> defs = getPragmaValues(name);
        if (defs != null && defs.size() > 0) {
            return defs.get(0);
        } else {
            return "";
        }
    }

    @Override
    public boolean hasPragma(final String name) {
        return getPragmaValues(name) != null;
    }

    @Override
    public boolean hasValue(final String name) {
        return getPragmaValues(name) != null && getPragmaValues(name).size() > 0;
    }

    public void addPragmas(final List<PragmaDefinition> pragmas) {
        this.pragmas.addAll(pragmas);

        for (final PragmaDefinition pragma : this.pragmas) {
            if (pragmaLookup.containsKey(pragma.getName())) {
                pragmaLookup.get(pragma.getName()).addAll(pragma.getValues());
            } else {
                pragmaLookup.put(pragma.getName(), pragma.getValues());
            }
        }
    }

    @Override
    public List<PragmaDefinition> getPragmas() {
        return Collections.unmodifiableList(pragmas);
    }

    @Override
    public String toString() {
        return (org.xtuml.masl.utils.TextUtils.formatList(pragmas, "", "\n", "\n"));
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitPragmaList(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren(pragmas);
    }

}
