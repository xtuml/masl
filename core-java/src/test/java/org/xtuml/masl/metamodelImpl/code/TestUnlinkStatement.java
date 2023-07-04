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

public class TestUnlinkStatement extends TestCase {

    public void checkUnlink(final Expression lhs, final RelationshipDeclaration.Reference relRef,
            final String objOrRole, final ObjectNameExpression obj, final Expression rhs, final Expression assoc) {
        ErrorLog.getInstance().reset();
        LinkUnlinkStatement.create(null, LinkUnlinkStatement.UNLINK, lhs,
                RelationshipSpecification.createReference(lhs, relRef, objOrRole, obj, false, false), rhs, assoc);
        ErrorLog.getInstance().checkErrors();
    }

    public void checkUnlink(final Expression lhs, final RelationshipDeclaration.Reference relRef,
            final String objOrRole, final ObjectNameExpression obj, final Expression rhs, final Expression assoc,
            final ErrorCode... errors) {
        ErrorLog.getInstance().reset();
        LinkUnlinkStatement.create(null, LinkUnlinkStatement.UNLINK, lhs,
                RelationshipSpecification.createReference(lhs, relRef, objOrRole, obj, false, false), rhs, assoc);
        ErrorLog.getInstance().checkErrors(errors);
    }

    // R1 ->

    public void test_link_a_R1_b() {
        checkUnlink(singleA, r1, "to left of", refB, singleB, null);
    }

