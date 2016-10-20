//============================================================================//
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:   socketsocketCommon.hh
//
// Description:             
//   Provide the basic types and helper functions used thoughout the applications
// created to support the data cache.
//============================================================================//

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
