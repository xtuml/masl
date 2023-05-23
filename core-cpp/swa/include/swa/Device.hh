// 
// Filename : device.hh
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
#ifndef SWA_Device_HH
#define SWA_Device_HH

#include <iostream>
#include <errno.h>

#include "IOError.hh"
#include "String.hh"

#include "boost/type_traits/is_fundamental.hpp"
#include "boost/shared_ptr.hpp"

namespace SWA
{

template <class C, class T>
class basic_Device;

typedef basic_Device<char,::std::char_traits<char> > Device;

template <class C, class T=::std::char_traits<C> >
class basic_Device
{
  public:
    // Type definitions for the input and output stream types.
    typedef ::std::basic_istream<C,T>  InputStream;
    typedef ::std::basic_ostream<C,T>  OutputStream;
    typedef ::std::basic_iostream<C,T> InOutStream;

    basic_Device ();

    basic_Device ( const boost::shared_ptr<InputStream>& input_stream);

    basic_Device ( const boost::shared_ptr<OutputStream>& output_stream);

    basic_Device ( const boost::shared_ptr<InputStream>&  input_stream, 
                   const boost::shared_ptr<OutputStream>& output_stream);

    ~basic_Device() { if ( output_stream ) { output_stream->flush(); } }

    void setInputStream  ( const boost::shared_ptr<InputStream>&  stream);
    void setOutputStream ( const boost::shared_ptr<OutputStream>& stream);
    void setInOutStream  ( const boost::shared_ptr<InOutStream>&  stream) { setInputStream(stream); setOutputStream(stream); }

    void setRecordDelimiter (char delimiter = '\n') {record_delimiter = delimiter;}

    // Functions to return references to the currently active streams.
    template <class StreamType>
    boost::shared_ptr<StreamType> getInputStream (StreamType* dummy = 0) const { return boost::dynamic_pointer_cast<StreamType>(input_stream); }

    template <class StreamType>
    boost::shared_ptr<StreamType> getOutputStream (StreamType* dummy = 0) const { return boost::dynamic_pointer_cast<StreamType>(output_stream); }

    // Functions to return references to the currently active streams.
    const boost::shared_ptr<InputStream>&  getInputStream () const { return input_stream; }
    const boost::shared_ptr<OutputStream>&  getOutputStream () const { return output_stream; }

    void clearInputStream  ()  { input_stream.reset(); }
    void clearOutputStream ()  { if (output_stream) { output_stream->flush(); } output_stream.reset(); }

    // Stream operators for data and manipulators.
    template<class D>
    const basic_Device& operator << (const D& data) const;

    template<class D>
    const basic_Device& operator >> (D& data) const;

    template<class D>
    const basic_Device& operator < (const D& data) const;

    template<class D>
    const basic_Device& operator > (D& data) const;

    const basic_Device& operator << ( const basic_Device& data ) const;
    const basic_Device& operator >> ( basic_Device& data ) const;

    const basic_Device& operator << (OutputStream& (*op)(OutputStream&)) const;
    const basic_Device& operator >> (InputStream& (*op)(InputStream&)) const;

    // Reads up to the next newline character and puts the 
    // result into data. The newline is consumed, but is not 
    // included in the result data. 
    const basic_Device<C,T>& getline (std::string& data) const;
    const basic_Device<C,T>& getline (SWA::String& data) const;

    // EOF check - returns true if the next read of the input stream will fail because it is at end of file.
    bool atEOF () const;
    void checkRead () const;
    void checkWrite () const;

    bool inputValid() const  { return bool(input_stream); }
    bool outputValid() const { return bool(output_stream); }


    void write_raw ( const char* data, size_t size ) const;
    void write_raw ( const std::string& buffer ) const;
    void write_raw ( char value ) const;

    template<class V>
    void write_raw ( const std::vector<V>& buffer ) const;

    template<class V>
    void write_raw ( V buffer ) const;


    std::vector<char> read_raw_bytes (int32_t length ) const;
    std::string read_raw_string (int32_t length ) const;
    char read_raw_char() const;

    template<class V>
    std::vector<V> read_raw ( size_t length ) const;

    template<class V>
    V read_raw () const;


  private:
    boost::shared_ptr<InputStream>   input_stream;
    boost::shared_ptr<OutputStream>  output_stream;


    // Defines the record delimiter used in getline
    char record_delimiter;

    // Internal utility functions to return the stream currently in use.
    InputStream&  In() const
    {
      if (!input_stream) throw IOError("Input Device Invalid");

      return *input_stream;
    }

