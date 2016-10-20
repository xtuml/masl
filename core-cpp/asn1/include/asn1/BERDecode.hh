//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef ASN1_BER_Decode_HH
#define ASN1_BER_Decode_HH

#include "BERDecoder.hh"
#include "Encoding.hh"
#include "UniversalTag.hh"

#include <boost/dynamic_bitset.hpp>
#include <boost/tuple/tuple.hpp>
#include <bitset>
#include <iterator>

#include "swa/Duration.hh"
#include "swa/Timestamp.hh"
#include "swa/Device.hh"
#include "swa/ObjectPtr.hh"
#include "swa/remove_const.hh"


namespace ASN1
{
  namespace BER
  {
    // Helper functions
    namespace
    {
      template<class It> Octet octet(It it) { return static_cast<Octet>(*it); }

      template<class I,class It>
      I decode2sComplementInt ( It curPos, It end )
      {
        I result = (octet(curPos) & 0x80) && std::numeric_limits<I>::is_signed ?
                    -1 : 0;

        while ( curPos != end )
        {
          result <<= 8;
          result |= octet(curPos++);
        }
        return result;
      }


      template<class It>
      double decodeDouble ( It curPos, It end )
      {
        static const Octet specialMask        = 0x40;
        static const Octet binaryMask         = 0x80;
        static const Octet signMask           = 0x40;
        static const Octet baseMask           = 0x30;
        static const Octet scaleMask          = 0x0C;
        static const Octet exponentSizeMask   = 0x03;
      
        if ( curPos == end ) return 0.0;

        Octet header = octet(curPos++);
        if ( header & binaryMask )
        {
          bool negative = header & signMask;
          uint8_t baseF = ((header & baseMask) == 0x00 ) ? 1 : // base  2 = 2**1
                          ((header & baseMask) == 0x01 ) ? 3 : // base  8 = 2**3
                                                           4 ; // base 16 = 2**4
          uint8_t scale = (header & scaleMask) >> 2;
          uint8_t exponentLen = (header & exponentSizeMask) + 1;
          if ( exponentLen == 4 ) exponentLen = octet(curPos++);

          int32_t exponent = decode2sComplementInt<int32_t>(curPos,curPos+exponentLen);
          curPos+= exponentLen;

          double mantissa = 0.0;
          while ( curPos != end )
          {
            mantissa = ldexp(mantissa,8) + octet(curPos++);
          }

          // V = M * 2**scale * base ** exponent
          //   = M * 2**scale * 2**baseF ** exponent
          //   = M * 2**(scale + baseF*exponent)
          double result = ldexp ( mantissa , scale + baseF * exponent );

          return negative ? -result : result;
        }
        else if ( header & specialMask )
        {
          return header == 0x40 ? INFINITY :
                 header == 0x41 ? -INFINITY : 
                 header == 0x42 ? NAN :
                 header == 0x43 ? -0.0 :
                                  NAN;
        }
        else // NR decimal format
        {
          return strtod(std::string(curPos,end).c_str(),0);
        }

      } 

    }
    template<class It> bool     decodeBoolean ( It begin, It end ) { return *begin; }

    template<class It> uint64_t decodeInteger ( It begin, It end, uint64_t dummy ) { return decode2sComplementInt<uint64_t> ( begin,end ); }
    template<class It> int64_t  decodeInteger ( It begin, It end, int64_t  dummy ) { return decode2sComplementInt<int64_t > ( begin,end ); }
    template<class It> uint32_t decodeInteger ( It begin, It end, uint32_t dummy ) { return decode2sComplementInt<uint32_t> ( begin,end ); }
    template<class It> int32_t  decodeInteger ( It begin, It end, int32_t  dummy ) { return decode2sComplementInt<int32_t > ( begin,end ); }
    template<class It> uint16_t decodeInteger ( It begin, It end, uint16_t dummy ) { return decode2sComplementInt<uint16_t> ( begin,end ); }
    template<class It> int16_t  decodeInteger ( It begin, It end, int16_t  dummy ) { return decode2sComplementInt<int16_t > ( begin,end ); }
    template<class It> uint8_t  decodeInteger ( It begin, It end, uint8_t  dummy ) { return decode2sComplementInt<uint8_t > ( begin,end ); }
    template<class It> int8_t   decodeInteger ( It begin, It end, int8_t   dummy ) { return decode2sComplementInt<int8_t  > ( begin,end ); }
    template<class It> char     decodeInteger ( It begin, It end, char     dummy ) { return decode2sComplementInt<char    > ( begin,end ); }

