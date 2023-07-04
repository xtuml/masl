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
package org.xtuml.masl.translate.main.code;

import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.metamodel.code.LinkUnlinkStatement;
import org.xtuml.masl.translate.main.Architecture;
import org.xtuml.masl.translate.main.Scope;
import org.xtuml.masl.translate.main.expression.ExpressionTranslator;
import org.xtuml.masl.translate.main.object.ObjectTranslator;
import org.xtuml.masl.translate.main.object.RelationshipTranslator;

public class LinkUnlinkTranslator extends CodeTranslator {

    protected LinkUnlinkTranslator(final LinkUnlinkStatement linkUnlink,
                                   final Scope parentScope,
                                   final CodeTranslator parentTranslator) {
        super(linkUnlink, parentScope, parentTranslator);
        final Expression
                lhs =
                ExpressionTranslator.createTranslator(linkUnlink.getLhs(), getScope()).getReadExpression();

        final Expression
                rhs =
                linkUnlink.getRhs() == null ?
                null :
                ExpressionTranslator.createTranslator(linkUnlink.getRhs(), getScope()).getReadExpression();

        final Expression
                assoc =
                linkUnlink.getAssoc() == null ?
                null :
                ExpressionTranslator.createTranslator(linkUnlink.getAssoc(), getScope()).getReadExpression();

        RelationshipTranslator.PublicAccessors relTrans;

        Function linkFn;
        final boolean unlink = linkUnlink.getLinkType() == LinkUnlinkStatement.Type.UNLINK;

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

            linkFn = unlink ? relTrans.getMultipleUnlinkFunction() : relTrans.getMultipleLinkFunction();
        } else {
            from = lhs;

            final ObjectTranslator fromObj = ObjectTranslator.getInstance(linkUnlink.getLhsObject());
            relTrans = fromObj.getRelationshipTranslator(linkUnlink.getRelationship()).getPublicAccessors();

            if (rhs == null) {
                assert unlink;
                linkFn = relTrans.getAllUnlinkFunction();
            } else {
                to = rhs;
                if (linkUnlink.getRhs().getType().isCollection()) {
                    linkFn = unlink ? relTrans.getMultipleUnlinkFunction() : relTrans.getMultipleLinkFunction();
                } else {
                    linkFn = unlink ? relTrans.getSingleUnlinkFunction() : relTrans.getSingleLinkFunction();
                }
            }

            fromMulti = linkUnlink.getLhs().getType().isCollection();

        }

        if (fromMulti) {
            final Function multiLinkFn = unlink ? Architecture.unlink : Architecture.link;
            if (to == null) {
                getCode().appendStatement(multiLinkFn.asFunctionCall(from, linkFn.asFunctionPointer()).asStatement());
            } else {
                getCode().appendStatement(multiLinkFn.asFunctionCall(from,
                                                                     to,
                                                                     linkFn.asFunctionPointer()).asStatement());
            }
        } else {
            if (to == null) {
                getCode().appendStatement(linkFn.asFunctionCall(from, true).asStatement());
            } else if (assoc == null) {
                getCode().appendStatement(linkFn.asFunctionCall(from, true, to).asStatement());
            } else {
                getCode().appendStatement(linkFn.asFunctionCall(from, true, to, assoc).asStatement());
            }
        }

    }

}