    OutputStream& Out() const
    {
      if (!output_stream) throw IOError("Output Device Invalid");

      return *output_stream;
    }

    template<class V>
    void write_raw ( const std::vector<V>& buffer, boost::true_type ) const;

    template<class V>
    void write_raw ( V buffer, boost::true_type ) const;

    template<class V>
    std::vector<V> read_raw ( size_t length, boost::true_type ) const;

    template<class V>
    V read_raw (boost::true_type) const;


};


//***********************************************************************************************************
//***********************************************************************************************************
//***********************************************************************************************************
template<class C,class T>
basic_Device<C,T>::basic_Device() 
  : record_delimiter('\n')
{
}
    
template<class C,class T>
basic_Device<C,T>::basic_Device ( const boost::shared_ptr<InputStream>& default_input_stream )
  : input_stream(default_input_stream),
    record_delimiter('\n')
{
  default_input_stream->setf(::std::ios::boolalpha);
  if ( default_input_stream->fail() ) throw IOError(strerror(errno));
}

template<class C,class T>
basic_Device<C,T>::basic_Device ( const boost::shared_ptr<OutputStream>& default_output_stream ) 
  : output_stream(default_output_stream),
    record_delimiter('\n')
{
  default_output_stream->setf(::std::ios::boolalpha);
  if ( default_output_stream->fail() ) throw IOError(strerror(errno));
}

template<class C,class T>
basic_Device<C,T>::basic_Device ( const boost::shared_ptr<InputStream>&  default_input_stream,
                                  const boost::shared_ptr<OutputStream>& default_output_stream )
  : input_stream(default_input_stream), 
    output_stream(default_output_stream),
    record_delimiter('\n')

{  
  input_stream->setf(::std::ios::boolalpha);
  if ( input_stream->fail() ) throw IOError(strerror(errno));
  output_stream->setf(::std::ios::boolalpha);
  if ( output_stream->fail() ) throw IOError(strerror(errno));
}

template<class C,class T>
bool  basic_Device<C,T>::atEOF () const 
{ 
  return In().peek() == T::eof() || !In().good();
}

//***********************************************************************************************************
// Sets the input/output stream to that supplied.
template<class C,class T>
void basic_Device<C,T>::setInputStream ( const boost::shared_ptr<InputStream>& stream)
{
  if (stream)
  {
    stream->setf(::std::ios::boolalpha);
    if ( stream->fail() ) throw IOError(strerror(errno));
  }    
  input_stream = stream;
}

template<class C,class T>
void basic_Device<C,T>::setOutputStream ( const boost::shared_ptr<OutputStream>& stream)
{
  if (stream)
  {
    stream->setf(::std::ios::boolalpha);
    if ( stream->fail() ) throw IOError(strerror(errno));
  }    

  if ( outputValid() )
  {
    Out().flush();
  }

  output_stream = stream;
}


//***********************************************************************************************************
// Stream operators for data.
template<class C,class T>
template<class D>
const basic_Device<C,T>& basic_Device<C,T>::operator << (const D& data) const
{
  Out().clear();
  Out() << data;
  checkWrite();
  return *this;
}

template<class C,class T>
template<class D>
const basic_Device<C,T>& basic_Device<C,T>::operator >> (D& data) const
{
  In().clear();
  In() >> data;
  checkRead();
  return *this;
}


template<class C,class T>
template<class D>
const basic_Device<C,T>& basic_Device<C,T>::operator < (const D& data) const
{
  Out().clear();
  Out() < data;
  checkWrite();
  return *this;
}

template<class C,class T>
template<class D>
const basic_Device<C,T>& basic_Device<C,T>::operator > (D& data) const
{
  In().clear();
  In() > data;
  checkRead();
  return *this;
}


template<class C,class T>
const basic_Device<C,T>& basic_Device<C,T>::operator << ( const basic_Device<C,T>& data ) const
{
  Out().clear();
  Out() << data.In().rdbuf();
  checkWrite();
  return *this;
}

template<class C,class T>
const basic_Device<C,T>& basic_Device<C,T>::operator >> ( basic_Device<C,T>& data ) const
{
  In().clear();
  In() >> data.Out().rdbuf();
  checkRead();
  return *this;
}

template<class C,class T>
const basic_Device<C,T>& basic_Device<C,T>::operator << (OutputStream& (*op)(OutputStream&)) const
{
  Out().clear();
  (*op)(Out());
  checkWrite();
  return *this;
}

template<class C,class T>
const basic_Device<C,T>& basic_Device<C,T>::operator >> (InputStream& (*op)(InputStream&)) const
{
  In().clear();
  (*op)(In());  
  checkRead();
  return *this;
}


template<class C,class T>
const basic_Device<C,T>& basic_Device<C,T>::getline ( std::string& data ) const
{
  In().clear();
  ::std::getline(In(),data, record_delimiter);
  checkRead();
  return *this;
}

template<class C,class T>
const basic_Device<C,T>& basic_Device<C,T>::getline ( SWA::String& data ) const
{
  std::string tmp;
  ::std::getline(In(),tmp, record_delimiter);
  data = tmp;
  checkRead();
  return *this;
}


template<class C,class T>
void basic_Device<C,T>::write_raw ( const char* data, size_t size ) const
{
  Out().clear();
  Out().write(data,size);    
  checkWrite();
}

template<class C,class T>
void basic_Device<C,T>::write_raw ( const std::string& buffer ) const
{
  write_raw(buffer.data(),buffer.size());
}

template<class C,class T>
template<class V>
void basic_Device<C,T>::write_raw ( const std::vector<V>& buffer ) const
{
  write_raw(buffer, typename boost::is_fundamental<V>::type());
}

template<class C,class T>
template<class V>
void basic_Device<C,T>::write_raw ( const V buffer ) const
{
  write_raw(buffer, typename boost::is_fundamental<V>::type());
}


template<class C,class T>
template<class V>
void basic_Device<C,T>::write_raw ( const std::vector<V>& buffer, boost::true_type ) const
{
  write_raw(reinterpret_cast<const char*>(&buffer[0]),buffer.size()*sizeof(V));
}

template<class C,class T>
template<class V>
void basic_Device<C,T>::write_raw ( V buffer, boost::true_type ) const
{
  write_raw(reinterpret_cast<const char*>(&buffer),sizeof(V));
}

template<class C,class T>
void basic_Device<C,T>::write_raw ( char value ) const
{
  Out().clear();
  Out().put(value);
  checkWrite();
}

template<class C,class T>
std::vector<char> basic_Device<C,T>::read_raw_bytes ( int32_t length ) const
{
  std::vector<char> result(length);
  In().clear();
  In().read(&result[0],length);
  checkRead();
  return result;
}

template<class C,class T>
std::string basic_Device<C,T>::read_raw_string ( int32_t length ) const
{
  std::vector<char> result = read_raw_bytes(length);
  checkRead();
  return std::string(result.begin(),result.end());
}

template<class C,class T>
char basic_Device<C,T>::read_raw_char() const
{
  char c;
  In().clear();
  In().get(c);
  checkRead();
  return c;
}

template<class C,class T>
template<class V>
std::vector<V> basic_Device<C,T>::read_raw ( size_t length ) const
{
  return read_raw<V>(length, typename boost::is_fundamental<V>::type());
}



template<class C,class T>
template<class V>
std::vector<V> basic_Device<C,T>::read_raw ( size_t length, boost::true_type ) const
{
  std::vector<V> result(length);
  In().clear();
  In().read(reinterpret_cast<char*>(&result[0]),length*sizeof(V));
  checkRead();
  return result;

}

template<class C,class T>
template<class V>
V basic_Device<C,T>::read_raw () const
{
  return read_raw<V>(typename boost::is_fundamental<V>::type());
}

template<class C,class T>
template<class V>
V basic_Device<C,T>::read_raw (boost::true_type) const
{
  V result;
  In().clear();
  In().read(reinterpret_cast<char*>(&result),sizeof(V));
  checkRead();
  return result;
}


template<class C,class T>
void basic_Device<C,T>::checkRead() const
{
  if ( In().fail() )
  {
    if (In().eof())
    {
      In().clear();
      throw IOError("End of file");
    }
    else if (In().bad())
    {
      In().clear();
      throw IOError("Read failed");
    }
    else
    {  
      In().clear();
      throw IOError("Format error");
    }
  }
}

template<class C,class T>
void basic_Device<C,T>::checkWrite() const
{
  if ( Out().fail() )
  {
    Out().clear();
    throw IOError("Write failed");
  }
}

template<class C,class T>
std::ostream& operator << (std::ostream& str, const basic_Device<C,T>& device)
{
   str << "<opaque device>";
   return str;
}

} // end namespace SWA


#endif
