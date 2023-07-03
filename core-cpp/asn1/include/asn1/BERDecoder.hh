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
#ifndef ASN1_BER_Decoder_HH
#define ASN1_BER_Decoder_HH

#include <stdint.h>
#include <string>
#include <vector>
#include <iostream>
#include <stdexcept>
#include <boost/lexical_cast.hpp>
#include <boost/operators.hpp>

#include "UniversalTag.hh"

namespace ASN1
{
  namespace BER
  {
    typedef uint8_t Octet;

    template<class ForwardIterator>
    class Decoder
    {
      public: 
        enum TagClass { UNIVERSAL   = 0x00,
                        APPLICATION = 0x40,
                        CONTEXT     = 0x80,
                        PRIVATE     = 0xC0 };

        class ChildIterator : public boost::forward_iterator_helper<ChildIterator,Decoder<ForwardIterator>,std::ptrdiff_t,Decoder<ForwardIterator>&,Decoder<ForwardIterator>*>
        {
          public:
            ChildIterator ( ForwardIterator curPos, ForwardIterator stopPos )
             : curPos(curPos),
               stopPos(stopPos)
            {
              if ( curPos != stopPos )
              {
                curChild = Decoder(curPos);
                if ( curChild.isEndOfContent() )
                {
                  // end tag for indeterminate length
                  curPos = curChild.end();
                }
              }
            }

            ChildIterator ( ForwardIterator stopPos )
             : curPos(stopPos),
               stopPos(stopPos) {}

            const Decoder<ForwardIterator>& operator*() const { return curChild; }

            ChildIterator& operator++()
            {
              curPos = curChild.end();
              if ( curPos != stopPos )
              {
                curChild = Decoder(curPos);
                if ( curChild.isEndOfContent() )
                {
                  // end tag for indeterminate length, so skip to real end
                  curPos = curChild.end();
                }
              }
              return *this;
            }
             
            bool operator==( const ChildIterator& rhs ) const
            {
              return curPos == rhs.curPos;
            }

          private:
            ForwardIterator curPos;
            ForwardIterator stopPos;
            Decoder curChild;
        };  

      public: 
        Decoder ();
        Decoder ( ForwardIterator start );

        void checkHeader( UniversalTag tag, bool constructed = false ) const;
        void checkChildPresent( const ChildIterator& childIt ) const;
        void checkNoMoreChildren( const ChildIterator& childIt ) const;

        uint64_t getTag()        const { return tag; }
        TagClass getTagClass()   const { return static_cast<TagClass>(*beginPos & tagClassMask); }
        bool     isConstructed() const { return *beginPos & constructedMask; }

        bool isEndOfContent() const { return getTag() == 0 && getTagClass() == UNIVERSAL && !isConstructed(); }

        ForwardIterator getValueBegin() const { return valuePos; }
        ForwardIterator getValueEnd() const { return endPos; }

        ChildIterator getChildrenBegin() const { return ChildIterator(valuePos,endPos); }
        ChildIterator getChildrenEnd() const { return ChildIterator(endPos); }

        size_t size() const { return bufSize; }

        ForwardIterator begin() const { return beginPos; }
        ForwardIterator end() const { return endPos; }

        typedef ForwardIterator iterator;
        typedef iterator        const_iterator;

      private:
        ForwardIterator beginPos;
        ForwardIterator valuePos;
        ForwardIterator endPos;      

        uint64_t tag;

        size_t bufSize;

      private:
        static const Octet tagClassMask    = 0xC0;
        static const Octet constructedMask = 0x20;
        static const Octet shortTagMask    = 0x1F;
        static const Octet longTagMask     = 0x7F;
        static const Octet moreTagMask     = 0x80;

        static const Octet longLengthMask  = 0x80;
        static const Octet shortLengthMask = 0x7F;
    };

    template<class ForwardIterator>
    Decoder<ForwardIterator>::Decoder ()
      : beginPos(),
        valuePos(),
        endPos(),
        tag(),
        bufSize()
    {
    }

    template<class ForwardIterator>
    Decoder<ForwardIterator>::Decoder ( ForwardIterator begin )
      : beginPos(begin),
        valuePos(begin),
        endPos(begin),
        tag(),
        bufSize()
    {

      // Extract tag
      ForwardIterator curPos = begin;
      Octet tagBits = static_cast<Octet>(*curPos++) & shortTagMask;
      ++bufSize;

      if ( tagBits != shortTagMask )
      {
        tag = tagBits;
      }
      else
      {
        uint64_t tag = 0;
        do 
        {
          tag <<= 7;
          tag |= static_cast<Octet>(*curPos) & longTagMask;
        }
        while ( ++bufSize, static_cast<Octet>(*curPos++) & moreTagMask );
      }

      // Extract length
      uint64_t length = 0;
      bool indefiniteLength = false;

      if ( static_cast<Octet>(*curPos) & longLengthMask )
      {
        Octet lengthOctets = static_cast<Octet>(*curPos++) & ~longLengthMask; 
        ++bufSize;

        // Check for indefinite length form
        indefiniteLength = !lengthOctets;

        while ( lengthOctets-- )
        {
          length <<= 8;
          length |= static_cast<Octet>(*curPos++);
          ++bufSize;
        }
      }
      else
      {
        length = static_cast<Octet>(*curPos++) & shortLengthMask;
        ++bufSize;
      }

      // Set value iterators and children
      valuePos = curPos;

      if ( indefiniteLength )
      {
        bool endOfContent = false;
        while(!endOfContent)
        {
          Decoder child(curPos);
          curPos = child.end();
          bufSize += child.size();
          endOfContent = child.isEndOfContent();
        }
        endPos = curPos;
      }
      else
      {
        endPos = curPos + length;
        bufSize+= length;
      }
    }

    template<class ForwardIterator>
    void Decoder<ForwardIterator>::checkChildPresent ( const ChildIterator& childIt ) const
    {
      if ( childIt == ChildIterator(endPos) )
      {
        throw std::runtime_error("ASN1 BER Decode Error: Expected child");
      }
    }

    template<class ForwardIterator>
    void Decoder<ForwardIterator>::checkNoMoreChildren ( const ChildIterator& childIt ) const
    {
      if ( childIt != ChildIterator(endPos) )
      {
        throw std::runtime_error("ASN1 BER Decode Error: Found child when none expected");
      }
    }


    template<class ForwardIterator>
    void Decoder<ForwardIterator>::checkHeader ( UniversalTag tag, bool constructed ) const
    { 
      if ( getTagClass() != UNIVERSAL )
      {
        throw std::runtime_error("ASN1 BER Decode Error: Expected Universal Tag");
      }

      if ( getTag() != static_cast<uint64_t>(tag) )
      {
        throw std::runtime_error("ASN1 BER Decode Error: Expected tag " + boost::lexical_cast<std::string>(tag) + ", found " + boost::lexical_cast<std::string>(getTag()));
      }

      if ( isConstructed() != constructed )
      {
        if ( constructed ) throw std::runtime_error("ASN1 BER Decode Error: Expected constructed value");
        else throw std::runtime_error("ASN1 BER Decode Error: Expected primitive value");
      }
    }



  }
}


#endif
