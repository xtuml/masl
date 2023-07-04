/*
 ----------------------------------------------------------------------------
 (c) 2009-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
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
package org.xtuml.masl.metamodelImpl;

import org.xtuml.masl.metamodelImpl.code.VariableDefinition;
import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.expression.Expression;
import org.xtuml.masl.metamodelImpl.expression.ObjectNameExpression;
import org.xtuml.masl.metamodelImpl.expression.VariableNameExpression;
import org.xtuml.masl.metamodelImpl.object.AttributeDeclaration;
import org.xtuml.masl.metamodelImpl.object.ObjectDeclaration;
import org.xtuml.masl.metamodelImpl.object.ReferentialAttributeDefinition;
import org.xtuml.masl.metamodelImpl.relationship.*;
import org.xtuml.masl.metamodelImpl.type.BasicType;
import org.xtuml.masl.metamodelImpl.type.InstanceType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;

import java.util.ArrayList;

public class SampleDomain {

    // Set up the following relationships...
    // R1 : A<----->B
    //
    // R2 : A<---->>B
    //
    // R3 : A<----->B
    // C
    //
    // R4 : A<---->>B
    // D
    //
    // R5 : A<<--->>B
    // E

    // R6 : A<----->B
    // F F has non-referential id
    //
    // R7 : A<---->>B
    // G G has non-referential id
    //
    // R8 : A<<--->>B
    // H H has non-referential id

    static public final Domain domain = new Domain(null, "TestLinkUnlink");
    static public final Domain.Reference domainRef = domain.getReference(null);

    static public final ObjectDeclaration objA = ObjectDeclaration.create(null, domain, "A", null);
    static public final ObjectDeclaration objB = ObjectDeclaration.create(null, domain, "B", null);
    static public final ObjectDeclaration objC = ObjectDeclaration.create(null, domain, "C", null);
    static public final ObjectDeclaration objD = ObjectDeclaration.create(null, domain, "D", null);
    static public final ObjectDeclaration objE = ObjectDeclaration.create(null, domain, "E", null);
    static public final ObjectDeclaration objF = ObjectDeclaration.create(null, domain, "F", null);
    static public final ObjectDeclaration objG = ObjectDeclaration.create(null, domain, "G", null);
    static public final ObjectDeclaration objH = ObjectDeclaration.create(null, domain, "H", null);

    static public final ObjectNameExpression refA = ObjectNameExpression.create(domain.getReference(null), "A");
    static public final ObjectNameExpression refB = ObjectNameExpression.create(domain.getReference(null), "B");
    static public final ObjectNameExpression refC = ObjectNameExpression.create(domain.getReference(null), "C");
    static public final ObjectNameExpression refD = ObjectNameExpression.create(domain.getReference(null), "D");
    static public final ObjectNameExpression refE = ObjectNameExpression.create(domain.getReference(null), "E");
    static public final ObjectNameExpression refF = ObjectNameExpression.create(domain.getReference(null), "F");
    static public final ObjectNameExpression refG = ObjectNameExpression.create(domain.getReference(null), "G");
    static public final ObjectNameExpression refH = ObjectNameExpression.create(domain.getReference(null), "H");

    static public final IntegerType integer = IntegerType.createAnonymous();

    static {
        NormalRelationshipDeclaration.create(null,
                                             domain,
                                             "R1",
                                             HalfRelationship.create(refA,
                                                                     true,
                                                                     "to left of",
                                                                     MultiplicityType.ONE,
                                                                     refB),
                                             HalfRelationship.create(refB,
                                                                     true,
                                                                     "to right of",
                                                                     MultiplicityType.ONE,
                                                                     refA),
                                             new PragmaList());

        NormalRelationshipDeclaration.create(null,
                                             domain,
                                             "R2",
                                             HalfRelationship.create(refA,
                                                                     true,
                                                                     "to left of",
                                                                     MultiplicityType.MANY,
                                                                     refB),
                                             HalfRelationship.create(refB,
                                                                     true,
                                                                     "to right of",
                                                                     MultiplicityType.ONE,
                                                                     refA),
                                             new PragmaList());

        AssociativeRelationshipDeclaration.create(null,
                                                  domain,
                                                  "R3",
                                                  HalfRelationship.create(refA,
                                                                          true,
                                                                          "to left of",
                                                                          MultiplicityType.ONE,
                                                                          refB),
                                                  HalfRelationship.create(refB,
                                                                          true,
                                                                          "to right of",
                                                                          MultiplicityType.ONE,
                                                                          refA),
                                                  refC,
                                                  new PragmaList());

        AssociativeRelationshipDeclaration.create(null,
                                                  domain,
                                                  "R4",
                                                  HalfRelationship.create(refA,
                                                                          true,
                                                                          "to left of",
                                                                          MultiplicityType.MANY,
                                                                          refB),
                                                  HalfRelationship.create(refB,
                                                                          true,
                                                                          "to right of",
                                                                          MultiplicityType.ONE,
                                                                          refA),
                                                  refD,
                                                  new PragmaList());

        AssociativeRelationshipDeclaration.create(null,
                                                  domain,
                                                  "R5",
                                                  HalfRelationship.create(refA,
                                                                          true,
                                                                          "to left of",
                                                                          MultiplicityType.MANY,
                                                                          refB),
                                                  HalfRelationship.create(refB,
                                                                          true,
                                                                          "to right of",
                                                                          MultiplicityType.MANY,
                                                                          refA),
                                                  refE,
                                                  new PragmaList());

        AssociativeRelationshipDeclaration.create(null,
                                                  domain,
                                                  "R6",
                                                  HalfRelationship.create(refA,
                                                                          true,
                                                                          "to left of",
                                                                          MultiplicityType.ONE,
                                                                          refB),
                                                  HalfRelationship.create(refB,
                                                                          true,
                                                                          "to right of",
                                                                          MultiplicityType.ONE,
                                                                          refA),
                                                  refF,
                                                  new PragmaList());

        AssociativeRelationshipDeclaration.create(null,
                                                  domain,
                                                  "R7",
                                                  HalfRelationship.create(refA,
                                                                          true,
                                                                          "to left of",
                                                                          MultiplicityType.MANY,
                                                                          refB),
                                                  HalfRelationship.create(refB,
                                                                          true,
                                                                          "to right of",
                                                                          MultiplicityType.ONE,
                                                                          refA),
                                                  refG,
                                                  new PragmaList());

        AssociativeRelationshipDeclaration.create(null,
                                                  domain,
                                                  "R8",
                                                  HalfRelationship.create(refA,
                                                                          true,
                                                                          "to left of",
                                                                          MultiplicityType.MANY,
                                                                          refB),
                                                  HalfRelationship.create(refB,
                                                                          true,
                                                                          "to right of",
                                                                          MultiplicityType.MANY,
                                                                          refA),
                                                  refH,
                                                  new PragmaList());

        AttributeDeclaration.create(objA,
                                    "id",
                                    integer,
                                    true,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());
        AttributeDeclaration.create(objA,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());
        AttributeDeclaration.create(objB,
                                    "id",
                                    integer,
                                    true,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());
        AttributeDeclaration.create(objB,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R1R2 =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R1R2.add(ReferentialAttributeDefinition.create(objA,
                                                                  RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                    null,
                                                                                                                    objB),
                                                                                                            RelationshipDeclaration.createReference(
                                                                                                                    domainRef,
                                                                                                                    "R1"),
                                                                                                            "A",
                                                                                                            refA,
                                                                                                            false,
                                                                                                            false),
                                                                  "id"));
        refAttList_R1R2.add(ReferentialAttributeDefinition.create(objA,
                                                                  RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                    null,
                                                                                                                    objB),
                                                                                                            RelationshipDeclaration.createReference(
                                                                                                                    domainRef,
                                                                                                                    "R2"),
                                                                                                            "A",
                                                                                                            refA,
                                                                                                            false,
                                                                                                            false),
                                                                  "id"));
        AttributeDeclaration.create(objB, "a_id", integer, false, false, refAttList_R1R2, null, new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R3A =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R3A.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objC),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R3"),
                                                                                                           "A",
                                                                                                           refA,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));
        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R3B =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R3B.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objC),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R3"),
                                                                                                           "B",
                                                                                                           refB,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));

        AttributeDeclaration.create(objC, "a_id", integer, true, false, refAttList_R3A, null, new PragmaList());
        AttributeDeclaration.create(objC, "b_id", integer, true, false, refAttList_R3B, null, new PragmaList());
        AttributeDeclaration.create(objC,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R4A =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R4A.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objD),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R4"),
                                                                                                           "A",
                                                                                                           refA,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));
        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R4B =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R4B.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objD),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R4"),
                                                                                                           "B",
                                                                                                           refB,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));

        AttributeDeclaration.create(objD, "a_id", integer, true, false, refAttList_R4A, null, new PragmaList());
        AttributeDeclaration.create(objD, "b_id", integer, true, false, refAttList_R4B, null, new PragmaList());
        AttributeDeclaration.create(objD,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R5A =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R5A.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objE),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R5"),
                                                                                                           "A",
                                                                                                           refA,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));
        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R5B =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R5B.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objE),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R5"),
                                                                                                           "B",
                                                                                                           refB,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));

        AttributeDeclaration.create(objE, "a_id", integer, true, false, refAttList_R5A, null, new PragmaList());
        AttributeDeclaration.create(objE, "b_id", integer, true, false, refAttList_R5B, null, new PragmaList());
        AttributeDeclaration.create(objE,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R6A =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R6A.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objF),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R6"),
                                                                                                           "A",
                                                                                                           refA,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));
        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R6B =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R6B.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objF),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R6"),
                                                                                                           "B",
                                                                                                           refB,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));

        AttributeDeclaration.create(objF,
                                    "id",
                                    integer,
                                    true,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());
        AttributeDeclaration.create(objF, "a_id", integer, false, false, refAttList_R6A, null, new PragmaList());
        AttributeDeclaration.create(objF, "b_id", integer, false, false, refAttList_R6B, null, new PragmaList());
        AttributeDeclaration.create(objF,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R7A =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R7A.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objG),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R7"),
                                                                                                           "A",
                                                                                                           refA,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));
        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R7B =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R7B.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objG),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R7"),
                                                                                                           "B",
                                                                                                           refB,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));

        AttributeDeclaration.create(objG,
                                    "id",
                                    integer,
                                    true,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());
        AttributeDeclaration.create(objG, "a_id", integer, false, false, refAttList_R7A, null, new PragmaList());
        AttributeDeclaration.create(objG, "b_id", integer, false, false, refAttList_R7B, null, new PragmaList());
        AttributeDeclaration.create(objG,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R8A =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R8A.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objH),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R8"),
                                                                                                           "A",
                                                                                                           refA,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));
        final ArrayList<ReferentialAttributeDefinition>
                refAttList_R8B =
                new ArrayList<ReferentialAttributeDefinition>();
        refAttList_R8B.add(ReferentialAttributeDefinition.create(objA,
                                                                 RelationshipSpecification.createReference(new ObjectNameExpression(
                                                                                                                   null,
                                                                                                                   objH),
                                                                                                           RelationshipDeclaration.createReference(
                                                                                                                   domainRef,
                                                                                                                   "R8"),
                                                                                                           "B",
                                                                                                           refB,
                                                                                                           false,
                                                                                                           false),
                                                                 "id"));

        AttributeDeclaration.create(objH,
                                    "id",
                                    integer,
                                    true,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());
        AttributeDeclaration.create(objH, "a_id", integer, false, false, refAttList_R8A, null, new PragmaList());
        AttributeDeclaration.create(objH, "b_id", integer, false, false, refAttList_R8B, null, new PragmaList());
        AttributeDeclaration.create(objH,
                                    "non_id",
                                    integer,
                                    false,
                                    false,
                                    new ArrayList<ReferentialAttributeDefinition>(),
                                    null,
                                    new PragmaList());

        objA.setFullyDefined();
        objB.setFullyDefined();
        objC.setFullyDefined();
        objD.setFullyDefined();
        objE.setFullyDefined();
        objF.setFullyDefined();
        objG.setFullyDefined();
        objH.setFullyDefined();

        domain.setFullyDefined();
    }

    static public final RelationshipDeclaration.Reference r1 = RelationshipDeclaration.createReference(domainRef, "R1");
    static public final RelationshipDeclaration.Reference r2 = RelationshipDeclaration.createReference(domainRef, "R2");
    static public final RelationshipDeclaration.Reference r3 = RelationshipDeclaration.createReference(domainRef, "R3");
    static public final RelationshipDeclaration.Reference r4 = RelationshipDeclaration.createReference(domainRef, "R4");
    static public final RelationshipDeclaration.Reference r5 = RelationshipDeclaration.createReference(domainRef, "R5");
    static public final RelationshipDeclaration.Reference r6 = RelationshipDeclaration.createReference(domainRef, "R6");
    static public final RelationshipDeclaration.Reference r7 = RelationshipDeclaration.createReference(domainRef, "R7");
    static public final RelationshipDeclaration.Reference r8 = RelationshipDeclaration.createReference(domainRef, "R8");

    static public final BasicType singleAType = InstanceType.create(null, refA, false);
    static public final VariableDefinition
            singleAdef =
            VariableDefinition.create("varA", singleAType, false, null, new PragmaList());
    static public final Expression singleA = new VariableNameExpression(null, singleAdef);

    static public final BasicType singleBType = InstanceType.create(null, refB, false);
    static public final VariableDefinition
            singleBdef =
            VariableDefinition.create("varB", singleBType, false, null, new PragmaList());
    static public final Expression singleB = new VariableNameExpression(null, singleBdef);

    static public final BasicType singleCType = InstanceType.create(null, refC, false);
    static public final VariableDefinition
            singleCdef =
            VariableDefinition.create("varC", singleCType, false, null, new PragmaList());
    static public final Expression singleC = new VariableNameExpression(null, singleCdef);

    static public final BasicType singleDType = InstanceType.create(null, refD, false);
    static public final VariableDefinition
            singleDdef =
            VariableDefinition.create("varD", singleDType, false, null, new PragmaList());
    static public final Expression singleD = new VariableNameExpression(null, singleDdef);

    static public final BasicType singleEType = InstanceType.create(null, refE, false);
    static public final VariableDefinition
            singleEdef =
            VariableDefinition.create("varE", singleEType, false, null, new PragmaList());
    static public final Expression singleE = new VariableNameExpression(null, singleEdef);

    static public final BasicType singleFType = InstanceType.create(null, refF, false);
    static public final VariableDefinition
            singleFdef =
            VariableDefinition.create("varF", singleFType, false, null, new PragmaList());
    static public final Expression singleF = new VariableNameExpression(null, singleFdef);

    static public final BasicType singleGType = InstanceType.create(null, refG, false);
    static public final VariableDefinition
            singleGdef =
            VariableDefinition.create("varG", singleGType, false, null, new PragmaList());
    static public final Expression singleG = new VariableNameExpression(null, singleGdef);

    static public final BasicType singleHType = InstanceType.create(null, refH, false);
    static public final VariableDefinition
            singleHdef =
            VariableDefinition.create("varH", singleHType, false, null, new PragmaList());
    static public final Expression singleH = new VariableNameExpression(null, singleHdef);

    static public final BasicType collAType = SequenceType.createAnonymous(singleAType);
    static public final VariableDefinition
            collAdef =
            VariableDefinition.create("collA", collAType, false, null, new PragmaList());
    static public final Expression collA = new VariableNameExpression(null, collAdef);

    static public final BasicType collBType = SequenceType.createAnonymous(singleBType);
    static public final VariableDefinition
            collBdef =
            VariableDefinition.create("varB", collBType, false, null, new PragmaList());
    static public final Expression collB = new VariableNameExpression(null, collBdef);

    static public final BasicType collCType = SequenceType.createAnonymous(singleCType);
    static public final VariableDefinition
            collCdef =
            VariableDefinition.create("collB", collCType, false, null, new PragmaList());
    static public final Expression collC = new VariableNameExpression(null, collCdef);

    static public final BasicType collDType = SequenceType.createAnonymous(singleDType);
    static public final VariableDefinition
            collDdef =
            VariableDefinition.create("collB", collDType, false, null, new PragmaList());
    static public final Expression collD = new VariableNameExpression(null, collDdef);

    static public final BasicType collEType = SequenceType.createAnonymous(singleEType);
    static public final VariableDefinition
            collEdef =
            VariableDefinition.create("collB", collEType, false, null, new PragmaList());
    static public final Expression collE = new VariableNameExpression(null, collEdef);

    static public final BasicType collFType = SequenceType.createAnonymous(singleFType);
    static public final VariableDefinition
            collFdef =
            VariableDefinition.create("collF", collFType, false, null, new PragmaList());
    static public final Expression collF = new VariableNameExpression(null, collFdef);

    static public final BasicType collGType = SequenceType.createAnonymous(singleGType);
    static public final VariableDefinition
            collGdef =
            VariableDefinition.create("collG", collGType, false, null, new PragmaList());
    static public final Expression collG = new VariableNameExpression(null, collGdef);

    static public final BasicType collHType = SequenceType.createAnonymous(singleHType);
    static public final VariableDefinition
            collHdef =
            VariableDefinition.create("collH", collHType, false, null, new PragmaList());
    static public final Expression collH = new VariableNameExpression(null, collHdef);
}
