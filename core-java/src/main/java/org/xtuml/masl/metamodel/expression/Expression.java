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
package org.xtuml.masl.metamodel.expression;

import org.xtuml.masl.metamodel.ASTNode;
import org.xtuml.masl.metamodel.object.AttributeDeclaration;
import org.xtuml.masl.metamodel.type.BasicType;

import java.util.List;

public interface Expression extends ASTNode {

    BasicType getType();

    List<? extends FindParameterExpression> getFindParameters();

    /**
     * Returns a list of all the attributes that are checked for equality with a
     * find parameter in the given expression. They must all be linked with 'and'.
     * If there are attributes which are checked for anything other than equality or
     * if they are not linked with 'and', then null is returned. This function is
     * called recursively on subexpressions until the bottom of the tree is reached,
     * or a null is returned by a subexpression.
     * <p>
     * This function is used to check whether a find expression is finding an exact
     * match on a unique identifier. If it is, then a number of optimisations may be
     * possible.
     *
     * @return a set of attribute names, or null if this is not a compatible
     * expression
     */
    List<? extends AttributeDeclaration> getFindEqualAttributes();

    /**
     * @return For a complex expresion return the literal result.
     */
    LiteralExpression evaluate();

}
