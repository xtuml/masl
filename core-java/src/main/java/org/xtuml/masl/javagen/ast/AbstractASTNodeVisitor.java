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
package org.xtuml.masl.javagen.ast;

import java.util.Collection;

public abstract class AbstractASTNodeVisitor<R, P> implements ASTNodeVisitor<R, P> {

    public final R visit(final ASTNode node) throws Exception {
        return visit(node, null);
    }

    @Override
    public final R visit(final ASTNode node, final P p) throws Exception {
        if (node == null) {
            return visitNull(p);
        } else {
            return node.accept(this, p);
        }
    }

    public final R visit(final Collection<? extends ASTNode> nodes) throws Exception {
        return visit(nodes, null);
    }

    public final R visit(final Collection<? extends ASTNode> nodes, final P p) throws Exception {
        R r = null;
        for (final ASTNode node : nodes) {
            r = visit(node);
        }
        return r;
    }

    @Override
    public R visitNull(final P p) throws Exception {
        throw new NullPointerException();
    }
}