    template<class It> double   decodeReal    ( It begin, It end, double   dummy ) { return decodeDouble( begin,end ); }
    template<class It> float    decodeReal    ( It begin, It end, float    dummy ) { return decodeDouble( begin,end ); }


    template<class It>
    std::string decodeOid ( It curPos, It end )
    {
      Octet n = Octet(*curPos++);
      int firstArc = (n<40)?0:((n<80)?1:2);
      int secondArc = (n<40)?n:((n<80)?n-40:n-80);

      std::string result = boost::lexical_cast<std::string>(firstArc) + "." + boost::lexical_cast<std::string>(secondArc);

      while ( curPos != end )
      {
        Octet n = Octet(*curPos++);
        int value = n & 0x7f;
        while ( n & 0x80 )
        {
          n = Octet(*curPos++);
          value <<= 7;
          value |= (n & 0x7f);
        }
        result += "." + boost::lexical_cast<std::string>(value);
      }
      return result;
    }


    template<class It>
    boost::dynamic_bitset<Octet> decodeBitString ( It curPos, It end )
    {
      Octet offset = octet(curPos++);
      std::vector<Octet> buffer(curPos,end);
      boost::dynamic_bitset<Octet> value(buffer.rbegin(),buffer.rend());
      value >>= offset;
      value.resize(value.size()-offset);
      return value;
    }

    // Decoders
    template<class I, class T>
    void decode ( const Decoder<I>& decoder, T& value )
    {
      decodeBasic(decoder, value, typename Encoding::Traits<T>::type() );
    }

