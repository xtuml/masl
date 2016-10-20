// 
// Filename : pm_buffered_io.hh
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
#ifndef Inspector_BufferedIO_HH
#define Inspector_BufferedIO_HH

#include <vector>
#include <set>
#include <map>
#include <string>
#include <algorithm>
#include <unistd.h>
#include <errno.h>
#include <sys/time.h>
#include <stdint.h>
#include "swa/String.hh"
#include "swa/Dictionary.hh"
#include "swa/ObjectPtr.hh"
#include "swa/Set.hh"


namespace Inspector {

  class WritableObject;
  class ReadableObject;

  //*******************************************************************
  //
  //                    Buffered Output Stream
  //
  //*******************************************************************

  // The BufferedOutputStream class provides functions to write 
  // various data types to a unix file handle. The output to the 
  // file is in a binary format with big endian byte ordering. 
  // The output is buffered in an internal array before being 
  // written to the file when the array gets to large or when 
  // the stream is explicitly flushed. 
  class BufferedOutputStream
  {
    private:
      // Unix file handle for the file to be written to.
      int fp;

      // The internal buffer used to store data before writing to the file.
      std::vector<unsigned char> buffer;

      // The maximum size of the buffer allowed before the 
      // buffer is flushed to file. 
      enum { FLUSH_LIMIT = 1400 };

      template<class T> void writeRaw ( const T& bytes );

      template<class InputIterator> void writeRange ( InputIterator first, InputIterator last );

    public:
      // Constructors
      BufferedOutputStream () : fp(-1) { buffer.reserve(FLUSH_LIMIT); };
      BufferedOutputStream ( int fp ) : fp(fp) {buffer.clear();};

      // Sets the file handle
      void setFp( int fp ) { this->fp = fp; buffer.clear(); }

      // Writes the value val to the file. 
      template<class T> 
      void write ( const T& val );

      template<class T1,class T2> 
      void write ( const std::pair<T1,T2>& val );

      template<class T> 
      void write ( const std::vector<T>& val );

      template<class T> 
      void write ( const SWA::Set<T>& val );

      template<class K,class V> 
      void write ( const SWA::Dictionary<K,V>& val );

      template<class T> 
      void write ( const SWA::Bag<T>& val );

      template<class T> 
      void write ( const SWA::Sequence<T>& val );

      template<class T> 
      void write ( const SWA::ObjectPtr<T>& val );

      void write ( const SWA::ObjectPtr<void>& val );

      void write ( const char* val );

      // Provide non-template versions for fundamental types for efficiency
      void write ( uint64_t val );
      void write ( int64_t val );
      void write ( uint32_t val );
      void write ( int32_t val );
      void write ( double val );
      void write ( char val );
      void write ( unsigned char val );
      void write ( signed char val );
      void write ( bool val );


      // Writes the collection to the file, including a size integer at the start. 
      template<class InputIterator> void write ( InputIterator first, InputIterator last );

      // Flushes any data in the buffer to the file.
      void flush();

  };

  // Writes the first and second values of the pair to the file.
  template<class T1,class T2> 
  void BufferedOutputStream::write ( const std::pair<T1,T2>& val )
  {
    write(val.first);
    write(val.second);
  }

  // Writes the first and second values of the pair to the file.
  template<class T> 
  void BufferedOutputStream::write ( const std::vector<T>& val )
  {
    write(static_cast<int>(val.size()));
    writeRange(val.begin(), val.end());
  }

  template<class T>
  void BufferedOutputStream::write ( const SWA::Set<T>& val )
  {
    write(static_cast<int>(val.first()));
    write(static_cast<int>(val.last()));
    writeRange(val.begin(), val.end());
  }

  template<class T>
  void BufferedOutputStream::write ( const SWA::Bag<T>& val )
  {
    write(static_cast<int>(val.first()));
    write(static_cast<int>(val.last()));
    writeRange(val.begin(), val.end());
  }

