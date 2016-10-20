//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef ASN1_DER_Encode_HH
#define ASN1_DER_Encode_HH


#include <utility>
#include <stdint.h>

#include <iostream>
#include <boost/dynamic_bitset.hpp>
#include <boost/tuple/tuple.hpp>
#include <bitset>

#include "Encoding.hh"
#include "DEREncoder.hh"

#include "swa/String.hh"
#include "swa/Duration.hh"
#include "swa/Timestamp.hh"
#include "swa/Device.hh"
#include "swa/ObjectPtr.hh"

namespace ASN1
{
  namespace DER
  {
    template<class T>
    Encoder encode ( const T& value );

    inline Encoder encode ()
    {
      return Encoder(ASN_NULL);
    }

    // Helper functions
    Buffer encodeBoolean ( bool     value );
    
    Buffer encodeInteger ( uint64_t value );
    Buffer encodeInteger ( int64_t  value );  
    Buffer encodeInteger ( uint32_t value ); 
    Buffer encodeInteger ( int32_t  value ); 
    Buffer encodeInteger ( uint16_t value ); 
    Buffer encodeInteger ( int16_t  value );  
    Buffer encodeInteger ( uint8_t  value );  
    Buffer encodeInteger ( int8_t   value );  
    Buffer encodeInteger ( char     value );     

    Buffer encodeReal    ( double   value );
    Buffer encodeReal    ( float    value );

    Buffer encodeBitString ( boost::dynamic_bitset<Octet> value );

    // Encoders for types covered by Encoding traits
    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::Unknown )
    {
      return encodeValue(value);
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::Null )
    {
      return Encoder(ASN_NULL);
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::Boolean )
    {
      return Encoder(BOOLEAN,encodeBoolean(value));
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::Integer )
    {
      return Encoder(INTEGER,encodeInteger(value));
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::Enumerated )
    {
      return Encoder(ENUMERATED,encodeInteger(value));
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::Real )
    {
      return Encoder(REAL,encodeReal(value));
    }
  
    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::BitString )
    {
      boost::dynamic_bitset<Octet> v;
      for ( typename T::const_iterator it = value.begin(), end = value.end(); it != end; ++it )
      {
        v.push_back(*it);
      }
      return encode(v);
    }
  
    inline Encoder encodeBasic ( const Buffer& value, Encoding::OctetString )
    {
      return Encoder(OCTET_STRING,value);
    }

    inline Encoder encodeBasic ( const SWA::String& value, Encoding::OctetString )
    {
      return encodeBasic(value.s_str(),Encoding::OctetString());
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::OctetString )
    {
      return Encoder(OCTET_STRING,Buffer(value.begin(),value.end()));
    }
  
    template<class T>
    Encoder encodeBasic ( const T* value, Encoding::OctetString )
    {
      return encode(std::basic_string<T>(value));
    }

    template<class T>
    Encoder encodeBasic ( const T& value, Encoding::SequenceOf )
    {
      Encoder result(SEQUENCE);
      result.reserve(value.size());
      for ( typename T::const_iterator it = value.begin(), end = value.end(); it != end; ++it )
      {
        result.addChild(encode(*it));
      }
      return result;
    }

    // Specialised encoders for other type not determinable by Encoding traits
    // Must be fully specialised for any T not covered by the known Encodings
    template<class T>
    Encoder encodeValue ( const T& value );


    template<class T>
    Encoder encode ( const T& value )
    {
      return encodeBasic(value, typename Encoding::Traits<T>::type() );
    }



    template<>
    inline Encoder encodeValue ( const boost::dynamic_bitset<Octet>& value )
    {
      return Encoder(BIT_STRING,encodeBitString(value));
    }
  
    template<size_t bits>
    Encoder encodeValue ( const std::bitset<bits>& value )
    {
      boost::dynamic_bitset<Octet> v;
      for ( size_t bit = 0; bit != bits; ++bit )
      {
        v.push_back(value[bit]);
      }
      return encode(v);
    }
  

    template<class T1, class T2>
    Encoder encodeValue ( const std::pair<T1,T2>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(2);
      result.addChild(encode(value.first));
      result.addChild(encode(value.second));
      return result;
    }
      
    // Boost tuples

    template<>
    inline Encoder encodeValue ( const boost::tuple<>& value )
    {
      Encoder result(SEQUENCE);
      return result;
    }

    template<class T0>
    Encoder encodeValue ( const boost::tuple<T0>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(1);
      result.addChild(encode(value.template get<0>()));
      return result;
    }

    template<class T0, class T1>
    Encoder encodeValue ( const boost::tuple<T0,T1>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(2);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      return result;
    }

    template<class T0, class T1, class T2>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(3);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(4);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3, class T4>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3,T4>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(5);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      result.addChild(encode(value.template get<4>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3, class T4, class T5>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3,T4,T5>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(6);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      result.addChild(encode(value.template get<4>()));
      result.addChild(encode(value.template get<5>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3, class T4, class T5, class T6>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3,T4,T5,T6>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(7);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      result.addChild(encode(value.template get<4>()));
      result.addChild(encode(value.template get<5>()));
      result.addChild(encode(value.template get<6>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3, class T4, class T5, class T6, class T7>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3,T4,T5,T6,T7>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(8);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      result.addChild(encode(value.template get<4>()));
      result.addChild(encode(value.template get<5>()));
      result.addChild(encode(value.template get<6>()));
      result.addChild(encode(value.template get<7>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3, class T4, class T5, class T6, class T7, class T8>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3,T4,T5,T6,T7,T8>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(9);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      result.addChild(encode(value.template get<4>()));
      result.addChild(encode(value.template get<5>()));
      result.addChild(encode(value.template get<6>()));
      result.addChild(encode(value.template get<7>()));
      result.addChild(encode(value.template get<8>()));
      return result;
    }

    template<class T0, class T1, class T2, class T3, class T4, class T5, class T6, class T7, class T8, class T9>
    Encoder encodeValue ( const boost::tuple<T0,T1,T2,T3,T4,T5,T6,T7,T8,T9>& value )
    {
      Encoder result(SEQUENCE);
      result.reserve(10);
      result.addChild(encode(value.template get<0>()));
      result.addChild(encode(value.template get<1>()));
      result.addChild(encode(value.template get<2>()));
      result.addChild(encode(value.template get<3>()));
      result.addChild(encode(value.template get<4>()));
      result.addChild(encode(value.template get<5>()));
      result.addChild(encode(value.template get<6>()));
      result.addChild(encode(value.template get<7>()));
      result.addChild(encode(value.template get<8>()));
      result.addChild(encode(value.template get<9>()));
      return result;
    }

    inline void f ()
    {
      encodeValue(std::pair<int,int>(1,2));
    }
    // Masl Types
    template<>
    inline Encoder encodeValue ( const SWA::Duration& value )  { return encode(value.nanos()); }

    template<>
    inline Encoder encodeValue ( const SWA::Timestamp& value ) { return encode(value.nanosSinceEpoch()); }

    template<>
    inline Encoder encodeValue ( const SWA::Device& value )    { return encode(); }

    template<class T>
    Encoder encodeValue ( const SWA::ObjectPtr<T>& value )
    { 
      if ( value.isNull() || value.isDeleted() )
      {
        return encode(::SWA::IdType());
      }
      else
      {
        return encode(value->getArchitectureId());
      }
    }



  }
};     

#endif