    template<class I, class T>
    void decode ( const Decoder<I>& decoder )
    {
      decoder.checkHeader(ASN_NULL);
    }


    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::Unknown )
    {
      decodeValue( decoder, value );
    }


    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::Null )
    {
      decoder.checkHeader(ASN_NULL);
    }


    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::Boolean )
    {
      decoder.checkHeader(BOOLEAN);
      value = decodeBoolean(decoder.getValueBegin(),decoder.getValueEnd());
    }

    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::Integer )
    {
      decoder.checkHeader(INTEGER);
      value = decodeInteger(decoder.getValueBegin(),decoder.getValueEnd(),value);
    }

    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::Enumerated )
    {
      decoder.checkHeader(ENUMERATED);
      value = static_cast<T>(decodeInteger(decoder.getValueBegin(),decoder.getValueEnd(),value));
    }

    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::Real )
    {
      decoder.checkHeader(REAL);
      value = decodeReal(decoder.getValueBegin(),decoder.getValueEnd(),value);
    }


    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::BitString )
    {
      decoder.checkHeader(BIT_STRING);
      boost::dynamic_bitset<Octet> v = decodeBitString(decoder.getValueBegin(),decoder.getValueEnd());
      std::insert_iterator<T> ins(value,value.end());
      for ( size_t bit = 0; bit != v.size(); ++bit )
      {
        *ins++ = v[bit];
      }
    }
  
    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::OctetString )
    {
      decoder.checkHeader(OCTET_STRING);
      value = T(decoder.getValueBegin(),decoder.getValueEnd());
    }

    template<class MFT, MFT mf> struct mem_fun_test;

    template<class C,class I> void try_reserve ( C& container, const Decoder<I>& decoder, mem_fun_test<void(C::*)(typename C::size_type), &C::reserve>* = 0 )
    {
      container.reserve(std::distance(decoder.getChildrenBegin(),decoder.getChildrenEnd()));
    }

    template<class C, class D> void try_reserve ( C& container, const D& decoder ) {}

    template<class I, class T>
    void decodeBasic ( const Decoder<I>& decoder, T& value, Encoding::SequenceOf )
    {
      decoder.checkHeader(SEQUENCE,true);
      value.clear();

      try_reserve(value,decoder);

      std::insert_iterator<T> ins(value,value.end());  
      for ( typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin(), end = decoder.getChildrenEnd();
           childIt != end; ++childIt )
      {
        // Strip const off value type, as we are passing it by 
        // reference rather than assigning diectly to it 
        typename boost::remove_const<typename T::value_type>::type value;
        decode(*childIt,value);
        *ins++ = value;
      }
    }

    template<class I, class T>
    void decodeValue ( const Decoder<I>& decoder, T& value );

    template<class I>
    void decodeValue ( const Decoder<I>& decoder, boost::dynamic_bitset<Octet>& value )
    {
      decoder.checkHeader(BIT_STRING);
      value = decodeBitString(decoder.getValueBegin(),decoder.getValueEnd());
    }
  
    template<class I, size_t bits>
    void decodeValue ( const Decoder<I>& decoder, std::bitset<bits>& value )
    {
      decoder.checkHeader(BIT_STRING);
      boost::dynamic_bitset<Octet> v = decodeBitString(decoder.getValueBegin(),decoder.getValueEnd());
      for ( size_t bit = 0; bit != bits; ++bit )
      {
        value[bit] = v[bit];
      }
    }
  
    template<class I, class T1, class T2>
    void decodeValue ( const Decoder<I>& decoder, std::pair<T1,T2>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.first);
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.second);
      decoder.checkNoMoreChildren(childIt);
    }
      
    // Tuple decoders
    template<class I>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3, class T4>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3,T4>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<4>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3, class T4, class T5>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3,T4,T5>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<4>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<5>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3, class T4, class T5, class T6>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3,T4,T5,T6>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<4>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<5>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<6>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3, class T4, class T5, class T6, class T7>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3,T4,T5,T6,T7>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<4>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<5>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<6>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<7>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3, class T4, class T5, class T6, class T7, class T8>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3,T4,T5,T6,T7,T8>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<4>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<5>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<6>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<7>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<8>());
      decoder.checkNoMoreChildren(childIt);
    }

    template<class I, class T0, class T1, class T2, class T3, class T4, class T5, class T6, class T7, class T8, class T9>
    void decodeValue ( const Decoder<I>& decoder, boost::tuple<T0,T1,T2,T3,T4,T5,T6,T7,T8,T9>& value )
    {
      decoder.checkHeader(SEQUENCE,true);
      typename Decoder<I>::ChildIterator childIt = decoder.getChildrenBegin();
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<0>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<1>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<2>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<3>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<4>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<5>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<6>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<7>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<8>());
      decoder.checkChildPresent(childIt);
      decode(*childIt++, value.template get<9>());
      decoder.checkNoMoreChildren(childIt);
    }

    // Masl Types
    template<class I>
    void decodeValue ( const Decoder<I>& decoder, SWA::Duration& value )
    { 
      int64_t nanos;
      decode(decoder,nanos);
      value = SWA::Duration::fromNanos(nanos);
    }

    template<class I>
    void decodeValue ( const Decoder<I>& decoder, SWA::Timestamp& value )
    { 
      int64_t nanos;
      decode(decoder,nanos);
      value = SWA::Timestamp::fromNanosSinceEpoch(nanos);
    }

    template<class I>
    void decodeValue ( const Decoder<I>& decoder, SWA::Device& value )
    { 
      decode(decoder);
    }

    template<class I, class T>
    void decodeValue ( const Decoder<I>& decoder, SWA::ObjectPtr<T>& value )
    { 
      SWA::IdType id;
      decode(decoder,id);
      if ( id )
      {
        value = T::getInstance(id);
      }
      else
      {
        value = SWA::ObjectPtr<T>();
      }
    }

  }
}

#endif
