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

#ifndef SocketCommon
#define SocketCommon

#include <cerrno>
#include <string>
#include <cstring>
#include <sstream>
#include <stdexcept>

namespace SKT {

// ******************************************************************************
// ******************************************************************************
// ******************************************************************************
class SocketException
{
  public:
     SocketException (const std::string& iText):text_(iText){}
     virtual ~SocketException() {}
     const std::string& report() const {return  text_; }
  private:
      const std::string text_;
};

class SocketIOException : public SocketException
{
  public:
     SocketIOException (const std::string& iText, const int iErrnoCode):
         SocketException(iText),
	 errnoCode_(iErrnoCode)   {}
     virtual ~SocketIOException() {} 
     const int         errnoCode()   const { return errnoCode_;           }
     const char* const reason()      const { return strerror(errnoCode_); }
  private:
      const int errnoCode_;
};

class SocketBindException : public SocketException
{
  public:
     SocketBindException (const int iErrnoCode):
         SocketException(std::string("Bind failed : ") + strerror(iErrnoCode)),
	 errnoCode_(iErrnoCode)   {}
     virtual ~SocketBindException() {} 
     const int         errnoCode()   const { return errnoCode_;           }
     const char* const reason()      const { return strerror(errnoCode_); }
  private:
      const int errnoCode_;
};

class SocketConnectException : public SocketException
{
  public:
     SocketConnectException (const int iErrnoCode):
         SocketException(std::string("Connect failed : ") + strerror(iErrnoCode)),
	 errnoCode_(iErrnoCode)   {}
     virtual ~SocketConnectException() {} 
     const int         errnoCode()   const { return errnoCode_;           }
     const char* const reason()      const { return strerror(errnoCode_); }
  private:
      const int errnoCode_;
};

class SocketListenException : public SocketException
{
  public:
     SocketListenException (const int iErrnoCode):
         SocketException(std::string("Listen failed : ") + strerror(iErrnoCode)),
	 errnoCode_(iErrnoCode)   {}
     virtual ~SocketListenException() {} 
     const int         errnoCode()   const { return errnoCode_;           }
     const char* const reason()      const { return strerror(errnoCode_); }
  private:
      const int errnoCode_;
};

class SocketOptionException : public SocketException
{
  public:
     SocketOptionException (const std::string& iText):
         SocketException(iText)   {}
     virtual ~SocketOptionException() {} 
};

//*******************************************************************************
//*******************************************************************************
//*******************************************************************************
template <typename T>
inline void stringToValue(const std::string& valueString, T& value)
{
   std::istringstream convertor(valueString.c_str());
   convertor >> value;
   if (convertor.bad() == true){
      throw std::runtime_error("stringToValue conversion failure");
   }
}

//*******************************************************************************
//*******************************************************************************
//*******************************************************************************
template <typename T>
inline T stringToValue(const std::string& valueString)
{
   T value;
   std::istringstream convertor(valueString.c_str());
   convertor >> value;
   if (convertor.bad() == true){
      throw std::runtime_error("stringToValue conversion failure");
   }
   return value;
}

//*******************************************************************************
//*******************************************************************************
//*******************************************************************************
template <typename T>
inline std::string valueToString(const T& value)
{
   std::ostringstream convertor;
   convertor << value;
   if (convertor.bad() == true){
      throw std::runtime_error("valueToString conversion failure");
   }
   return convertor.str().c_str();
}

//*******************************************************************************
//*******************************************************************************
//*******************************************************************************
struct DeleteObject {
  template <typename T>
  void operator ()(const T* ptr) const
  {
     delete ptr;
     ptr = NULL;
  }
};

} // end namespace SKT

#endif
