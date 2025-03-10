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

import java.util.ArrayList;
import java.util.List;

public class PragmaDefinition implements org.xtuml.masl.metamodel.common.PragmaDefinition {

    String name;
    List<String> values = null;

    public PragmaDefinition(final String name, final List<String> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public List<String> getValues() {
        return new ArrayList<>(values);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "pragma " + name + " (" + org.xtuml.masl.utils.TextUtils.formatList(values, "", ",", "") + ");";
    }

    @Override
    public void accept(final ASTNodeVisitor v) {
        v.visitPragmaDefinition(this);
    }

    @Override
    public List<ASTNode> children() {
        return ASTNode.makeChildren();
    }

}
