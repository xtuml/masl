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

#ifndef Sqlite_BlobData_HH
#define Sqlite_BlobData_HH

#include <string>

namespace SQLITE
{
  class BlobData
  {
    public:
      typedef std::string::const_iterator const_iterator;

      BlobData () : blob() {}

      template<class It>
      BlobData ( It begin, It end ) : blob(begin,end) {}
  
      explicit BlobData ( const std::string& str ) : blob(str) {}

      void append ( const char* data, size_t size )
      {
        blob.append(data,size);
      }
  
      template<class It>
      void assign ( It begin, It end )
      {
        blob.assign(begin,end);
      }

      const char* const data() const { return blob.data(); } 
      size_t size() const { return blob.size(); } 

      const_iterator begin() const { return blob.begin(); }  
      const_iterator end() const { return blob.end(); }  

  private:
      std::string blob;
  };

}
#endif
