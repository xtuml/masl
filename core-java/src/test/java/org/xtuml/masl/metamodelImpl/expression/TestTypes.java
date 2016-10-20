//
// File: TestData.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.metamodelImpl.expression;

import org.xtuml.masl.metamodelImpl.common.PragmaList;
import org.xtuml.masl.metamodelImpl.common.Visibility;
import org.xtuml.masl.metamodelImpl.domain.Domain;
import org.xtuml.masl.metamodelImpl.type.CharacterType;
import org.xtuml.masl.metamodelImpl.type.IntegerType;
import org.xtuml.masl.metamodelImpl.type.RealType;
import org.xtuml.masl.metamodelImpl.type.SequenceType;
import org.xtuml.masl.metamodelImpl.type.StringType;
import org.xtuml.masl.metamodelImpl.type.TypeDeclaration;
import org.xtuml.masl.metamodelImpl.type.UserDefinedType;


public final class TestTypes
{

  static private final Domain     domain  = new Domain(null, "TestExpressions");

  static private final PragmaList pragmas = new PragmaList();

  public static TestTypes         data1   = new TestTypes("data1");
  public static TestTypes         data2   = new TestTypes("data2");


  private TestTypes ( final String prefix )
  {

    bCharacter = CharacterType.create(null, false);
    bInteger = IntegerType.create(null, false);
    bReal = RealType.create(null, false);
    bString = StringType.create(null, false);

    udIntTypeDecl = TypeDeclaration.create(null, domain, prefix + "udIntType", Visibility.PUBLIC, bInteger, pragmas);
    udIntType = UserDefinedType.create(domain.getReference(null), prefix + "udIntType");

    udRealTypeDecl = TypeDeclaration.create(null, domain, prefix + "udRealType", Visibility.PUBLIC, bReal, pragmas);
    udRealType = UserDefinedType.create(domain.getReference(null), prefix + "udRealType");

    udCharacterTypeDecl = TypeDeclaration.create(null,
                                                 domain,
                                                 prefix + "udCharacterType",
                                                 Visibility.PUBLIC,
                                                 bCharacter,
                                                 pragmas);
    udCharacterType = UserDefinedType.create(domain.getReference(null), prefix + "udCharacterType");

    udStringTypeDecl = TypeDeclaration.create(null,
                                              domain,
                                              prefix + "udStringType",
                                              Visibility.PUBLIC,
                                              bString,
                                              pragmas);
    udStringType = UserDefinedType.create(domain.getReference(null), prefix + "udStringType");

    seqOfCharacter = SequenceType.create(null, bCharacter, null, false);
    seqOfUdCharacterType = SequenceType.create(null, udCharacterType, null, false);

    seqOfUdStringType = SequenceType.create(null, udStringType, null, false);


    udSeqOfUdStringTypeDecl = TypeDeclaration.create(null,
                                                     domain,
                                                     prefix + "udSeqOfUdStringType",
                                                     Visibility.PUBLIC,
                                                     seqOfUdStringType,
                                                     pragmas);
    udSeqOfUdStringType = UserDefinedType.create(domain.getReference(null), prefix + "udSeqOfUdStringType");


  }

  public final CharacterType   bCharacter;
  public final IntegerType     bInteger;
  public final RealType        bReal;
  public final StringType      bString;

  /**
   * <code>type udIntType is integer;</code>
   */
  public final UserDefinedType udIntType;
  public final TypeDeclaration udIntTypeDecl;

  /**
   * <code>type udIntType is integer;</code>
   */
  public final UserDefinedType udRealType;
  public final TypeDeclaration udRealTypeDecl;

  /**
   * <code>type udCharacterType is integer;</code>
   */
  public final UserDefinedType udCharacterType;
  public final TypeDeclaration udCharacterTypeDecl;


  /**
   * <code>type udCharacterType is integer;</code>
   */
  public final UserDefinedType udStringType;
  public final TypeDeclaration udStringTypeDecl;


  /**
   * <code>sequence of character</code>
   */
  public final SequenceType    seqOfCharacter;


  /**
   * <code>sequence of {@link #udCharacterType};</code>
   */
  public final SequenceType    seqOfUdCharacterType;

  /**
   * <code>sequence of {@link #udCharacterType};</code>
   */
  public final SequenceType    seqOfUdStringType;

  public final UserDefinedType udSeqOfUdStringType;
  public final TypeDeclaration udSeqOfUdStringTypeDecl;


}
