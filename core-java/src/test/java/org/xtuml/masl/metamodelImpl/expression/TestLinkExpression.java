/*
 ----------------------------------------------------------------------------
 (c) 2008-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
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
package org.xtuml.masl.metamodelImpl.expression;

import junit.framework.TestCase;
import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.unittest.ErrorLog;

import static org.xtuml.masl.metamodelImpl.SampleDomain.*;

public class TestLinkExpression extends TestCase {

    public void checkLink(final Expression lhs,
                          final RelationshipDeclaration.Reference relRef,
                          final String objOrRole,
                          final ObjectNameExpression obj,
                          final Expression rhs,
                          final Expression assignTo) {
        ErrorLog.getInstance().reset();
        final LinkUnlinkExpression
                result =
                LinkUnlinkExpression.create(null,
                                            LinkUnlinkExpression.LINK,
                                            lhs,
                                            RelationshipSpecification.createReference(lhs,
                                                                                      relRef,
                                                                                      objOrRole,
                                                                                      obj,
                                                                                      false,
                                                                                      false),
                                            rhs);
        ErrorLog.getInstance().checkErrors();
        assertTrue(assignTo.getType().isAssignableFrom(result, false));
    }

    public void checkLink(final Expression lhs,
                          final RelationshipDeclaration.Reference relRef,
                          final String objOrRole,
                          final ObjectNameExpression obj,
                          final Expression rhs,
                          final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        LinkUnlinkExpression.create(null,
                                    LinkUnlinkExpression.LINK,
                                    lhs,
                                    RelationshipSpecification.createReference(lhs,
                                                                              relRef,
                                                                              objOrRole,
                                                                              obj,
                                                                              false,
                                                                              false),
                                    rhs);
        ErrorLog.getInstance().checkErrors(errors);
    }

    // R1

    public void test_link_a_R1_b() {
        checkLink(singleA, r1, "to left of", refB, singleB, SemanticErrorCode.AssociativeRelationshipRequired);
    }

    public void test_link_b_R1_a() {
        checkLink(singleB, r1, "to right of", refA, singleA, SemanticErrorCode.AssociativeRelationshipRequired);
    }

    // R2

    public void test_link_a_R2_b() {
        checkLink(singleA, r2, "to left of", refB, singleB, SemanticErrorCode.AssociativeRelationshipRequired);
    }

    public void test_link_b_R2_a() {
        checkLink(singleB, r2, "to right of", refA, singleA, SemanticErrorCode.AssociativeRelationshipRequired);
    }

    // R3 ->

    public void test_link_a_R3_b() {
        checkLink(singleA, r3, "to left of", refB, singleB, singleC);
    }

    public void test_link_a_R3_bb() {
        checkLink(singleA, r3, "to left of", refB, collB, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_b() {
        checkLink(collA, r3, "to left of", refB, singleB, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_bb() {
        checkLink(collA, r3, "to left of", refB, collB, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3() {
        checkLink(singleA, r3, "to left of", refB, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R3() {
        checkLink(collA, r3, "to left of", refB, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R3 <-

    public void test_link_b_R3_a() {
        checkLink(singleB, r3, "to right of", refA, singleA, singleC);
    }

    public void test_link_bb_R3_a() {
        checkLink(collB, r3, "to right of", refA, singleA, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_aa() {
        checkLink(singleB, r3, "to right of", refA, collA, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_aa() {
        checkLink(collB, r3, "to right of", refA, collA, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3() {
        checkLink(singleB, r3, "to right of", refA, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R3() {
        checkLink(collB, r3, "to right of", refA, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R4 ->

    public void test_link_a_R4_b() {
        checkLink(singleA, r4, "to left of", refB, singleB, singleD);
    }

    public void test_link_a_R4_bb() {
        checkLink(singleA, r4, "to left of", refB, collB, collD);
    }

    public void test_link_aa_R4_b() {
        checkLink(collA, r4, "to left of", refB, singleB, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_bb() {
        checkLink(collA, r4, "to left of", refB, collB, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4() {
        checkLink(singleA, r4, "to left of", refB, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R4() {
        checkLink(collA, r4, "to left of", refB, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R4 <-

    public void test_link_b_R4_a() {
        checkLink(singleB, r4, "to right of", refA, singleA, singleD);
    }

    public void test_link_bb_R4_a() {
        checkLink(collB, r4, "to right of", refA, singleA, collD);
    }

    public void test_link_b_R4_aa() {
        checkLink(singleB, r4, "to right of", refA, collA, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_aa() {
        checkLink(collB, r4, "to right of", refA, collA, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4() {
        checkLink(singleB, r4, "to right of", refA, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R4() {
        checkLink(collB, r4, "to right of", refA, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R5 ->

    public void test_link_a_R5_b() {
        checkLink(singleA, r5, "to left of", refB, singleB, singleE);
    }

    public void test_link_a_R5_bb() {
        checkLink(singleA, r5, "to left of", refB, collB, collE);
    }

    public void test_link_aa_R5_b() {
        checkLink(collA, r5, "to left of", refB, singleB, collE);
    }

    public void test_link_aa_R5_bb() {
        checkLink(collA, r5, "to left of", refB, collB, collE);
    }

    public void test_link_a_R5() {
        checkLink(singleA, r5, "to left of", refB, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R5() {
        checkLink(collA, r5, "to left of", refB, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R5 <-

    public void test_link_b_R5_a() {
        checkLink(singleB, r5, "to right of", refA, singleA, singleE);
    }

    public void test_link_bb_R5_a() {
        checkLink(collB, r5, "to right of", refA, singleA, collE);
    }

    public void test_link_b_R5_aa() {
        checkLink(singleB, r5, "to right of", refA, collA, collE);
    }

    public void test_link_bb_R5_aa() {
        checkLink(collB, r5, "to right of", refA, collA, collE);
    }

    public void test_link_b_R5() {
        checkLink(singleB, r5, "to right of", refA, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R5() {
        checkLink(collB, r5, "to right of", refA, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R6

    public void test_link_a_R6_b() {
        checkLink(singleA, r6, "to left of", refB, singleB, SemanticErrorCode.CannotDeduceAssocIdentifier);
    }

    public void test_link_b_R6_a() {
        checkLink(singleB, r6, "to right of", refA, singleA, SemanticErrorCode.CannotDeduceAssocIdentifier);
    }

    // R7

    public void test_link_a_R7_b() {
        checkLink(singleA, r7, "to left of", refB, singleB, SemanticErrorCode.CannotDeduceAssocIdentifier);
    }

    public void test_link_b_R7_a() {
        checkLink(singleB, r7, "to right of", refA, singleA, SemanticErrorCode.CannotDeduceAssocIdentifier);
    }

    // R7

    public void test_link_a_R8_b() {
        checkLink(singleA, r8, "to left of", refB, singleB, SemanticErrorCode.CannotDeduceAssocIdentifier);
    }

    public void test_link_b_R8_a() {
        checkLink(singleB, r8, "to right of", refA, singleA, SemanticErrorCode.CannotDeduceAssocIdentifier);
    }

}
