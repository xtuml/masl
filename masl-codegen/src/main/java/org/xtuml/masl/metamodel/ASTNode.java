/*
 ----------------------------------------------------------------------------
 (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 The copyright of this Software is vested in the Crown
 and the Software is the property of the Crown.
 ----------------------------------------------------------------------------
 SPDX-License-Identifier: Apache-2.0
 ----------------------------------------------------------------------------
 */
package org.xtuml.masl.metamodel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ASTNode {

    void accept(final ASTNodeVisitor v);

    List<ASTNode> children();

    static List<ASTNode> makeChildren(final Object... children) {

        final List<ASTNode>
                result =
                Stream.of(children).flatMap(o -> o instanceof Collection ?
                                                 ((Collection<?>) o).stream() :
                                                 Stream.of(o)).filter(o -> o instanceof ASTNode).map(o -> (ASTNode) o).collect(
                        Collectors.toList());

        return result;

    }

}
