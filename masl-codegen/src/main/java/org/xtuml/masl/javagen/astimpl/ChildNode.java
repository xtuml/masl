/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.javagen.astimpl;

public class ChildNode<C extends ASTNodeImpl> {

    ChildNode(final ASTNodeImpl parent) {
        this.parent = parent;
    }

    void set(final C childNode) {
        if (this.childNode != null) {
            parent.removeChildNode(this.childNode);
        }
        if (childNode != null) {
            parent.addChildNode(childNode);
        }
        this.childNode = childNode;
    }

    void clear() {
        set(null);
    }

    C get() {
        return childNode;
    }

    private final ASTNodeImpl parent;
    private C childNode = null;
}
