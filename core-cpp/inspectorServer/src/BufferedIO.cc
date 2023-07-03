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

#include "inspector/BufferedIO.hh"
#include <errno.h>
#include <unistd.h>
#include <iostream>
#include <byteswap.h>

#include "ConnectionError.hh"
#include "swa/String.hh"
#include "swa/Timestamp.hh"
#include "swa/Duration.hh"

namespace Inspector
{

  void BufferedOutputStream::flush()
  {
    size_t totalSent = 0;
    while ( totalSent < buffer.size() )
    {
      int sent = ::write(fp,&buffer[totalSent],buffer.size()-totalSent);
      if ( sent > 0 ) totalSent+= sent;
      else
      { 
        throw ConnectionError(std::string("Error Sending: ") + strerror(errno)); 
      }
    }
    buffer.clear();
  }

  template<class T>
  void BufferedOutputStream::writeRaw ( const T& bytes )
  {
    // If the write would take things over the flush limit, then flush before writing.
    if ( buffer.size()+sizeof(T) >= FLUSH_LIMIT ) this->flush();

    // Push the bytes onto the buffer
    unsigned const char* src = reinterpret_cast<unsigned const char*>(&bytes);
    copy(src,src+sizeof(T),back_inserter(buffer));
  } 

  template<>
  void BufferedOutputStream::write<std::string> ( const std::string& val )
  {
    write(static_cast<int>(val.size()));
    writeRange(val.begin(), val.end());
  }

  template<>
  void BufferedOutputStream::write<SWA::String> ( const SWA::String& val )
  {
    write(static_cast<int>(val.size()));
    writeRange(val.begin(), val.end());
  }

  void BufferedOutputStream::write ( const SWA::ObjectPtr<void>& val )
  {
    bool valid = val;
    write(valid);
  }

  void BufferedOutputStream::write ( const char* val )
  {
    write(std::string(val));
  }

  template<>
  void BufferedOutputStream::write<SWA::Timestamp> ( const SWA::Timestamp& val )
  {
    write(val.nanosSinceEpoch());
  }

  template<>
  void BufferedOutputStream::write<SWA::Duration> ( const SWA::Duration& val )
  {
    write(val.nanos());
  }
  // Provide templated versions for fundamental types, just 
  // in case someone writes "write<double>(x)" or similar 
  template<> void BufferedOutputStream::write<uint64_t     > ( const uint64_t     & val ) { write(val); }
  template<> void BufferedOutputStream::write<int64_t      > ( const int64_t      & val ) { write(val); }
  template<> void BufferedOutputStream::write<uint32_t     > ( const uint32_t     & val ) { write(val); }
  template<> void BufferedOutputStream::write<int32_t      > ( const int32_t      & val ) { write(val); }
  template<> void BufferedOutputStream::write<double       > ( const double       & val ) { write(val); }
  template<> void BufferedOutputStream::write<char         > ( const char         & val ) { write(val); }
  template<> void BufferedOutputStream::write<unsigned char> ( const unsigned char& val ) { write(val); }
  template<> void BufferedOutputStream::write<signed char  > ( const signed char  & val ) { write(val); }
  template<> void BufferedOutputStream::write<bool         > ( const bool         & val ) { write(val); }

  void BufferedOutputStream::write ( uint64_t val )
  {
    writeRaw(bswap_64(val));
  }

  void BufferedOutputStream::write ( int64_t val )
  {
    writeRaw(bswap_64(val));
  }

  void BufferedOutputStream::write ( uint32_t val )
  {
    writeRaw(bswap_32(val));
  }

  void BufferedOutputStream::write ( int32_t val )
  {
    writeRaw(bswap_32(val));
  }

  namespace 
  {
    union doublecast
    {
      double dval;
      uint64_t ival;
    };
  }


  void BufferedOutputStream::write ( double val )
  {
    doublecast caster;
    caster.dval = val;
    writeRaw(bswap_64(caster.ival));
  }

  void BufferedOutputStream::write ( char val )
  {
    writeRaw(val);
  }

  void BufferedOutputStream::write ( unsigned char val )
  {
    writeRaw(val);
  }

  void BufferedOutputStream::write ( signed char val )
  {
    writeRaw(val);
  }

  void BufferedOutputStream::write ( bool val )
  {
    writeRaw(val);
  }


  void InputBuffer::updateBuffer()
  {
    endPos = buffer + ::read(fp,buffer,BUFFER_SIZE);
    curPos = buffer;
    if ( curPos == endPos ) { throw ConnectionError("Socket gone"); }
    if ( curPos > endPos ) {
      if ( errno == EINTR ) {
        // Interrupted, try again
        updateBuffer();
      }
      else {
        throw ConnectionError(strerror(errno));
      }
    }
  }

  bool InputBuffer::available()
  {
    if ( curPos != endPos ) return true;
    else  
    {
      fd_set readset;
      FD_ZERO(&readset);
      FD_SET(fp,&readset);
      struct timeval timeout;
      timeout.tv_sec = 0;
      timeout.tv_usec = 0;
      return select(fp+1,&readset,0,0,&timeout) && FD_ISSET(fp,&readset);
    }
  }

  // Copy function to copy n bytes from in to out
  template<class InputIterator, class OutputIterator>
  void copy_buffer ( InputIterator in, int length, OutputIterator out )
  {
    while ( length-- )
      *out++ = *in++;
  }


  template<class T>
  void BufferedInputStream::readRaw ( T& bytes )
  {
    unsigned char* dest = reinterpret_cast<unsigned char*>(&bytes);
    copy_buffer(InputBufferIterator(buffer),sizeof(T),dest);
  }

  void BufferedInputStream::read ( uint64_t& val )
  {
    readRaw(val);
    val = bswap_64(val);
  }

  void BufferedInputStream::read ( int64_t& val )
  {
    readRaw(val);
    val = bswap_64(val);
  }

  void BufferedInputStream::read ( uint32_t& val )
  {
    readRaw(val);
    val = bswap_32(val);
  }

  void BufferedInputStream::read ( int32_t& val )
  {
    readRaw(val);
    val = bswap_32(val);
  }

  void BufferedInputStream::read ( double& val )
  {
    doublecast caster;
    readRaw(caster.ival);
    caster.ival = bswap_64(caster.ival);
    val = caster.dval;
  }

  void BufferedInputStream::read ( char& val )
  {
    readRaw(val);
  }

  void BufferedInputStream::read ( unsigned char& val )
  {
    readRaw(val);
  }

  void BufferedInputStream::read ( signed char& val )
  {
    readRaw(val);
  }

  void BufferedInputStream::read ( bool& val )
  {
    readRaw(val);
  }

  template<>
  void BufferedInputStream::read<std::string> ( std::string& val )
  {
    int size;
    read(size);
    val.clear();
    val.reserve(size);
    read_container(back_inserter(val),size);
  }

  template<>
  void BufferedInputStream::read<SWA::String> ( SWA::String& val )
  {
    std::string tmp;
    read(tmp);
    val = tmp;
  }

  template<>
  void BufferedInputStream::read<SWA::Timestamp> ( SWA::Timestamp& val )
  {
    int64_t tmp;
    read(tmp);
    val = SWA::Timestamp::fromNanosSinceEpoch(tmp);
  }

  template<>
  void BufferedInputStream::read<SWA::Duration> ( SWA::Duration& val )
  {
    int64_t tmp;
    read(tmp);
    val = SWA::Duration::fromNanos(tmp);
  }



}
