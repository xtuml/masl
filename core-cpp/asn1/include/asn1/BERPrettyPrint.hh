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
#ifndef ASN1_BER_PrettyPrint_HH
#define ASN1_BER_PrettyPrint_HH

#include "UniversalTag.hh"
#include "BERDecode.hh"
#include "BERDecoder.hh"
#include <iostream>

namespace ASN1
{
  namespace BER
  {
    inline std::string getUniversalTagName ( UniversalTag tag )
    {
      switch (tag)
      {
        case BOOLEAN          : return "BOOLEAN";
        case INTEGER          : return "INTEGER";
        case BIT_STRING       : return "BIT_STRING";
        case OCTET_STRING     : return "OCTET_STRING";
        case ASN_NULL         : return "NULL";
        case OBJECT_IDENTIFIER: return "OBJECT IDENTIFIER";
        case ObjectDescriptor : return "ObjectDescriptor";
        case INSTANCE_OF      : return "INSTANCE OF";
        case REAL             : return "REAL";
        case ENUMERATED       : return "ENUMERATED";
        case EMBEDDED_PDV     : return "EMBEDDED PDV";
        case UTF8String       : return "UTF8String";
        case RELATIVE_OID     : return "RELATIVE-OID";
        case SEQUENCE         : return "SEQUENCE";
        case SET              : return "SET";
        case NumericString    : return "NumericString";
        case PrintableString  : return "PrintableString";
        case TeletexString    : return "TeletexString";
        case VideotexString   : return "VideotexString";
        case IA5String        : return "IA5String";
        case UTCTime          : return "UTCTime";
        case GeneralizedTime  : return "GeneralizedTime";
        case GraphicString    : return "GraphicString";
        case VisibleString    : return "VisibleString";
        case GeneralString    : return "GeneralString";
        case UniversalString  : return "UniversalString";
        case CHARACTER_STRING : return "CHARACTER STRING";
        case BMPString        : return "BMPString";
        default               : return "UNIVERSAL:" + boost::lexical_cast<std::string>(tag);
      }
    }


    template<class I>
    std::string getTagName ( const Decoder<I>& decoder )
    {
      switch ( decoder.getTagClass() )
      {
        case Decoder<I>::UNIVERSAL:
          return getUniversalTagName(static_cast<UniversalTag>(decoder.getTag()));
        case Decoder<I>::APPLICATION:
          return "APPLICATION:" + boost::lexical_cast<std::string>(decoder.getTag());
        case Decoder<I>::CONTEXT:
          return "CONTEXT:" + boost::lexical_cast<std::string>(decoder.getTag());
        case Decoder<I>::PRIVATE:
          return "PRIVATE:" + boost::lexical_cast<std::string>(decoder.getTag());
        default:
          return "UNKNOWN:" + boost::lexical_cast<std::string>(decoder.getTag());
      }
    }

    template<class C, class T, class It>
    std::basic_ostream<C,T>& hexPrint ( std::basic_ostream<C,T>& stream, It begin, It end, const std::string& indent )
    {
      if (begin != end )
      {
        int i = 0;
        for ( It it = begin; it != end; ++it )
        {
          if ( !(i%16) ) stream << indent; 
          stream << std::hex << std::setfill('0') << std::setw(2) << static_cast<unsigned int>(static_cast<Octet>(*it) & 0xff) << std::dec;
          if ( !(++i%16) || it == end -1 ) stream << "\n";
          else if ( !(i%4) ) stream << "   ";
          else stream << " ";
        }
      }
      return stream;
    }

    template<class C, class T, class I>
    std::basic_ostream<C,T>& prettyPrintChildren ( std::basic_ostream<C,T>& stream, const Decoder<I>& constructed, const std::string& indent )
    {
      for ( typename Decoder<I>::ChildIterator it = constructed.getChildrenBegin(), end = constructed.getChildrenEnd(); it != end; ++it )
      {
        prettyPrint ( stream, *it, indent );
      }
      return stream;
    }

    template<class C, class T, class I>
    std::basic_ostream<C,T>& prettyPrint ( std::basic_ostream<C,T>& stream, const Decoder<I>& decoder, const std::string& indent )
    {
      stream << indent << "[" << getTagName(decoder) << "]";

      if ( decoder.getTagClass() == Decoder<I>::UNIVERSAL )
      {
        switch ( decoder.getTag() )
        {
          case ASN_NULL         : return stream << "\n";
          case BOOLEAN          : return stream << " " << decodeBoolean(decoder.getValueBegin(),decoder.getValueEnd()) << "\n";
          case INTEGER          : return stream << " " << decodeInteger(decoder.getValueBegin(),decoder.getValueEnd(),int64_t()) << "\n";
          case ENUMERATED       : return stream << " " << decodeInteger(decoder.getValueBegin(),decoder.getValueEnd(),int64_t()) << "\n";
          case REAL             : return stream << " " << decodeReal(decoder.getValueBegin(),decoder.getValueEnd(),double()) << "\n";
          case BIT_STRING       : return stream << " " << decodeBitString(decoder.getValueBegin(),decoder.getValueEnd()) << "\n";
          case OBJECT_IDENTIFIER : return stream << " " << decodeOid(decoder.getValueBegin(),decoder.getValueEnd()) << "\n";

          case UTF8String       :
          case NumericString    : 
          case PrintableString  : 
          case TeletexString    : 
          case VideotexString   : 
          case IA5String        : 
          case UTCTime          : 
          case GeneralizedTime  : 
          case GraphicString    : 
          case VisibleString    : 
          case GeneralString    : 
          case UniversalString  : 
          case BMPString        : 
          case OCTET_STRING     : return stream << " '" << std::string(decoder.getValueBegin(),decoder.getValueEnd()) << "'\n";

          case SEQUENCE         :
          case SET              :
          default:
            if ( decoder.isConstructed() )
            {
              return prettyPrintChildren(stream << "\n",decoder,indent + "\t");
            }
            else
            {
              return hexPrint(stream << "\n",decoder.getValueBegin(),decoder.getValueEnd(),indent + "\t");
            }
        }
      }
      else
      {
        if ( decoder.isConstructed() )
        {
          return prettyPrintChildren(stream << "\n",decoder,indent + "\t");
        }
        else
        {
          return hexPrint(stream << "\n",decoder.getValueBegin(),decoder.getValueEnd(),indent + "\t");
        }
      }   
    }
  }
}

namespace std
{
  template<class C, class T, class I>
  basic_ostream<C,T>& operator<< ( basic_ostream<C,T>& stream, const ASN1::BER::Decoder<I>& decoder )
  {
    return ASN1::BER::prettyPrint(stream,decoder,"");
  }
}

#endif
