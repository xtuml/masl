//============================================================================
// UK Crown Copyright (c) 2005. All rights reserved.
//
// File:    
//
// Description:    
//
//============================================================================//
#ifndef SOCKET_UnbufferedSocketBufP__
#define SOCKET_UnbufferedSocketBufP__

#ifdef sun
# define BSD_COMP
#endif

#include <cerrno>
#include <stdexcept>
#include <streambuf>
#include <algorithm>

#include <unistd.h>
#include <sys/ioctl.h>

#include "sockets/socketLogging.hh"
#include "sockets/socketReliableClient.hh"

namespace SKT {

// *******************************************************************
// *******************************************************************
// *******************************************************************
template <class charT, class traits>
class UnbufferedSocketBuf : public std::basic_streambuf<charT,traits>
{
  public:
     typedef typename std::basic_streambuf<charT,traits>::int_type    int_type;
     typedef typename std::basic_streambuf<charT,traits>::char_type   char_type;
     typedef typename std::basic_streambuf<charT,traits>::traits_type traits_type;
     

  public:
     UnbufferedSocketBuf (const int_type iSocket); 
    ~UnbufferedSocketBuf();

     void setFd (const int_type iFd) { socketD_ = iFd; }

  protected:
     // Provide implementations of the virtual methods in the 
     // base class, that are required for unbuffered operation.
     int_type   underflow   ();
     int_type   uflow       ();
     int_type   overflow    (int_type c);
     int_type   pbackfail   (int_type c);
     std::streamsize xsputn (const char_type* s, std::streamsize n);
     
     // The current implementation of these methods uses a default
     // read and write mechanism using the read/write system calls.
     // As other read/write calls are avaliable to sockets these
     // methods are defined as virtual. Just extend from this class
     // and provide the required functionality.
     virtual int_type writeDevice (char_type* iC,  int iSize);
     virtual int_type readDevice  (char_type* ioC, int iSize);
  
  private:  
     // prevent copy and assignment of this buffer.
     UnbufferedSocketBuf(const UnbufferedSocketBuf& rhs);
     UnbufferedSocketBuf& operator=(const UnbufferedSocketBuf& rhs);

  private:  
     // The file descriptor for the socket.
     int  socketD_;
     
     // underflow does not consume character it transfers (stream position not changed)
     // as a result successive calls to underflow() must all return same value referred 
     // to by current stream position. Therefore store the character in charBuf_ once we 
     // get it from the device, so do not have to repeatedly fetch.
     char_type charBuf_;
     
     // indicate whether the character in charBuf_ has 
     // been consumed (true = not consumed)
     bool takenFromBuf_;

};

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
class BufferedSocketBuf : public std::basic_streambuf<charT,traits>
{
  public:
     typedef typename std::basic_streambuf<charT,traits>::int_type    int_type;
     typedef typename std::basic_streambuf<charT,traits>::char_type   char_type;
     typedef typename std::basic_streambuf<charT,traits>::traits_type traits_type;

  public:
     BufferedSocketBuf(const int iSocketD);
     virtual ~BufferedSocketBuf();

     void setFd (const int iSocketD);

  protected:
      #if (defined __DECCXX) && (__DECCXX_VER <= 60199999) 
        int showmanyc();
      #else
        std::streamsize showmanyc();
      #endif

    int_type flush_buffer();

    virtual int_type sync      ();
    virtual int_type overflow  (int_type c);
    virtual int_type underflow ();
    std::streamsize  xsputn    (const char_type* s, std::streamsize n);
 
    // The current implementation of these methods uses a default
    // read and write mechanism using the read/write system calls.
    // As other read/write calls are avaliable to sockets these
    // methods are defined as virtual. Just extend from this class
    // and provide the required functionality.
    virtual int_type writeDevice (char_type* iC,  int iSize);
    virtual int_type readDevice  (char_type* ioC, int iSize);
      
  private:
      enum { PUTBACK_BUFFER_SIZE=10 };
    
