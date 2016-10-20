//
// File: ASN1.java
//
// UK Crown Copyright (c) 2010. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.TemplateType;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypenameTemplateParameter;
import org.xtuml.masl.cppgen.UnaryExpression;
import org.xtuml.masl.cppgen.UnaryOperator;
import org.xtuml.masl.cppgen.Variable;


public class ASN1
{

  public static final Library library         = new Library("asn1").inBuildSet(Architecture.buildSet);

  private static CodeFile     DEREncoderInc   = library.createInterfaceHeader("asn1/DEREncoder.hh");
  private static CodeFile     DEREncodeInc    = library.createInterfaceHeader("asn1/DEREncode.hh");
  private static CodeFile     universalTagInc = library.createInterfaceHeader("asn1/UniversalTag.hh");
  private static CodeFile     BERDecoderInc   = library.createInterfaceHeader("asn1/BERDecoder.hh");
  private static CodeFile     BERDeodeInc     = library.createInterfaceHeader("asn1/BERDecode.hh");

  private static Namespace      ASN1_NAMESPACE  = new Namespace("ASN1");
  private static Namespace      DER_NAMESPACE   = new Namespace("DER", ASN1_NAMESPACE);
  private static Namespace BER_NAMESPACE = new Namespace("BER", ASN1_NAMESPACE);

  public static Expression      sequenceTag     = new Variable("SEQUENCE", ASN1_NAMESPACE, universalTagInc).asExpression();

  public static Class           DEREncoder      = new Class("Encoder", DER_NAMESPACE, DEREncoderInc);
  public static Function        DEREncode       = new Function("encode", DER_NAMESPACE, DEREncodeInc);

  public static Function encodeValue ()
  {
    final Function function = new Function("encodeValue", DER_NAMESPACE);
    function.setReturnType(new TypeUsage(DEREncoder));
    function.setSpecialisationFrom(DEREncodeInc);
    return function;
  }


  public static Statement addChild ( final Expression encoder, final Expression child )
  {
    return new Function("addChild").asFunctionCall(encoder, false, DEREncode.asFunctionCall(child)).asStatement();
  }


  public static Class BERDecoder ( final TypeUsage iterator )
  {
    final Class result = new Class("Decoder", BER_NAMESPACE, BERDecoderInc);
    result.addTemplateSpecialisation(iterator);
    return result;
  }

  public static Function BERDecode = new Function("decode", BER_NAMESPACE, BERDeodeInc);

  public static Variable getChildIterator ( final Expression decoder, final TypeUsage iterator )
  {
    return new Variable(new TypeUsage(BERDecoder(iterator).referenceNestedType("ChildIterator")),
                        "childIt",
                        new Function("getChildrenBegin").asFunctionCall(decoder, false));
  }

  public static Statement checkChildPresent ( final Expression decoder, final Expression iterator )
  {
    return new Function("checkChildPresent").asFunctionCall(decoder, false, iterator).asStatement();
  }

  public static Statement checkNoMoreChildren ( final Expression decoder, final Expression iterator )
  {
    return new Function("checkNoMoreChildren").asFunctionCall(decoder, false, iterator).asStatement();
  }

  public static class DecodeValue
  {

    public DecodeValue ()
    {
      function = new Function("decodeValue", BER_NAMESPACE);
      function.setSpecialisationFrom(BERDeodeInc);
      final TemplateType iterator = new TemplateType("I");
      function.addTemplateParameter(new TypenameTemplateParameter(iterator));
      decoder = function.createParameter(new TypeUsage(BERDecoder(new TypeUsage(iterator)), TypeUsage.ConstReference), "decoder");
      childIt = getChildIterator(decoder.asExpression(), new TypeUsage(iterator));
    }

    Function function;
    Variable decoder;
    Variable childIt;
  }


  public static Statement checkHeader ( final Expression decoder, final Expression tag, final boolean constructed )
  {
    return new Function("checkHeader").asFunctionCall(decoder,
                                                      false,
                                                      tag,
                                                      constructed ? Literal.TRUE : Literal.FALSE).asStatement();
  }

  public static Statement getChild ( final Expression childIt, final Expression value )
  {
    final Expression childDecoder = new UnaryExpression(UnaryOperator.DEREFERENCE, new UnaryExpression(UnaryOperator.POSTINCREMENT,
                                                                                                       childIt));
    return BERDecode.asFunctionCall(childDecoder, value).asStatement();
  }
}