    public void test_link_a_R1_bb() {
        checkUnlink(singleA, r1, "to left of", refB, collB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R1_b() {
        checkUnlink(collA, r1, "to left of", refB, singleB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R1_bb() {
        checkUnlink(collA, r1, "to left of", refB, collB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R1() {
        checkUnlink(singleA, r1, "to left of", refB, null, null);
    }

    public void test_link_aa_R1() {
        checkUnlink(collA, r1, "to left of", refB, null, null);
    }

    public void test_link_a_R1_b_using_a() {
        checkUnlink(singleA, r1, "to left of", refB, singleB, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R1 <-

    public void test_link_b_R1_a() {
        checkUnlink(singleB, r1, "to right of", refA, singleA, null);
    }

    public void test_link_bb_R1_a() {
        checkUnlink(collB, r1, "to right of", refA, singleA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R1_aa() {
        checkUnlink(singleB, r1, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R1_aa() {
        checkUnlink(collB, r1, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R1() {
        checkUnlink(singleB, r1, "to right of", refA, null, null);
    }

    public void test_link_bb_R1() {
        checkUnlink(collB, r1, "to right of", refA, null, null);
    }

    public void test_link_b_R1_a_using_a() {
        checkUnlink(singleB, r1, "to right of", refA, singleA, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R2 ->

    public void test_link_a_R2_b() {
        checkUnlink(singleA, r2, "to left of", refB, singleB, null);
    }

    public void test_link_a_R2_bb() {
        checkUnlink(singleA, r2, "to left of", refB, collB, null);
    }

    public void test_link_aa_R2_b() {
        checkUnlink(collA, r2, "to left of", refB, singleB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R2_bb() {
        checkUnlink(collA, r2, "to left of", refB, collB, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R2() {
        checkUnlink(singleA, r2, "to left of", refB, null, null);
    }

    public void test_link_aa_R2() {
        checkUnlink(collA, r2, "to left of", refB, null, null);
    }

    public void test_link_a_R2_b_using_a() {
        checkUnlink(singleA, r2, "to left of", refB, singleB, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R2 <-

    public void test_link_b_R2_a() {
        checkUnlink(singleB, r2, "to right of", refA, singleA, null);
    }

    public void test_link_bb_R2_a() {
        checkUnlink(collB, r2, "to right of", refA, singleA, null);
    }

    public void test_link_b_R2_aa() {
        checkUnlink(singleB, r2, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R2_aa() {
        checkUnlink(collB, r2, "to right of", refA, collA, null, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R2() {
        checkUnlink(singleB, r2, "to right of", refA, null, null);
    }

    public void test_link_bb_R2() {
        checkUnlink(collB, r2, "to right of", refA, null, null);
    }

    public void test_link_b_R2_a_using_a() {
        checkUnlink(singleB, r2, "to right of", refA, singleA, singleA, SemanticErrorCode.NonAssocWithUsing);
    }

    // R3 ->

    public void test_link_a_R3_b() {
        checkUnlink(singleA, r3, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R3_bb() {
        checkUnlink(singleA, r3, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R3_b() {
        checkUnlink(collA, r3, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R3_bb() {
        checkUnlink(collA, r3, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R3_b_using_c() {
        checkUnlink(singleA, r3, "to left of", refB, singleB, singleC);
    }

    public void test_link_a_R3_bb_using_c() {
        checkUnlink(singleA, r3, "to left of", refB, collB, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_b_using_c() {
        checkUnlink(collA, r3, "to left of", refB, singleB, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_bb_using_c() {
        checkUnlink(collA, r3, "to left of", refB, collB, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3_b_using_cc() {
        checkUnlink(singleA, r3, "to left of", refB, singleB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3_bb_using_cc() {
        checkUnlink(singleA, r3, "to left of", refB, collB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_b_using_cc() {
        checkUnlink(collA, r3, "to left of", refB, singleB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R3_bb_using_cc() {
        checkUnlink(collA, r3, "to left of", refB, collB, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R3() {
        checkUnlink(singleA, r3, "to left of", refB, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R3() {
        checkUnlink(collA, r3, "to left of", refB, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    // R3 <-

    public void test_link_b_R3_a() {
        checkUnlink(singleB, r3, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R3_a() {
        checkUnlink(collB, r3, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R3_aa() {
        checkUnlink(singleB, r3, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R3_aa() {
        checkUnlink(collB, r3, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R3_a_using_c() {
        checkUnlink(singleB, r3, "to right of", refA, singleA, singleC);
    }

    public void test_link_bb_R3_a_using_c() {
        checkUnlink(collB, r3, "to right of", refA, singleA, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_aa_using_c() {
        checkUnlink(singleB, r3, "to right of", refA, collA, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_aa_using_c() {
        checkUnlink(collB, r3, "to right of", refA, collA, singleC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_a_using_cc() {
        checkUnlink(singleB, r3, "to right of", refA, singleA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_a_using_cc() {
        checkUnlink(collB, r3, "to right of", refA, singleA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3_aa_using_cc() {
        checkUnlink(singleB, r3, "to right of", refA, collA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R3_aa_using_cc() {
        checkUnlink(collB, r3, "to right of", refA, collA, collC, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R3() {
        checkUnlink(singleB, r3, "to right of", refA, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R3() {
        checkUnlink(collB, r3, "to right of", refA, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    // R4 ->

    public void test_link_a_R4_b() {
        checkUnlink(singleA, r4, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R4_bb() {
        checkUnlink(singleA, r4, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R4_b() {
        checkUnlink(collA, r4, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R4_bb() {
        checkUnlink(collA, r4, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R4_b_using_d() {
        checkUnlink(singleA, r4, "to left of", refB, singleB, singleD);
    }

    public void test_link_a_R4_bb_using_d() {
        checkUnlink(singleA, r4, "to left of", refB, collB, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_b_using_d() {
        checkUnlink(collA, r4, "to left of", refB, singleB, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_bb_using_d() {
        checkUnlink(collA, r4, "to left of", refB, collB, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4_b_using_dd() {
        checkUnlink(singleA, r4, "to left of", refB, singleB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4_bb_using_dd() {
        checkUnlink(singleA, r4, "to left of", refB, collB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_b_using_dd() {
        checkUnlink(collA, r4, "to left of", refB, singleB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R4_bb_using_dd() {
        checkUnlink(collA, r4, "to left of", refB, collB, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R4() {
        checkUnlink(singleA, r4, "to left of", refB, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R4() {
        checkUnlink(collA, r4, "to left of", refB, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    // R4 <-

    public void test_link_b_R4_a() {
        checkUnlink(singleB, r4, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R4_a() {
        checkUnlink(collB, r4, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R4_aa() {
        checkUnlink(singleB, r4, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R4_aa() {
        checkUnlink(collB, r4, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R4_a_using_d() {
        checkUnlink(singleB, r4, "to right of", refA, singleA, singleD);
    }

    public void test_link_bb_R4_a_using_d() {
        checkUnlink(collB, r4, "to right of", refA, singleA, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4_aa_using_d() {
        checkUnlink(singleB, r4, "to right of", refA, collA, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_aa_using_d() {
        checkUnlink(collB, r4, "to right of", refA, collA, singleD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4_a_using_dd() {
        checkUnlink(singleB, r4, "to right of", refA, singleA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_a_using_dd() {
        checkUnlink(collB, r4, "to right of", refA, singleA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4_aa_using_dd() {
        checkUnlink(singleB, r4, "to right of", refA, collA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R4_aa_using_dd() {
        checkUnlink(collB, r4, "to right of", refA, collA, collD, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R4() {
        checkUnlink(singleB, r4, "to right of", refA, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R4() {
        checkUnlink(collB, r4, "to right of", refA, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    // R5 ->

    public void test_link_a_R5_b() {
        checkUnlink(singleA, r5, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R5_bb() {
        checkUnlink(singleA, r5, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R5_b() {
        checkUnlink(collA, r5, "to left of", refB, singleB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R5_bb() {
        checkUnlink(collA, r5, "to left of", refB, collB, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_a_R5_b_using_e() {
        checkUnlink(singleA, r5, "to left of", refB, singleB, singleE);
    }

    public void test_link_a_R5_bb_using_e() {
        checkUnlink(singleA, r5, "to left of", refB, collB, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_b_using_e() {
        checkUnlink(collA, r5, "to left of", refB, singleB, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_bb_using_e() {
        checkUnlink(collA, r5, "to left of", refB, collB, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R5_b_using_ee() {
        checkUnlink(singleA, r5, "to left of", refB, singleB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R5_bb_using_ee() {
        checkUnlink(singleA, r5, "to left of", refB, collB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_b_using_ee() {
        checkUnlink(collA, r5, "to left of", refB, singleB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_aa_R5_bb_using_ee() {
        checkUnlink(collA, r5, "to left of", refB, collB, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_a_R5() {
        checkUnlink(singleA, r5, "to left of", refB, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_aa_R5() {
        checkUnlink(collA, r5, "to left of", refB, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    // R5 <-

    public void test_link_b_R5_a() {
        checkUnlink(singleB, r5, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R5_a() {
        checkUnlink(collB, r5, "to right of", refA, singleA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R5_aa() {
        checkUnlink(singleB, r5, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R5_aa() {
        checkUnlink(collB, r5, "to right of", refA, collA, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_b_R5_a_using_e() {
        checkUnlink(singleB, r5, "to right of", refA, singleA, singleE);
    }

    public void test_link_bb_R5_a_using_e() {
        checkUnlink(collB, r5, "to right of", refA, singleA, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5_aa_using_e() {
        checkUnlink(singleB, r5, "to right of", refA, collA, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R5_aa_using_er5() {
        checkUnlink(collB, r5, "to right of", refA, collA, singleE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5_a_using_ee() {
        checkUnlink(singleB, r5, "to right of", refA, singleA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R5_a_using_ee() {
        checkUnlink(collB, r5, "to right of", refA, singleA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5_aa_using_ee() {
        checkUnlink(singleB, r5, "to right of", refA, collA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_bb_R5_aa_using_ee() {
        checkUnlink(collB, r5, "to right of", refA, collA, collE, SemanticErrorCode.NotInstanceType);
    }

    public void test_link_b_R5() {
        checkUnlink(singleB, r5, "to right of", refA, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

    public void test_link_bb_R5() {
        checkUnlink(collB, r5, "to right of", refA, null, null, SemanticErrorCode.AssocNeedsUsing);
    }

}