      char writeBuffer_[BUFFER_SIZE];
      char readBuffer_ [BUFFER_SIZE+PUTBACK_BUFFER_SIZE];
      int  socketD_;
};


// ******************************************************************
// ******************************************************************
// ******************************************************************
template <class charT, class traits>
UnbufferedSocketBuf<charT,traits>::UnbufferedSocketBuf (const int_type iFd):
     socketD_(iFd),
     charBuf_(traits_type::eof()),
     takenFromBuf_(false)
{
   Logger::trace<int>("UnbufferedSocketBuf::UnbufferedSocketBuf","iFd", iFd);
} 

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <class charT, class traits>
UnbufferedSocketBuf<charT,traits>::~UnbufferedSocketBuf()
{
   Logger::trace<int>("UnbufferedSocketBuf::~UnbufferedSocketBuf","socketD_",socketD_);
}

// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
std::streamsize UnbufferedSocketBuf<charT,traits>::xsputn (const char_type* iCharSequence, std::streamsize iSequenceLength)
{
   Logger::trace("UnbufferedSocketBuf::xsputn");
    
   // Provide an efficient mechanism for transporting a sequence of characters.
   // Therefore when calling 'client << "12345" << std::endl;' this xsputn method
   // will be called rather than multiple calls to overflow(...)
   char_type* charArray = new char_type[iSequenceLength];
   std::streamsize bytesToWrite = 0;
   for(std::streamsize i=0; i < iSequenceLength; ++i){
      if (traits_type::eq_int_type(iCharSequence[i],traits_type::eof())){
          break;
      }
      charArray[i] = traits_type::to_char_type(iCharSequence[i]);
      ++bytesToWrite;
   }
   
   std::streamsize bytesWritten = 0;
   if ( (bytesWritten = writeDevice(charArray, bytesToWrite)) < 0){
       // throw exception 
   }
   delete [] charArray;
   return bytesWritten;
}

// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
typename UnbufferedSocketBuf<charT,traits>::int_type UnbufferedSocketBuf<charT,traits>::overflow(int_type c)
{
   Logger::trace<char_type>("UnbufferedSocketBuf::overflow","c", traits_type::to_char_type(c));
    
    if (!traits_type::eq_int_type(c,traits_type::eof())){
       char_type charToWrite(traits_type::to_char_type(c));
       if (writeDevice(&charToWrite, sizeof(char_type)) < 0){
	  return traits_type::eof();
       }
    }
    return c;
}

// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
typename UnbufferedSocketBuf<charT,traits>::int_type UnbufferedSocketBuf<charT,traits>::underflow()
{
    if (takenFromBuf_ == true){  // not consumed
      Logger::trace<char_type>("UnbufferedSocketBuf::underflow","charBuf_", charBuf_);
      return traits_type::to_int_type(charBuf_);
    }  
    else{
      char_type c;
      if (readDevice(&c,sizeof(c)) <= 0){
         return traits_type::eof();
      }
      else{
	charBuf_ = c;
        takenFromBuf_ = true;
	Logger::trace<char_type>("UnbufferedSocketBuf::underflow","c", c);
        return traits_type::to_int_type(c);
      }
    }
}

// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
typename UnbufferedSocketBuf<charT,traits>::int_type UnbufferedSocketBuf<charT,traits>::uflow()
{
   // Must be redefines for unbuffered case because default
   // implementation moves the next pointer of the get area, 
   // this is not possible in unbuffered case.
   Logger::trace("UnbufferedSocketBuf::uflow");
   
   if (takenFromBuf_ == true){  // not consumed
       takenFromBuf_ = false;
       Logger::trace<char_type>("UnbufferedSocketBuf::uflow","charBuf_",charBuf_);
       return traits_type::to_int_type(charBuf_);
   }
   else{
     char_type c;
     if (readDevice(&c,sizeof(c)) < 0){
         return traits_type::eof();
     }
     else{
        charBuf_ = c;
	takenFromBuf_ = true;
        Logger::trace<char_type>("UnbufferedSocketBuf::uflow","c",c);
        return traits_type::to_int_type(c);
     }
   }   
}

// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
typename UnbufferedSocketBuf<charT,traits>::int_type UnbufferedSocketBuf<charT,traits>::pbackfail(int_type c)
{
   // A call to sbumpc() followed by sungetc() must have
   // the same effect as invocation of sgetc(). For this 
   // reason must implement pbackfail(), because it is called
   // by sungetc. The default implementation of this function
   // does not work for unbuffered as is produces a decrement
   // of the next pointer.
   
   Logger::trace<char_type>("UnbufferedSocketBuf::pbackfail","c",traits_type::to_char_type(c));
   
   if (takenFromBuf_ == false){
       if (!traits_type::eq_int_type(c,traits_type::eof())){
           charBuf_ = traits_type::to_char_type(c);
       } 
       takenFromBuf_ = true;
       return traits_type::to_int_type(charBuf_);
   }
   else{
     return traits_type::eof();
   }
}

// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
typename UnbufferedSocketBuf<charT,traits>::int_type UnbufferedSocketBuf<charT,traits>::writeDevice(char_type* iC, int iSize)
{
   Logger::trace<int>("UnbufferedSocketBuf::writeDevice","iSize",iSize);
   
   int_type written   = 0;
   int_type bytesLeft = iSize;
   while (bytesLeft > 0){
       written = ::write(socketD_,iC,bytesLeft);
       if (written < 0){
     	  if (written != EPIPE){
	      throw SocketIOException("UnbufferedSocketBuf::writeDevice : write failed",errno);
	  }
	  else{
	    return 0;
	  }    
       }
       bytesLeft -= written;
       iC	 += written;
   }  
   
   Logger::trace<int>("UnbufferedSocketBuf::writeDevice","written",written);
   return (iSize-bytesLeft);
}
     
// *****************************************************************
// *****************************************************************
// *****************************************************************
template <class charT, class traits>
typename UnbufferedSocketBuf<charT,traits>::int_type UnbufferedSocketBuf<charT,traits>::readDevice(char_type* ioC, int iSize)
{
   Logger::trace<int>("UnbufferedSocketBuf::readDevice","iSize",iSize);
   
   int_type readBytes = ::read(socketD_,ioC,iSize);
   if (readBytes < 0){
      throw SocketIOException("UnbufferedSocketBuf::readDevice : read failed",errno);
   }
   
   Logger::trace<int>("UnbufferedSocketBuf::readDevice","readBytes",readBytes);
   return readBytes;
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
BufferedSocketBuf<BUFFER_SIZE,charT,traits>::BufferedSocketBuf(const int iSocketD) : socketD_(iSocketD)
{
  Logger::trace<int>("BufferedSocketBuf::BufferedSocketBuf","socketD_", socketD_);
  this->setp(writeBuffer_,writeBuffer_+BUFFER_SIZE-1);
  this->setg(readBuffer_+PUTBACK_BUFFER_SIZE,readBuffer_+PUTBACK_BUFFER_SIZE,readBuffer_+PUTBACK_BUFFER_SIZE);
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
BufferedSocketBuf<BUFFER_SIZE,charT,traits>::~BufferedSocketBuf()
{
  Logger::trace<int>("BufferedSocketBuf::~BufferedSocketBuf","socketD_", socketD_);
  try {
    this->sync();
  }
  catch(...){
   
  }  
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
void BufferedSocketBuf<BUFFER_SIZE,charT,traits>::setFd (const int iSocketD)
{
  Logger::trace<int>("BufferedSocketBuf::setFd","iSocketD", iSocketD);
  this->setp(writeBuffer_,writeBuffer_+BUFFER_SIZE-1);
  this->setg(readBuffer_+PUTBACK_BUFFER_SIZE,readBuffer_+PUTBACK_BUFFER_SIZE,readBuffer_+PUTBACK_BUFFER_SIZE);
  socketD_ = iSocketD;
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
typename BufferedSocketBuf<BUFFER_SIZE,charT,traits>::int_type BufferedSocketBuf<BUFFER_SIZE,charT,traits>::flush_buffer()
{
  Logger::trace ("BufferedSocketBuf::flush_buffer");
  int_type numBytes   = this->pptr()-this->pbase();
  if (numBytes > 0){
      int_type numWritten = writeDevice(writeBuffer_,numBytes);
      if (numWritten == 0) {
          Logger::trace ("BufferedSocketBuf::underflow","wrote zero bytes, connection closed !!!");
          return traits_type::eof(); 
      } 
      this->pbump(-numBytes);
  }
  return numBytes;
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
typename BufferedSocketBuf<BUFFER_SIZE,charT,traits>::int_type BufferedSocketBuf<BUFFER_SIZE,charT,traits>::sync()
{
  if ( flush_buffer() == traits_type::eof()){
    return -1;
  }
  return 0;
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
std::streamsize BufferedSocketBuf<BUFFER_SIZE,charT,traits>::xsputn (const char_type* iText, std::streamsize iTextSize)
{
   Logger::trace ("BufferedSocketBuf::xsputn","iTextSize",iTextSize);
   if (iTextSize < this->epptr() - this->pptr()){
       memcpy(this->pptr(),iText, iTextSize*sizeof(char_type));
       this->pbump(iTextSize);
       return iTextSize;
   }
   else{
     for (std::streamsize i=0; i< iTextSize; i++){
        if (traits_type::eq_int_type(this->sputc(iText[i]), traits_type::eof())){
	   return i;
	}
     }
     return iTextSize;
   } 
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
typename BufferedSocketBuf<BUFFER_SIZE,charT,traits>::int_type BufferedSocketBuf<BUFFER_SIZE,charT,traits>::overflow ( int_type c )
{
  Logger::trace ("BufferedSocketBuf::overflow");
  if ( c != traits_type::eof() )
  {
    *(this->pptr()) = traits_type::to_char_type(c);
    this->pbump(1);
  }
  if (flush_buffer() == traits_type::eof()){
     return traits_type::eof();
  }
  return c;
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
typename BufferedSocketBuf<BUFFER_SIZE,charT,traits>::int_type BufferedSocketBuf<BUFFER_SIZE,charT,traits>::underflow()
{
  Logger::trace ("BufferedSocketBuf::underflow");
  if ( this->gptr() < this->egptr())
  {
    return traits_type::to_int_type(*(this->gptr()));
  }

  int numPutback = this->gptr() - this->eback();
  if ( numPutback > PUTBACK_BUFFER_SIZE)
  {
    numPutback = PUTBACK_BUFFER_SIZE;
  }

  memcpy(readBuffer_+(PUTBACK_BUFFER_SIZE-numPutback), this->gptr()-numPutback, numPutback);

  int numRead = readDevice(readBuffer_+PUTBACK_BUFFER_SIZE, BUFFER_SIZE);
  if (numRead == 0) {
     Logger::trace ("BufferedSocketBuf::underflow","read zero bytes connection closed !!!");
     return traits_type::eof(); // read zero bytes connection closed !!!
  } 
  
  this->setg(readBuffer_+(PUTBACK_BUFFER_SIZE-numPutback),readBuffer_+PUTBACK_BUFFER_SIZE,readBuffer_+PUTBACK_BUFFER_SIZE+numRead);

  // Cast to unsigned to prevent sign bit on 0xFF  
  return traits_type::to_int_type(*(this->gptr()));
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
#if (defined __DECCXX) && (__DECCXX_VER <= 60199999) 
int BufferedSocketBuf<BUFFER_SIZE,charT,traits>::showmanyc()
#else
std::streamsize BufferedSocketBuf<BUFFER_SIZE,charT,traits>::showmanyc()
#endif
{
  Logger::trace ("BufferedSocketBuf::showmanyc");
  int avail;
  if ( ioctl(socketD_,FIONREAD,&avail) == -1 )
    return -1;
  else
    return avail;
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
typename BufferedSocketBuf<BUFFER_SIZE,charT,traits>::int_type BufferedSocketBuf<BUFFER_SIZE,charT,traits>::writeDevice(char_type* iC,  int iSize)
{
  char_type* bufferPtr   = iC;
  int_type   bytewritten = 0;
  int_type   bytesLeft   = iSize;
  
  while (bytesLeft > 0){
     bytewritten = ::write(socketD_,bufferPtr,bytesLeft);
     if (bytewritten < 0){
	 if (errno == EINTR){
	    continue;
	 }
         else if (errno != EPIPE){
	      Logger::trace ("BufferedSocketBuf::underflow","write returned with EPIPE - connection closed !!!");
	      bytesLeft = iSize;  // return a zero value to the caller.
	      break;
	 } 
	 else{
	    // I would like to throw SocketIOException here to report an error
	    // to the stream class. But the stream class only catches std::exception's.
	    // Therefore need to throw a runtime_error so that the stream class will catch
	    // the error and set the streams bad bit. If execptions have been enabled on the
	    // stream class then this exception will propergate out of the stream class.
	    std::string errMsg("BufferedSocketBuf::writeDevice : write failed - ");
	    errMsg += strerror(errno);
	    throw std::runtime_error(errMsg);
	 }   
     }
     bytesLeft -= bytewritten;
     bufferPtr += bytewritten;
  }
  return (iSize-bytesLeft);
}

// ******************************************************************
// ******************************************************************
// ******************************************************************
template <int BUFFER_SIZE, class charT, class traits>
typename BufferedSocketBuf<BUFFER_SIZE,charT,traits>::int_type BufferedSocketBuf<BUFFER_SIZE,charT,traits>::readDevice(char_type* iC,  int iSize)
{
  int bytesRead = 0;
  bool interrupted = false;
  do
  {  
     interrupted = false;
     bytesRead = ::read(socketD_,iC,iSize);
     if (bytesRead < 0){
         if (errno == EINTR){
	     interrupted = true;
	     continue;
	 }
	 if (errno != EAGAIN) { 
	     // I would like to throw SocketIOException here to report an error
	     // to the stream class. But the stream class only catches std::exception's.
	     // Therefore need to throw a runtime_error so that the stream class will catch
	     // the error and set the streams bad bit. If execptions have been enabled on the
	     // stream class then this exception will propergate out of the stream class.
	     std::string errMsg("SocketReliableClient::readDevice : read failed - ");
	     errMsg += strerror(errno);
	     throw std::runtime_error(errMsg);
	 }  
      }
  } while (interrupted == true);
  return bytesRead;
}

} // end namespace SKT  

#endif 
