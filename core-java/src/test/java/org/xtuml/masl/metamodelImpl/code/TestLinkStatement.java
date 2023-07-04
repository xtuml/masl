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
package org.xtuml.masl.metamodelImpl.code;
import static org.xtuml.masl.metamodelImpl.SampleDomain.collA;
import static org.xtuml.masl.metamodelImpl.SampleDomain.collB;
import static org.xtuml.masl.metamodelImpl.SampleDomain.collC;
import static org.xtuml.masl.metamodelImpl.SampleDomain.collD;
import static org.xtuml.masl.metamodelImpl.SampleDomain.collE;
import static org.xtuml.masl.metamodelImpl.SampleDomain.r1;
import static org.xtuml.masl.metamodelImpl.SampleDomain.r2;
import static org.xtuml.masl.metamodelImpl.SampleDomain.r3;
import static org.xtuml.masl.metamodelImpl.SampleDomain.r4;
import static org.xtuml.masl.metamodelImpl.SampleDomain.r5;
import static org.xtuml.masl.metamodelImpl.SampleDomain.refA;
import static org.xtuml.masl.metamodelImpl.SampleDomain.refB;
import static org.xtuml.masl.metamodelImpl.SampleDomain.singleA;
import static org.xtuml.masl.metamodelImpl.SampleDomain.singleB;
import static org.xtuml.masl.metamodelImpl.SampleDomain.singleC;
import static org.xtuml.masl.metamodelImpl.SampleDomain.singleD;
import static org.xtuml.masl.metamodelImpl.SampleDomain.singleE;

import org.xtuml.masl.error.ErrorCode;
import org.xtuml.masl.metamodelImpl.error.SemanticErrorCode;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipDeclaration;
import org.xtuml.masl.metamodelImpl.relationship.RelationshipSpecification;
import org.xtuml.masl.unittest.ErrorLog;

import junit.framework.TestCase;

public class TestLinkStatement extends TestCase {

    public void checkLink(final Expression lhs, final RelationshipDeclaration.Reference relRef, final String objOrRole,
            final ObjectNameExpression obj, final Expression rhs, final Expression assoc) {
        ErrorLog.getInstance().reset();
        LinkUnlinkStatement.create(null, LinkUnlinkStatement.LINK, lhs,
                RelationshipSpecification.createReference(lhs, relRef, objOrRole, obj, false, false), rhs, assoc);
        ErrorLog.getInstance().checkErrors();
    }

    public void checkLink(final Expression lhs, final RelationshipDeclaration.Reference relRef, final String objOrRole,
            final ObjectNameExpression obj, final Expression rhs, final Expression assoc, final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        LinkUnlinkStatement.create(null, LinkUnlinkStatement.LINK, lhs,
                RelationshipSpecification.createReference(lhs, relRef, objOrRole, obj, false, false), rhs, assoc);
        ErrorLog.getInstance().checkErrors(errors);
    }

    // R1 ->

    public void test_link_a_R1_b() {
        checkLink(singleA, r1, "to left of", refB, singleB, null);
    }