  template<class T>
  void BufferedOutputStream::write ( const SWA::Sequence<T>& val )
  {
    write(static_cast<int>(val.first()));
    write(static_cast<int>(val.last()));
    writeRange(val.begin(), val.end());
  }

  template<class K, class V>
  void BufferedOutputStream::write ( const SWA::Dictionary<K,V>& val )
  {
    write(static_cast<int>(val.size()));
    writeRange(val.begin(), val.end());
  }

  template<class T>
  void BufferedOutputStream::write ( const SWA::ObjectPtr<T>& val )
  {
    bool valid = val;
    write(valid);
    if ( valid )
    {
      // Do not access ptr through 'operator->'as object 
      // may have been deleted in the current frame.
      write(val.getChecked()->getArchitectureId());
    }
  }


  // Writes the collection to the file, including a size integer at the start. 
  template<class InputIterator> 
  void BufferedOutputStream::write ( InputIterator first, InputIterator last )
  {
    write(static_cast<int>(std::distance(first,last)));
    writeRange(first,last);
  }

  template<class InputIterator> 
  void BufferedOutputStream::writeRange ( InputIterator first, InputIterator last )
  {
    while ( first != last ) write(*first++);
  }


  //*******************************************************************
  //
  //                    Buffered Input Stream
  //
  //*******************************************************************



  // The Input buffer reads data from the input streams in 
  // chunks and allows single bytes to be read from it. 
  class InputBuffer
  {
    private:
      enum { BUFFER_SIZE = 2048 }; 
      int fp;
      unsigned char buffer[BUFFER_SIZE];
      unsigned char* curPos;
      unsigned char* endPos;

      void updateBuffer();

    public:

      InputBuffer() : fp(0), curPos(buffer), endPos(buffer) {}

      void setFp(int fp) { this->fp = fp;curPos = buffer; endPos = buffer;}

      unsigned char get()
      { 
        if ( curPos == endPos ) updateBuffer();
        return *curPos;
      }

      bool available();
      bool empty() { return curPos == endPos; }

      void advance()
      { 
        if ( curPos == endPos ) updateBuffer();
        ++curPos;
      }
  };


  // InputBufferIterator provides an STL InputIterator wrapper 
  // around the InputBuffer 
  class InputBufferIterator 
    : public std::iterator<std::input_iterator_tag,unsigned char,void,void,void>
  {
    private:
      InputBuffer& buffer;
      unsigned char proxyChar;

    public:
      InputBufferIterator(InputBuffer& buffer) : buffer(buffer) {}

      value_type operator*()
      { 
        return buffer.get();
      }

      InputBufferIterator& operator++()
      { 
        buffer.advance();
        return *this;
      }

      unsigned char* operator++(int)
      {
        proxyChar = buffer.get();
        buffer.advance();
        return &proxyChar;
      }
  };



  // The BufferedInputStream class provides functions to read 
  // various data types from a unix file handle. The input from 
  // the file is assumed to be in a binary format with big 
  // endian byte ordering. The input is buffered using an 
  // InputBuffer, so that data can be read in large chunks from 
  // the file, even if it is only needed in small amounts at a 
  // time. 
  class BufferedInputStream
  {
    private:
      // The unix file handle
      int fp;

      // The buffer to store data read from the file before it is needed.
      InputBuffer buffer;

      // Reads raw bytes into val
      template<class T> void readRaw ( T& val );

      template<class Container> 
      void readCollection(Container& val);

      // Reads 'length' values into the output iterator 
      template<class Container> 
      void read_container ( std::back_insert_iterator<Container> it, int length);

      // Reads 'length' values into the output iterator 
      template<class Container> 
      void read_container ( std::insert_iterator<Container> it, int length);

    public:
      // Constructor
      BufferedInputStream () : buffer() {};

      // Sets the unix file handle
      void setFp( int fp ) { buffer.setFp(fp);}

