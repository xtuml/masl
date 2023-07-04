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
package org.xtuml.masl.translate.main.expression;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.expression.LinkUnlinkExpression;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.object.ObjectTranslator;
import org.xtuml.masl.translate.main.object.RelationshipTranslator;

public class LinkUnlinkExpressionTranslator extends ExpressionTranslator {

    LinkUnlinkExpressionTranslator(final LinkUnlinkExpression linkUnlink, final Scope scope) {

        final Expression lhs = ExpressionTranslator.createTranslator(linkUnlink.getLhs(), scope).getReadExpression();

        final Expression
                rhs =
                linkUnlink.getRhs() == null ?
                null :
                ExpressionTranslator.createTranslator(linkUnlink.getRhs(), scope).getReadExpression();

        RelationshipTranslator.PublicAccessors relTrans;

        Function linkFn;
        final boolean unlink = linkUnlink.getLinkType() == LinkUnlinkExpression.Type.UNLINK;

        Expression from;
        Expression to = null;
        boolean fromMulti = false;

        // Switch lhs & rhs if linking many to one so that optimised versions can be
        // used
        if (linkUnlink.getLhs().getType().isCollection() &&
            linkUnlink.getRhs() != null &&
            !linkUnlink.getRhs().getType().isCollection()) {

            from = rhs;
            to = lhs;
            final ObjectTranslator fromObj = ObjectTranslator.getInstance(linkUnlink.getRhsObject());
            relTrans =
                    fromObj.getRelationshipTranslator(linkUnlink.getRelationship().getReverseSpec()).getPublicAccessors();

            linkFn =
                    unlink ?
                    relTrans.getDeducedAssocMultipleUnlinkFunction() :
                    relTrans.getDeducedAssocMultipleLinkFunction();
        } else {
            from = lhs;

            final ObjectTranslator fromObj = ObjectTranslator.getInstance(linkUnlink.getLhsObject());
            relTrans = fromObj.getRelationshipTranslator(linkUnlink.getRelationship()).getPublicAccessors();

            if (rhs == null) {
                assert unlink;
                linkFn = relTrans.getDeducedAssocAllUnlinkFunction();
            } else {
                to = rhs;
                if (linkUnlink.getRhs().getType().isCollection()) {
                    linkFn =
                            unlink ?
                            relTrans.getDeducedAssocMultipleUnlinkFunction() :
                            relTrans.getDeducedAssocMultipleLinkFunction();
                } else {
                    linkFn =
                            unlink ?
                            relTrans.getDeducedAssocSingleUnlinkFunction() :
                            relTrans.getDeducedAssocSingleLinkFunction();
                }
            }

            fromMulti = linkUnlink.getLhs().getType().isCollection();
        }

        if (fromMulti) {
            final Function multiLinkFn = unlink ? Architecture.unlink : Architecture.link;
            if (to == null) {
                setReadExpression(multiLinkFn.asFunctionCall(from, linkFn.asFunctionPointer()));
            } else {
                setReadExpression(multiLinkFn.asFunctionCall(from, to, linkFn.asFunctionPointer()));
            }
        } else {
            if (to == null) {
                setReadExpression(linkFn.asFunctionCall(from, true));
            } else {
                setReadExpression(linkFn.asFunctionCall(from, true, to));
            }
        }
        setWriteableExpression(getReadExpression());
    }

}
