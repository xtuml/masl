// 
// Filename : MemoryBuffer.hh
//
// UK Crown Copyright (c) 2008. All Rights Reserved
//
#ifndef SWAMemoryBuffer__
#define SWAMemoryBuffer__

namespace SWA {

// **********************************************************
//! Define a buffer that encapsulates the functionality
//! required to manage memory being used for the packing and
//! unpacking of codec specific binary data.
// **********************************************************
class MemoryBuffer
{
   public:
      MemoryBuffer();
      MemoryBuffer(const unsigned char* const data, const unsigned int size);
     ~MemoryBuffer();

      void clear ();
      void add   (const unsigned char* const data, const unsigned int dataSize); 
      
      const unsigned int         getUsed() const { return used_;}
      const unsigned int         getSize() const { return size_;}
      const unsigned char* const getData() const { return buffer_;}

    private:
      MemoryBuffer(const MemoryBuffer& rhs);
      MemoryBuffer& operator=(const MemoryBuffer& rhs);

      void grow(const unsigned int requiredSize);

    private:
        unsigned char* buffer_;
	unsigned int   used_;    
	unsigned int   size_;
};


} // end SWA namespace
#endif
