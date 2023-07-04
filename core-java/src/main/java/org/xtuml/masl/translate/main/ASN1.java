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
package org.xtuml.masl.translate.main;

import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.*;

public class ASN1 {

    public static final Library library = new Library("asn1").inBuildSet(Architecture.buildSet);

    private static final CodeFile DEREncoderInc = library.createInterfaceHeader("asn1/DEREncoder.hh");
    private static final CodeFile DEREncodeInc = library.createInterfaceHeader("asn1/DEREncode.hh");
    private static final CodeFile universalTagInc = library.createInterfaceHeader("asn1/UniversalTag.hh");
    private static final CodeFile BERDecoderInc = library.createInterfaceHeader("asn1/BERDecoder.hh");
    private static final CodeFile BERDeodeInc = library.createInterfaceHeader("asn1/BERDecode.hh");

    private static final Namespace ASN1_NAMESPACE = new Namespace("ASN1");
    private static final Namespace DER_NAMESPACE = new Namespace("DER", ASN1_NAMESPACE);
    private static final Namespace BER_NAMESPACE = new Namespace("BER", ASN1_NAMESPACE);

    public static Expression sequenceTag = new Variable("SEQUENCE", ASN1_NAMESPACE, universalTagInc).asExpression();

    public static Class DEREncoder = new Class("Encoder", DER_NAMESPACE, DEREncoderInc);
    public static Function DEREncode = new Function("encode", DER_NAMESPACE, DEREncodeInc);

    public static Function encodeValue() {
        final Function function = new Function("encodeValue", DER_NAMESPACE);
        function.setReturnType(new TypeUsage(DEREncoder));
        function.setSpecialisationFrom(DEREncodeInc);
        return function;
    }

    public static Statement addChild(final Expression encoder, final Expression child) {
        return new Function("addChild").asFunctionCall(encoder, false, DEREncode.asFunctionCall(child)).asStatement();
    }

    public static Class BERDecoder(final TypeUsage iterator) {
        final Class result = new Class("Decoder", BER_NAMESPACE, BERDecoderInc);
        result.addTemplateSpecialisation(iterator);
        return result;
    }

    public static Function BERDecode = new Function("decode", BER_NAMESPACE, BERDeodeInc);

    public static Variable getChildIterator(final Expression decoder, final TypeUsage iterator) {
        return new Variable(new TypeUsage(BERDecoder(iterator).referenceNestedType("ChildIterator")),
                            "childIt",
                            new Function("getChildrenBegin").asFunctionCall(decoder, false));
    }

    public static Statement checkChildPresent(final Expression decoder, final Expression iterator) {
        return new Function("checkChildPresent").asFunctionCall(decoder, false, iterator).asStatement();
    }

    public static Statement checkNoMoreChildren(final Expression decoder, final Expression iterator) {
        return new Function("checkNoMoreChildren").asFunctionCall(decoder, false, iterator).asStatement();
    }

    public static class DecodeValue {

        public DecodeValue() {
            function = new Function("decodeValue", BER_NAMESPACE);
            function.setSpecialisationFrom(BERDeodeInc);
            final TemplateType iterator = new TemplateType("I");
            function.addTemplateParameter(new TypenameTemplateParameter(iterator));
            decoder =
                    function.createParameter(new TypeUsage(BERDecoder(new TypeUsage(iterator)),
                                                           TypeUsage.ConstReference), "decoder");
            childIt = getChildIterator(decoder.asExpression(), new TypeUsage(iterator));
        }

        Function function;
        Variable decoder;
        Variable childIt;
    }

    public static Statement checkHeader(final Expression decoder, final Expression tag, final boolean constructed) {
        return new Function("checkHeader").asFunctionCall(decoder,
                                                          false,
                                                          tag,
                                                          constructed ? Literal.TRUE : Literal.FALSE).asStatement();
    }

    public static Statement getChild(final Expression childIt, final Expression value) {
        final Expression
                childDecoder =
                new UnaryExpression(UnaryOperator.DEREFERENCE,
                                    new UnaryExpression(UnaryOperator.POSTINCREMENT, childIt));
        return BERDecode.asFunctionCall(childDecoder, value).asStatement();
    }
}