      // Reads the value val from the file. 
      template<class T> 
      void read ( T& val );

      template<class T> 
      void read ( SWA::Set<T>& val );

      template<class T> 
      void read ( SWA::Bag<T>& val );

      template<class T> 
      void read ( SWA::Sequence<T>& val );

      template<class K,class V> 
      void read ( SWA::Dictionary<K,V>& val );

      template<class T1,class T2>
      void read ( std::pair<T1,T2>& val );

      template<class T>
      void read ( std::vector<T>& val );

      template<class T>
      void read ( std::set<T>& val );

      void read ( uint64_t& val );
      void read ( int64_t& val );
      void read ( uint32_t& val );
      void read ( int32_t& val );
      void read ( double& val );
      void read ( char& val );
      void read ( unsigned char& val );
      void read ( signed char& val );
      void read ( bool& val );
      void read ( SWA::ObjectPtr<void>& val ) {}

      // Returns true if bytes are available to be read.
      bool available() { return buffer.available();}

      // Returns true if requesting a read would mean a request to the underlying fd.
      bool empty() { return buffer.empty();}
  };


  // A non-intrusive wrapper around any class to enable it to be 
  // read from a BufferedInputStream. The read function 
  // should be specialized for each class that needs to be 
  // read from the stream.
  template <class T>
  class ReaderObjectWrapper
  {
    public:
      void read ( BufferedInputStream& stream, T& val ) const;
  };


  // Copy function to copy n bytes from in to out
  template<class InputIterator, class OutputIterator>
  void copy_n ( InputIterator in, int length, OutputIterator out )
  {
    while ( length-- )
      *out++ = *in++;
  }

  template<class T> 
  void BufferedInputStream::read ( SWA::Sequence<T>& val )
  {
    int start = 0;
    int end = 0;
    read(start);
    read(end);
    val = SWA::Sequence<T>();
    read_container(std::back_inserter(val),end-start+1);
  }

  template<class K,class V> 
  void BufferedInputStream::read ( SWA::Dictionary<K,V>& dict )
  {
    int size = 0;
    read(size);
    dict = SWA::Dictionary<K,V>();
    K key;
    V val;
    while (size--)
    {
      read(key); 
      read(val); 
      dict.setValue(key) = val;
    }
  }

  template<class T>
  void BufferedInputStream::read ( SWA::Set<T>& val )
  {
    int start = 0;
    int end = 0;
    read(start);
    read(end);
    val = SWA::Set<T>();
    read_container(val.inserter(),end-start+1);
  }

  template<class T>
  void BufferedInputStream::read ( SWA::Bag<T>& val )
  {
    int start = 0;
    int end = 0;
    read(start);
    read(end);
    val = SWA::Bag<T>();
    read_container(val.inserter(),end-start+1);
  }

  template<class T1,class T2>
  void BufferedInputStream::read ( std::pair<T1,T2>& val )
  {
    read(val.first);
    read(val.second);
  }

  template<class T>
  void BufferedInputStream::read ( std::vector<T>& val )
  {
    int size = 0;
    read(size);
    val.clear();
    val.reserve(size);
    read_container(back_inserter(val),size);
  }

  template<class T>
  void BufferedInputStream::read ( std::set<T>& val )
  {
    int size = 0;
    read(size);
    val.clear();
    val.reserve(size);
    read_container(inserter(val),size);
  }


  // Reads 'length' values into the container 
  template<class Container> 
  void BufferedInputStream::read_container ( std::back_insert_iterator<Container> it, int length)
  {
    typename Container::value_type val;
    while (length--) { read(val); *it++ = val; }
  }

  // Reads 'length' values into the container 
  template<class Container> 
  void BufferedInputStream::read_container ( std::insert_iterator<Container> it, int length)
  {
    typename Container::value_type val;
    while (length--) { read(val); *it++ = val; }
  }

} 
#endif
