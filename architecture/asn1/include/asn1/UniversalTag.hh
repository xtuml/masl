/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#ifndef ASN1_UniversalTag_HH
#define ASN1_UniversalTag_HH

namespace ASN1 {

    enum UniversalTag {
        BOOLEAN = 1,
        INTEGER = 2,
        BIT_STRING = 3,
        OCTET_STRING = 4,
        ASN_NULL = 5,
        OBJECT_IDENTIFIER = 6,
        ObjectDescriptor = 7,
        INSTANCE_OF = 8,
        REAL = 9,
        ENUMERATED = 10,
        EMBEDDED_PDV = 11,
        UTF8String = 12,
        RELATIVE_OID = 13,
        SEQUENCE = 16,
        SET = 17,
        NumericString = 18,
        PrintableString = 19,
        TeletexString = 20,
        VideotexString = 21,
        IA5String = 22,
        UTCTime = 23,
        GeneralizedTime = 24,
        GraphicString = 25,
        VisibleString = 26,
        GeneralString = 27,
        UniversalString = 28,
        CHARACTER_STRING = 29,
        BMPString = 30

    };

}

#endif
