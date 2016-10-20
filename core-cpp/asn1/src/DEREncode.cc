//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "asn1/DEREncode.hh"

namespace ASN1
{
  namespace DER
  {
    namespace
    {
      template<class I>
      Buffer encode2sComplementInt ( I value )
      {
        Buffer valBuf;
        valBuf.reserve(sizeof(I));
        int octetNo = sizeof(value)-1;

        Octet curOctet = std::numeric_limits<I>::is_signed ? ((value >> (octetNo--<<3)) & 0xFF) : 0x00;

        while ( octetNo >= 0 )
        {
          Octet nextOctet = (value >> (octetNo--<<3)) & 0xFF;
          if ( !valBuf.empty() || 
               !( ( curOctet == 0x00 && (nextOctet & 0x80) == 0x00 ) ||
                  ( curOctet == 0xFF && (nextOctet & 0x80) == 0x80 ) ) )
          {
            valBuf.push_back(curOctet);
          }
          curOctet = nextOctet;
        }
        valBuf.push_back(curOctet);
        return valBuf;
      }

      template<class I>
      Buffer encodePositiveInt ( I value )
      {
        Buffer valBuf;
        valBuf.reserve( (value&(1ULL<<((sizeof(I)<<3)-1))) ? sizeof(I)+1 : sizeof(I) );

        for ( int octetNo = sizeof(value)-1; octetNo >= 0; --octetNo )
        {
          Octet curOctet = (value >> (octetNo<<3)) & 0xFF;
          if ( !valBuf.empty() || curOctet ) 
          {
            valBuf.push_back(curOctet);
          }
        }
        return valBuf;
      }


      Buffer encodeDouble ( double value )
      {
        // Assume IEEE 754-2008 binary64 encoding... 1 bit sign, 11 bit exponent, 52 bit fraction (53 bit normalized mantissa)

        static const uint64_t signMask         = 0x8000000000000000LL;

        static const uint64_t exponentMask     = 0x7FF0000000000000LL;
        static const int exponentBias          = 0x3FF;
        static const int specialExponent       = 0x7FF;

        static const int fractionSize          = 52;
        static const uint64_t fractionMask     = 0x000FFFFFFFFFFFFFLL;
        static const uint64_t normalizedTopBit = 0x0010000000000000LL;

        static const Buffer DER_posInfinity(1,0x40); 
        static const Buffer DER_negInfinity(1,0x41); 
        static const Buffer DER_NaN(1,0x42); 
        static const Buffer DER_negZero(1,0x43); 
        static const Buffer DER_posZero; 

        // Decode the real into bits. Use a union 
        // rather than a char buffer to avoid problems with byte 
        // ordering, as ordering of int and double will be the 
        // same 
        union
        {
           uint64_t bits;
           double value;
        } converter;

        converter.value = value;
        uint64_t bits = converter.bits;

        // Split out the fields
        bool      negative     = bits & signMask;
        uint16_t  exponentBits = (bits & exponentMask) >> fractionSize;
        uint64_t  fractionBits = bits & fractionMask;

        // Cope with NaN and infinities
        if ( exponentBits == specialExponent )
        {
          if ( fractionBits ) return DER_NaN;
          else                return negative ? DER_negInfinity : DER_posInfinity;
        }
        else if ( !exponentBits && !fractionBits )
        {
          return negative ? DER_negZero : DER_posZero;
        }

        bool isNormalized = exponentBits;

        int16_t  exponent = exponentBits - exponentBias - fractionSize + (isNormalized?0:1);
        uint64_t mantissa = fractionBits | (isNormalized?normalizedTopBit:0);

        // force mantissa odd for DER encoding
        if ( mantissa )
        {
          if ( ! (mantissa & 0xFFFFFFFF) )         { mantissa >>= 32; exponent+= 32; }
          if ( ! (mantissa & 0xFFFF) )             { mantissa >>= 16; exponent+= 16; }
          if ( ! (mantissa & 0xFF) )               { mantissa >>= 8;  exponent+= 8; }
          if ( ! (mantissa & 0x0F) )               { mantissa >>= 4;  exponent+= 4; }
          if ( ! (mantissa & 0x03) )               { mantissa >>= 2;  exponent+= 2; }
          if ( ! (mantissa & 0x01) )               { mantissa >>= 1;  exponent+= 1; }
        }

        Buffer buffer;
        buffer.reserve(8);

        // Bit 7
        // Unused encoding for documentation purposes
        // static const Octet DecimalEncoding = 0x00;
        static const Octet BinaryEncoding  = 0x80;

        // Bit 6
        static const Octet SignPositive = 0x00;
        static const Octet SignNegative = 0x40;

        // Bits 5 & 4
        static const Octet Base2  = 0x00;
        // Unused bases for documentation purposes
        // static const Octet Base8  = 0x10;
        // static const Octet Base16 = 0x20;


        // Bits 3 & 2
        static const Octet Scale0 = 0x00;
        // Unused scales for documentation purposes
        // static const Octet Scale1 = 0x04;
        // static const Octet Scale2 = 0x08;
        // static const Octet Scale3 = 0x0C;

        // Bits 1 & 0
        static const Octet ExponentLength1 = 0x00;
        static const Octet ExponentLength2 = 0x01;
        static const Octet ExponentLength3 = 0x02;
        static const Octet ExponentLengthX = 0x03;

        Octet firstOctet = BinaryEncoding | Base2 | Scale0 | (negative?SignNegative:SignPositive);

        // Encode the exponent
        Buffer exponentBuffer = encode2sComplementInt(exponent);
        switch ( exponentBuffer.size() )
        {
          case 1: buffer.push_back(firstOctet | ExponentLength1); break;
          case 2: buffer.push_back(firstOctet | ExponentLength2); break;
          case 3: buffer.push_back(firstOctet | ExponentLength3); break;
          default: 
            buffer.push_back(firstOctet | ExponentLengthX);
            buffer.push_back(exponentBuffer.size()&0xFF);
        }
        buffer.append(exponentBuffer);

        // Encode the mantissa
        buffer.append(encodePositiveInt(mantissa));

        return buffer;
      }
    }

    Buffer encodeBoolean ( bool value )    { return Buffer(1,value?0xFF:0x00); }
    Buffer encodeReal    ( float value )   { return encodeDouble(value); }
    Buffer encodeReal    ( double value )  { return encodeDouble(value); }
      
    Buffer encodeInteger ( uint64_t value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( int64_t  value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( uint32_t value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( int32_t  value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( uint16_t value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( int16_t  value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( uint8_t  value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( int8_t   value )  { return encode2sComplementInt(value); }
    Buffer encodeInteger ( char     value )  { return encode2sComplementInt(value); }


    Buffer encodeBitString ( boost::dynamic_bitset<Octet> value )
    {
      // Shift value so that it fills MSBs of result, with zeros in redundant LSBs
      Octet offset = ( 8 - (value.size()%8)) % 8;
      value.resize(value.size()+offset);
      value <<= offset;

      Buffer result(value.num_blocks()+1,0x00);
      result[0] = offset;
      boost::to_block_range(value,result.rbegin());
      return result;
    }

  }
}