    public void test_link_a_R1_bb() {
        checkLink(singleA, r1, "to left of", refB, collB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R1_b() {
        checkLink(collA, r1, "to left of", refB, singleB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R1_bb() {
        checkLink(collA, r1, "to left of", refB, collB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R1() {
        checkLink(singleA, r1, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R1() {
        checkLink(collA, r1, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_a_R1_b_using_a() {
        checkLink(singleA, r1, "to left of", refB, singleB, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R1 <-

    public void test_link_b_R1_a() {
        checkLink(singleB, r1, "to right of", refA, singleA, null);
    }

    public void test_link_bb_R1_a() {
        checkLink(collB, r1, "to right of", refA, singleA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R1_aa() {
        checkLink(singleB, r1, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R1_aa() {
        checkLink(collB, r1, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R1() {
        checkLink(singleB, r1, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R1() {
        checkLink(collB, r1, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_b_R1_a_using_a() {
        checkLink(singleB, r1, "to right of", refA, singleA, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R2 ->

    public void test_link_a_R2_b() {
        checkLink(singleA, r2, "to left of", refB, singleB, null);
    }

    public void test_link_a_R2_bb() {
        checkLink(singleA, r2, "to left of", refB, collB, null);
    }

    public void test_link_aa_R2_b() {
        checkLink(collA, r2, "to left of", refB, singleB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R2_bb() {
        checkLink(collA, r2, "to left of", refB, collB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R2() {
        checkLink(singleA, r2, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R2() {
        checkLink(collA, r2, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_a_R2_b_using_a() {
        checkLink(singleA, r2, "to left of", refB, singleB, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R2 <-

    public void test_link_b_R2_a() {
        checkLink(singleB, r2, "to right of", refA, singleA, null);
    }

    public void test_link_bb_R2_a() {
        checkLink(collB, r2, "to right of", refA, singleA, null);
    }

    public void test_link_b_R2_aa() {
        checkLink(singleB, r2, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R2_aa() {
        checkLink(collB, r2, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R2() {
        checkLink(singleB, r2, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R2() {
        checkLink(collB, r2, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_b_R2_a_using_a() {
        checkLink(singleB, r2, "to right of", refA, singleA, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R3 ->

    public void test_link_a_R3_b() {
        checkLink(singleA, r3, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R3_bb() {
        checkLink(singleA, r3, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R3_b() {
        checkLink(collA, r3, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R3_bb() {
        checkLink(collA, r3, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R3_b_using_c() {
        checkLink(singleA, r3, "to left of", refB, singleB, singleC);
    }

    public void test_link_a_R3_bb_using_c() {
        checkLink(singleA, r3, "to left of", refB, collB, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_b_using_c() {
        checkLink(collA, r3, "to left of", refB, singleB, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_bb_using_c() {
        checkLink(collA, r3, "to left of", refB, collB, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3_b_using_cc() {
        checkLink(singleA, r3, "to left of", refB, singleB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3_bb_using_cc() {
        checkLink(singleA, r3, "to left of", refB, collB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_b_using_cc() {
        checkLink(collA, r3, "to left of", refB, singleB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_bb_using_cc() {
        checkLink(collA, r3, "to left of", refB, collB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3() {
        checkLink(singleA, r3, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R3() {
        checkLink(collA, r3, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R3 <-

    public void test_link_b_R3_a() {
        checkLink(singleB, r3, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R3_a() {
        checkLink(collB, r3, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R3_aa() {
        checkLink(singleB, r3, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R3_aa() {
        checkLink(collB, r3, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R3_a_using_c() {
        checkLink(singleB, r3, "to right of", refA, singleA, singleC);
    }

    public void test_link_bb_R3_a_using_c() {
        checkLink(collB, r3, "to right of", refA, singleA, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_aa_using_c() {
        checkLink(singleB, r3, "to right of", refA, collA, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_aa_using_c() {
        checkLink(collB, r3, "to right of", refA, collA, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_a_using_cc() {
        checkLink(singleB, r3, "to right of", refA, singleA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_a_using_cc() {
        checkLink(collB, r3, "to right of", refA, singleA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_aa_using_cc() {
        checkLink(singleB, r3, "to right of", refA, collA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_aa_using_cc() {
        checkLink(collB, r3, "to right of", refA, collA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3() {
        checkLink(singleB, r3, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R3() {
        checkLink(collB, r3, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R4 ->

    public void test_link_a_R4_b() {
        checkLink(singleA, r4, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R4_bb() {
        checkLink(singleA, r4, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R4_b() {
        checkLink(collA, r4, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R4_bb() {
        checkLink(collA, r4, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R4_b_using_d() {
        checkLink(singleA, r4, "to left of", refB, singleB, singleD);
    }

    public void test_link_a_R4_bb_using_d() {
        checkLink(singleA, r4, "to left of", refB, collB, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_b_using_d() {
        checkLink(collA, r4, "to left of", refB, singleB, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_bb_using_d() {
        checkLink(collA, r4, "to left of", refB, collB, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4_b_using_dd() {
        checkLink(singleA, r4, "to left of", refB, singleB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4_bb_using_dd() {
        checkLink(singleA, r4, "to left of", refB, collB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_b_using_dd() {
        checkLink(collA, r4, "to left of", refB, singleB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_bb_using_dd() {
        checkLink(collA, r4, "to left of", refB, collB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4() {
        checkLink(singleA, r4, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R4() {
        checkLink(collA, r4, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R4 <-

    public void test_link_b_R4_a() {
        checkLink(singleB, r4, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R4_a() {
        checkLink(collB, r4, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R4_aa() {
        checkLink(singleB, r4, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R4_aa() {
        checkLink(collB, r4, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R4_a_using_d() {
        checkLink(singleB, r4, "to right of", refA, singleA, singleD);
    }

    public void test_link_bb_R4_a_using_d() {
        checkLink(collB, r4, "to right of", refA, singleA, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4_aa_using_d() {
        checkLink(singleB, r4, "to right of", refA, collA, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_aa_using_d() {
        checkLink(collB, r4, "to right of", refA, collA, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4_a_using_dd() {
        checkLink(singleB, r4, "to right of", refA, singleA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_a_using_dd() {
        checkLink(collB, r4, "to right of", refA, singleA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4_aa_using_dd() {
        checkLink(singleB, r4, "to right of", refA, collA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_aa_using_dd() {
        checkLink(collB, r4, "to right of", refA, collA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4() {
        checkLink(singleB, r4, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R4() {
        checkLink(collB, r4, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R5 ->

    public void test_link_a_R5_b() {
        checkLink(singleA, r5, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R5_bb() {
        checkLink(singleA, r5, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R5_b() {
        checkLink(collA, r5, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R5_bb() {
        checkLink(collA, r5, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R5_b_using_e() {
        checkLink(singleA, r5, "to left of", refB, singleB, singleE);
    }

    public void test_link_a_R5_bb_using_e() {
        checkLink(singleA, r5, "to left of", refB, collB, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_b_using_e() {
        checkLink(collA, r5, "to left of", refB, singleB, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_bb_using_e() {
        checkLink(collA, r5, "to left of", refB, collB, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R5_b_using_ee() {
        checkLink(singleA, r5, "to left of", refB, singleB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R5_bb_using_ee() {
        checkLink(singleA, r5, "to left of", refB, collB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_b_using_ee() {
        checkLink(collA, r5, "to left of", refB, singleB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_bb_using_ee() {
        checkLink(collA, r5, "to left of", refB, collB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R5() {
        checkLink(singleA, r5, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_aa_R5() {
        checkLink(collA, r5, "to left of", refB, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    // R5 <-

    public void test_link_b_R5_a() {
        checkLink(singleB, r5, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R5_a() {
        checkLink(collB, r5, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R5_aa() {
        checkLink(singleB, r5, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R5_aa() {
        checkLink(collB, r5, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R5_a_using_e() {
        checkLink(singleB, r5, "to right of", refA, singleA, singleE);
    }

    public void test_link_bb_R5_a_using_e() {
        checkLink(collB, r5, "to right of", refA, singleA, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5_aa_using_e() {
        checkLink(singleB, r5, "to right of", refA, collA, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R5_aa_using_er5() {
        checkLink(collB, r5, "to right of", refA, collA, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5_a_using_ee() {
        checkLink(singleB, r5, "to right of", refA, singleA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R5_a_using_ee() {
        checkLink(collB, r5, "to right of", refA, singleA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5_aa_using_ee() {
        checkLink(singleB, r5, "to right of", refA, collA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R5_aa_using_ee() {
        checkLink(collB, r5, "to right of", refA, collA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5() {
        checkLink(singleB, r5, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

    public void test_link_bb_R5() {
        checkLink(collB, r5, "to right of", refA, null, null, SemanticErrorCode.LinkMustSupplyRhs);
    }

}
