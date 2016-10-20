//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "asn1/DEREncoder.hh"

namespace ASN1
{
  namespace DER
  {
    Encoder::Encoder ()
      : tag(),
        tagClass(UNIVERSAL),
        constructed(false),
        value(),
        children(),
        stale(true),
        length(),
        header()
    {
    }

    Encoder::Encoder ( UniversalTag tag )
      : tag(tag),
        tagClass(UNIVERSAL),
        constructed(tag==SEQUENCE||tag==SET||tag==INSTANCE_OF),
        value(),
        children(),
        stale(true),
        length(),
        header()
    {
    }

    Encoder::Encoder ( UniversalTag tag, const Buffer& value )
      : tag(tag),
        tagClass(UNIVERSAL),
        constructed(false),
        value(value),
        children(),
        stale(true),
        length(),
        header()
    {
    }

    Encoder::Encoder ( UniversalTag tag, const Children& children )
      : tag(tag),
        tagClass(UNIVERSAL),
        constructed(true),
        value(),
        children(children),
        stale(true),
        length(),
        header()
    {
    }

    void Encoder::setValue ( Buffer value )
    {
      this->value = value;
      this->constructed = false;
      this->children.clear();
      this->stale = true;
    }

    void Encoder::addChild(const Encoder& child)
    {
      this->value.clear();
      this->constructed = true;
      this->children.push_back(child);
      this->stale = true;
    }

    size_t Encoder::size() const
    {
      cacheBuffers();
      return header.size() + length;
    }

    void Encoder::cacheTag() const
    {
      static const Octet tagOverflow = 0x1F;

      if ( tag < tagOverflow )
      {
        // field [ class | contructed |      tag          ]
        // bit   [ 7 | 6 |      5     | 4 | 3 | 2 | 1 | 0 ]
        header.push_back( tagClass | (constructed?CONSTRUCTED:PRIMITIVE) | tag );
      }
      else 
      {
        // field [ class | contructed | 1 | 1 | 1 | 1 | 1 ] [ 1 | tag....  ] ... [ 1 | tag.... ] [ 0 | tag.... ]
        // bit   [ 7 | 6 |      5     | 4 | 3 | 2 | 1 | 0 ] 
        header.push_back( tagClass | (constructed?CONSTRUCTED:PRIMITIVE) | tagOverflow );
        bool started = false;

        // Split tag into 7-bit chunks, with top bit set in all but in last octet. 
        for ( int i = sizeof(tag)*8/7; i >= 0; --i )
        {
          Octet curOctet = ((tag >> i*7 ) & 0x7F);
          // Don't include leading all-zero chunks
          if ( started || curOctet )
          {
            header.push_back(curOctet| (i?0x80:0x00));
            started = true;
          }
        }
      }
    }

    void Encoder::cacheLength() const
    {
      length = value.size();
      for ( Children::const_iterator it = children.begin(), end = children.end(); it != end; ++it )
      {
        size_t childSize = it->size();
        length+= childSize;
      }

      static const Octet valueLimit = 0x7F;
      static const Octet overflowFlag = 0x80;

      if ( length <= valueLimit )
      {
        // Single octet contining the length
        header.push_back(length);
      }
      else
      {
        // First octect [ 1 | count of length octects   ]
        //              [ 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 ]
        size_t firstOctetPos = header.size(); 
        Octet firstOctet = overflowFlag;
        header.push_back(firstOctet);
        for ( int i = sizeof(length)-1; i >= 0; --i )
        {
          Octet curOctet = ((length >> i*8 ) & 0xFF);
          // Don't include leading all-zero octets
          if (  curOctet || (firstOctet != overflowFlag) )
          {
            // add next length octet
            header.push_back(curOctet);
            //increase octet count
            ++firstOctet;
          }
        }
        header[firstOctetPos] = firstOctet;
      }
    }

    void Encoder::cacheBuffers() const
    {
      if ( stale )
      {
        header.clear();
        header.reserve(4); // Should be enough for most common cases.
        cacheTag();
        cacheLength();
        stale = false;
      }
    }

    Encoder::iterator Encoder::begin() const
    {
      cacheBuffers();
      return iterator(this);
    }

    const Encoder::iterator& Encoder::end() const
    {
      static const iterator endIt;
      return endIt;
    }

    Encoder::iterator::iterator ()
    {
    }

    Encoder::iterator::iterator ( const Encoder* encoder )
    {
      stack.push(encoder);
    }
         
  }
}
