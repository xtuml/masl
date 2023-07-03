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
