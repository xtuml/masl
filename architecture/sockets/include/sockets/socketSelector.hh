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

#include <string>
#include <cerrno>
#include <vector>
#include <cstring>
#include <utility>
#include <stdexcept>
#include <algorithm>

#include <sys/time.h>
#include <sys/select.h>

#include "sockets/socketLogging.hh"
#include "sockets/socketCommon.hh"

namespace SKT {

// **************************************************************************
// **************************************************************************
// **************************************************************************
class WaitForever
{
   public:
     typedef timeval* time_type;

   public:
     // Wait until one of the descriptors has been 
     // flagged for reading or writing. 
     time_type  period() { Logger::trace("WaitForever::period"); return NULL;}
      
     // This loop around the select should never end unless a descriptor is
     // ready for reading/writing. Therefore just return false.
     bool expired () const { Logger::trace("WaitForever::expired"); return false; }
   
     // If the select is interupted by delivery of a signal, 
     // then still need to continue in the loop, so do nothing.
     void interupted () { Logger::trace("WaitForever::interupted"); }
};

// **************************************************************************
// **************************************************************************
// **************************************************************************
template<int seconds, int microSeconds>
class Wait
{
   public:
      typedef timeval  time_type;

   public:
       Wait():
          hasExpired_(false)
       { 
          interval_.tv_sec  = seconds;
	  interval_.tv_usec = microSeconds;
	  getTime(expiryTime_);
	 
	  expiryTime_.tv_sec  += seconds;
	  expiryTime_.tv_usec += microSeconds;
       }
      
       time_type* period  ()        
       { 
         Logger::trace<time_t>     ("Wait::period","interval_.tv_sec", interval_.tv_sec);
         
	 // DEC platform does not define suseconds_t unless _XOPEN_SOURCE>=500 is defined
	 // therefore just use an int for the trace.
	 Logger::trace<int>("Wait::period","interval_.tv_usec",interval_.tv_usec);
	 return &interval_;  
       }
       
       bool expired () const  { 
         Logger::trace<bool> ("Wait::expired","hasExpired_", hasExpired_);
	 return hasExpired_; 
       }

       // The select system call was interupted before its time-out
       // had expired. Therefore need to calculate the remaining time
       // and call the select again with the remaining interval.
       void interupted () 
       { 
          Logger::trace("Wait::interupted");
	  time_type interuptedTime;
	  getTime(interuptedTime);
	  if (lessThan(interuptedTime,expiryTime_)){
	      double interuptedTimeAsD = timeAsDouble(interuptedTime);
	      double expiryTimeAsD     = timeAsDouble(expiryTime_);
	      double remainingTimeAsD  = expiryTimeAsD-interuptedTimeAsD;
	      doubleAsTime(remainingTimeAsD,interval_);
              Logger::trace<time_t> ("Wait::interupted","interval_.tv_sec", interval_.tv_sec);
              
	      // DEC platform does not define suseconds_t unless _XOPEN_SOURCE>=500 is defined
	      // therefore just use an int for the trace.
	      Logger::trace<int>    ("Wait::interupted","interval_.tv_usec",interval_.tv_usec);
	  }
	  else{
	    Logger::trace("Wait::interupted","time interval has expired");
	    hasExpired_ = true;
	  }
       }
       
    private: 
        void getTime(time_type& oTime)
	{
	   if (gettimeofday(&oTime,NULL) < 0){
	       std::string errorMsg = std::string("gettimeofday failed : ") + strerror(errno);
	       throw std::runtime_error(errorMsg);
	   }
	}

        bool lessThan(const time_type& iLhs, const time_type& iRhs)
	{
 	  return  (iLhs.tv_sec  <  iRhs.tv_sec  ||
 	           (iLhs.tv_sec  == iRhs.tv_sec &&
 	           iLhs.tv_usec <  iRhs.tv_usec));
        }
  
       double timeAsDouble(const time_type& iTime) 
       { 
         return (double)(iTime.tv_sec) + ((double)(iTime.tv_usec)/1000000.0); 
       }
       
       void doubleAsTime(const double iTimeAsD, time_type& iTime) 
       { 
         iTime.tv_sec  = static_cast<time_t>(iTimeAsD);
	 #ifdef __osf__
	   // DEC platform does not define suseconds_t unless _XOPEN_SOURCE>=500 is defined
	   // therefore just use an int for the trace.
	   iTime.tv_usec = static_cast<int>(((double)(iTimeAsD-iTime.tv_sec)*1000000.0));
         #else
	   iTime.tv_usec = static_cast<suseconds_t>(((double)(iTimeAsD-iTime.tv_sec)*1000000.0));
	 #endif
       }
   
   private:
        bool      hasExpired_;
	time_type interval_;
	time_type expiryTime_;
};

// **************************************************************************
// **************************************************************************
// **************************************************************************
class NoWait
{
   public:
      typedef timeval time_type;

   public:
      NoWait():expiredCount(0) { interval_.tv_sec = 0; interval_.tv_usec=0; }
   
     time_type* period     ()         { Logger::trace("NoWait::period");  return &interval_;     }  
     bool       expired    ()         { Logger::trace("NoWait::expired"); return expiredCount++; } // only allow entry to select loop once.
     void       interupted ()         { Logger::trace("NoWait::interupted");                     }

   private:
	int       expiredCount; 
	time_type interval_;
};

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
class Select
{
   public:
      typedef std::vector<int>                  DescriptorContType;
      typedef std::vector<int>::iterator        DescriptorItrType;
      typedef std::vector<int>::const_iterator  DescriptorConstItrType;
   
   public:
      Select();
     ~Select();
   
      void sense(DescriptorContType& oActiveReadDescriptors, DescriptorContType& oActiveWriteDescriptors);
      
