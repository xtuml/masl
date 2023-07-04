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