      void addSenseRead  (const int iDescriptor);
      void addSenseWrite (const int iDescriptor);

      void removeSenseRead  (const int iDescriptor);
      void removeSenseWrite (const int iDescriptor);
      
      const DescriptorContType&  senseReadList() const { return readSenseList_;  }
      const DescriptorContType&  senseWeadList() const { return writeSenseList_; }
  
   private:
      // prevent copy and assignment
      Select(const Select& rhs);
      Select& operator=(const Select& rhs);
      
      int populateSet(const DescriptorContType& iSource, fd_set& iDestination) const;
      
      void extractActiveDescriptor(const int iMaxFd, const fd_set& iFdSet, DescriptorContType& iDestContainer) const;
   
   private:
       DescriptorContType readSenseList_;
       DescriptorContType writeSenseList_;
};

// **************************************************************************
// **************************************************************************
// **************************************************************************
template<class WaitPolicy>
Select<WaitPolicy>::Select()
{
   Logger::trace("Select::Select","Constructor");
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
Select<WaitPolicy>::~Select()
{
   Logger::trace("Select::~Select","Destructor");
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
void Select<WaitPolicy>::addSenseRead  (const int iDescriptor)
{
    Logger::trace<int> ("Select::addSenseRead","iDescriptor",iDescriptor);
    if (std::find(readSenseList_.begin(),readSenseList_.end(),iDescriptor) == readSenseList_.end()){
        readSenseList_.push_back(iDescriptor);
    }
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
void Select<WaitPolicy>::addSenseWrite (const int iDescriptor)
{
    Logger::trace<int> ("Select::addSenseWrite","iDescriptor",iDescriptor);
    if (std::find(writeSenseList_.begin(),writeSenseList_.end(),iDescriptor) == writeSenseList_.end()){
        writeSenseList_.push_back(iDescriptor);
    }
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
void Select<WaitPolicy>::removeSenseRead  (const int iDescriptor)
{
   Logger::trace<int> ("Select::removeSenseRead","iDescriptor",iDescriptor);
   readSenseList_.erase(std::remove(readSenseList_.begin(),readSenseList_.end(),iDescriptor),readSenseList_.end());
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
void Select<WaitPolicy>::removeSenseWrite (const int iDescriptor)
{
   Logger::trace<int> ("Select::removeSenseWrite","iDescriptor",iDescriptor);
   writeSenseList_.erase(std::remove(writeSenseList_.begin(),writeSenseList_.end(),iDescriptor),writeSenseList_.end());
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
void Select<WaitPolicy>::sense(DescriptorContType& oActiveReadDescriptors,DescriptorContType& oActiveWriteDescriptors)
{
    Logger::trace("Select::sense");
    fd_set  readSet;
    fd_set  writeSet;
    fd_set* exceptionSet = NULL;
    
    oActiveReadDescriptors.clear();
    oActiveWriteDescriptors.clear();
    
    WaitPolicy waitInterval;
    
    while (waitInterval.expired() == false){
    
        FD_ZERO(&readSet);
        FD_ZERO(&writeSet);
    
        int maxReadFd   = populateSet(readSenseList_,  readSet);
        int maxWriteFd  = populateSet(writeSenseList_, writeSet);
        const int maxFd = (maxReadFd > maxWriteFd ? maxReadFd : maxWriteFd)+1; 
       
        if (maxFd > 0){
            int fdCount = select(maxFd,&readSet, &writeSet, exceptionSet, waitInterval.period());
            Logger::trace<int>("Select::sense","fdCount",fdCount);
	    if (fdCount < 0){
	       if (errno != EINTR){
	         // Non Signal Error.
		 throw SocketIOException("select system call failed : ",errno);
	       }
	       else{
	         // system call returned due to signal delivery
		 waitInterval.interupted();
	       }
            }
	    else{
              // If the select call returned a non zero value then extract the 
	      // active read and write descriptors. Else the fdCount value must
	      // be zero. This would indicate that the select was configured with
	      // a finite time-out period that has expired before a descriptor was
	      // flagged for read/write. Therefore just break from the loop.
	      if (fdCount > 0){
	          Logger::trace("Select::sense","active write descriptors");
	          extractActiveDescriptor(maxFd,writeSet,oActiveWriteDescriptors);
	         
		  Logger::trace("Select::sense","active read descriptors");
		  extractActiveDescriptor(maxFd,readSet, oActiveReadDescriptors);
	      }  
	      
	      // No matter what the wait policy is set to, when active
	      // descriptors are ready to be serviced must always return
	      // from this call. If select has returned with 0, then must 
	      // return as well, as this indicates the timeout period has
	      // expired.
	      break;
            }
        }
    }
}


// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
int Select<WaitPolicy>::populateSet(const DescriptorContType& iSource, fd_set& iDestination) const
{
    int maxDesc = 0;
    for(std::size_t descIndex = 0; descIndex < iSource.size(); ++descIndex){
       FD_SET(iSource[descIndex],&iDestination);
       maxDesc > iSource[descIndex] ? maxDesc = maxDesc : maxDesc = iSource[descIndex];
    }
    return maxDesc;
}

// **************************************************************************
// **************************************************************************
// **************************************************************************
template< class WaitPolicy>
void Select<WaitPolicy>::extractActiveDescriptor(const int iMaxFd, const fd_set& iFdSet, DescriptorContType& iDestContainer) const
{
    for(int currentFd = 0; currentFd < iMaxFd; ++currentFd){
      if (FD_ISSET(currentFd,&iFdSet)){
    	Logger::trace("Select::extractActiveDescriptor","descriptor",currentFd);
	iDestContainer.push_back(currentFd);
      }
    }
}

} // end namespace
