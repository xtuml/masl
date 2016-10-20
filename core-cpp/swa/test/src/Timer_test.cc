//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include <boost/test/unit_test.hpp>
#include "boost/test/test_tools.hpp"

#include <map>
#include <vector>
#include <iostream>

#include "boost/shared_ptr.hpp"
#include "boost/tuple/tuple.hpp"

#include "types.hh"
#include "Timer.hh"
#include "Domain.hh"
#include "TimerImpl.hh"
#include "TimerImplFactory.hh"

#include "Process.hh"
#include "EventQueue.hh"

namespace {

// *****************************************************************
// *****************************************************************
class TestTimerEvent : public ::SWA::Event
{
    public:
                TestTimerEvent():
                    domainId(0),
                    destObjectId(0),
                    eventId(0)
                {
                   setSource(0,0);
                   setDest(0);
                }

                TestTimerEvent(int domainId, int eventId, int sourceId ,int sourceObjId, int destObjId, int destInsId):
                    domainId(domainId),
                    destObjectId(destObjId),
                    eventId(eventId)
                {
                    setSource(sourceId,sourceObjId);
                    setDest(destInsId);
                }

       virtual ~TestTimerEvent() { }

       void setDomainId (const int id)  { domainId     = id; }
       void setObjectId (const int id)  { destObjectId = id; }
       void setEventId  (const int id)  { eventId      = id; }

       virtual int getDomainId() const { return domainId;     }
       virtual int getObjectId() const { return destObjectId; }
       virtual int getEventId()  const { return eventId;      }

       virtual void invoke() const { throw SWA::ProgramError("TestTimerEvent::invoke() should not be called, is only a place holder"); }

    private:
       int domainId;
       int destObjectId;
       int eventId;
};

// ***************************************************************
// ***************************************************************
class TransientTimerImpl : public ::SWA::TimerImpl
{
   public:
     typedef  ::SWA::TimerImpl::AllTimers TimerContainerType;
     typedef  ::SWA::TimerDetails      TimerDetailsType;

       typedef std::map< uint32_t, TimerDetailsType > TimerCollectionType;

   public:
        // ***************************************************************
        // ***************************************************************
        static TransientTimerImpl& singleton()
        {
           static TransientTimerImpl instance;
           return instance;
        }

        void timerFired ( const TimerIdType timerId ) { timerFiredOrder.push_back(timerId); }


        // ***************************************************************
        // ***************************************************************
        virtual void createTimer(const TimerIdType timerId)
        {
           std::pair< TimerCollectionType::iterator, bool> timerItr = timerDb.insert(std::make_pair( timerId, TimerDetails()));
           if (timerItr.second == false){
               throw SWA::ProgramError(::boost::make_tuple("failed to create timer with id : ",timerId));
           }
        }

        // ***************************************************************
        // ***************************************************************
        virtual void deleteTimer(const TimerIdType timerId)
        {
            timerDb.erase(timerId);
        }

        // ***************************************************************
        // ***************************************************************
        virtual void cancelTimer(const TimerIdType timerId)
        {
           TimerCollectionType::iterator timerItr = timerDb.find(timerId);
           if (timerItr != timerDb.end()){
               timerItr->second.cancel(); 
           }
           else{
             throw SWA::ProgramError(::boost::make_tuple("failed to cancel timer with timer id : ",timerId));
           }
        }

        // ***************************************************************
        // ***************************************************************
        virtual void updateTimer(const TimerIdType timerId, const TimerDetailsType& timerDetails)
        {
          TimerCollectionType::iterator timerItr = timerDb.find(timerId);
          if (timerItr != timerDb.end()){
              timerDb[timerId]  = timerDetails;
          }
          else{
            throw SWA::ProgramError(::boost::make_tuple("failed to update timer with id : ",timerId));
          }
        }

        // ***************************************************************
        // ***************************************************************
        virtual void initialiseTimer (TimerIdType& nextTimerId,  TimerContainerType& timers)
        {
            uint32_t timerId = 1;
            nextTimerId      = 2;
            std::pair< TimerCollectionType::iterator, bool> insertedTimer = 
                    timerDb.insert(std::make_pair( timerId, TimerDetails( TimerDetails::Scheduled, ::SWA::Time::now(), boost::shared_ptr< SWA::Event >( new TestTimerEvent))));
            if (insertedTimer.second == true){
                timers = timerDb;
            }
            else{
              throw std::runtime_error("failed to create test timer.");
            }
            
        }

        // ***************************************************************
        // ***************************************************************
        const std::vector<uint32_t>& getFiredTimers() const { return timerFiredOrder; }

   private:
       TransientTimerImpl() { }
      ~TransientTimerImpl() { } 

   private:
       TransientTimerImpl(const TransientTimerImpl& rhs);
       TransientTimerImpl& operator=(const TransientTimerImpl& rhs);

   private:
       TimerCollectionType    timerDb;
       std::vector<uint32_t>  timerFiredOrder;
};

// *************************************************************************************
// *************************************************************************************
bool registerTimerImpl = ::SWA::TimerImplFactory::getInstance().registerImpl(&TransientTimerImpl::singleton());


void dummyEventGenerator(int,uint32_t,SWA::IdType,int,SWA::IdType)
{
}

}

class EventListener : public SWA::ProcessMonitor::MonitorConnection
{
  public:
   static EventListener& getInstance();

   virtual std::string getName() { return "Event Listener"; }

   bool initialise();
   virtual void firingTimer     ( int timerId )
   {
     TransientTimerImpl::singleton().timerFired(timerId);
   }

};

namespace
{
  bool registered = EventListener::getInstance().initialise();  
}

EventListener& EventListener::getInstance()
{
  static EventListener singleton;
  return singleton;
}

bool EventListener::initialise()
{
  registerMonitor();
  connectToMonitor();
  return true;
}




#define BOOST_TEST_MODULE  Timer_Test_Module

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_SUITE( Timer_Test_Suite );

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_CASE( Timer_Test_Suite_testcase_1 ) 
{ 
    SWA::Process::getInstance().initialise();
   // The registered Timer Impl class above is configured to return a pending timer
   // when it is initialised. So even though these tests create a series of timers
   // there should be one timer already active on the timer queue.

   SWA::Domain& testDomain = ::SWA::Process::getInstance().addDomain("testDomain");
   testDomain.addTimerEventGenerator(0,dummyEventGenerator);


   ::SWA::Timer& timer = ::SWA::Timer::getInstance();
   uint32_t timer1 = timer.createTimer();
   uint32_t timer2 = timer.createTimer();

   BOOST_CHECK_EQUAL(timer1,2);
   BOOST_CHECK_EQUAL(timer2,3);

   BOOST_CHECK_THROW(timer.deleteTimer(5), ::SWA::Exception ); 
   timer.deleteTimer(timer2);

   timer2 = timer.createTimer();
   BOOST_CHECK_EQUAL(timer2,4);

   // Validate that a call to cancel and getTimeRemaining 
   // throws an exception for invalid timer ids.
   BOOST_CHECK_THROW(timer.cancelTimer(10),      ::SWA::Exception );
   BOOST_CHECK_THROW(timer.getTimeRemaining(10), ::SWA::Exception );


   timer.cancelTimer(timer1);
   // Validate that getTimeRemaining throws exception when 
   // timer has been cancelled.
   BOOST_CHECK_THROW(timer.getTimeRemaining(timer1), ::SWA::Exception );

   // Validate that getTimeRemaining throws exception when 
   // timer has not been scheduled.
   BOOST_CHECK_THROW(timer.getTimeRemaining(timer2), ::SWA::Exception );
   timer.cancelTimer(timer2);

   boost::shared_ptr< ::SWA::Event > testEvent( new TestTimerEvent);

   // Validate that scheduling an invalid timer causes exception
   BOOST_CHECK_THROW(timer.scheduleTimer(10,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent), ::SWA::Exception);

   timer.scheduleTimer(timer2,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent);

   // Validate that the following methods all work for a scheduled timer.
   timer.getTimeRemaining(timer2);
   timer.cancelTimer(timer2);
   timer.deleteTimer(timer2);

   // The registered Timer Impl class above will have created a pending 
   // timer as part of its intialisation. Polling the activity monitor 
   // should therefore enable this timer to be fired.
   sleep(2);
   bool activityFound = ::SWA::Process::getInstance().getActivityMonitor().pollActivity();
   BOOST_CHECK_EQUAL(activityFound,true);
   BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers().size(), 1);
   BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[0],1);
   

   // Create several timers and validate that they are fired in the correct order.
   uint32_t timer3 = timer.createTimer();
   uint32_t timer4 = timer.createTimer();
   uint32_t timer5 = timer.createTimer();

   boost::shared_ptr< ::SWA::Event > testEvent3(new TestTimerEvent);
   boost::shared_ptr< ::SWA::Event > testEvent4(new TestTimerEvent);
   boost::shared_ptr< ::SWA::Event > testEvent5(new TestTimerEvent);
   boost::shared_ptr< ::SWA::Event > testEvent6(new TestTimerEvent);

   timer.scheduleTimer(timer3,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent3);
   timer.scheduleTimer(timer5,SWA::Time::now() + SWA::Time(3,SWA::Time::SECONDS), testEvent5);
   timer.scheduleTimer(timer4,SWA::Time::now() + SWA::Time(2,SWA::Time::SECONDS), testEvent4);

   // Let all the timers expire and then make sure 
   // that they are processed in the correct order.
   sleep(4);

   {
      uint32_t eventCount = 0;
      while(SWA::Timer::getInstance().getNoTimersQueued() > 0 )
      {
        ::SWA::Process::getInstance().getActivityMonitor().pollActivity();
      }

      BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers().size(), 4);
      BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[1],timer3);
      BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[2],timer4);
      BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[3],timer5);

      BOOST_CHECK_NO_THROW(timer.getTimeRemaining(timer3));
      BOOST_CHECK_NO_THROW(timer.getTimeRemaining(timer4));
      BOOST_CHECK_NO_THROW(timer.getTimeRemaining(timer5));

   }

   // Validate that a timer must be cancelled before being re-scheduled.
   timer.cancelTimer(timer3);
   BOOST_CHECK_THROW(timer.getExpiry(timer3), ::SWA::Exception);
   timer.scheduleTimer(timer3,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent3);
   timer.getExpiry(timer3);

   timer.deleteTimer(timer4);

   // Check that a timer can be 
   // re-scheduled before it has been cancelled.
   timer.scheduleTimer(timer5,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent5);

   // having deleted some of the timers and rescheduled others let all 
   // the timers expire and then make sure that they are processed in 
   // the correct order.
   sleep(2);

   {   
     while(SWA::Timer::getInstance().getNoTimersQueued() > 0 )
     {
       ::SWA::Process::getInstance().getActivityMonitor().pollActivity();
     }

     BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers().size(), 6);
     BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[4],timer3);
     BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[5],timer5);

   } 

   timer.deleteTimer(1);
   timer.deleteTimer(timer1);
   timer.deleteTimer(timer3);
   timer.deleteTimer(timer5);

   // Deleting all timers will have caused the timer impl to have 
   // been suspended. Therefore make sure a new timer fires O.K

   ::SWA::Timer::TimerIdType timer6 = timer.createTimer();
   timer.scheduleTimer(timer6,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent6);

   {   
     while(SWA::Timer::getInstance().getNoTimersQueued() > 0 )
     {
       ::SWA::Process::getInstance().getActivityMonitor().pollActivity();
     }


     BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers().size(), 7);
     BOOST_CHECK_EQUAL(TransientTimerImpl::singleton().getFiredTimers()[6],timer6);

   } 

   timer.deleteTimer(timer6);
 
}

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_CASE( Timer_Test_Suite_testcase_2) 
{ 
   ::SWA::Timer& timer = ::SWA::Timer::getInstance();
   
   // create and schedule the timer
   uint32_t timer1 = timer.createTimer();

   boost::shared_ptr< ::SWA::Event > testEvent( new TestTimerEvent);
   timer.scheduleTimer(timer1,SWA::Time::now() + SWA::Time(1,SWA::Time::SECONDS), testEvent);

   // let the processing loop service the timer implementation, 
   // until the timer has been fired.
   while(SWA::Timer::getInstance().getNoTimersQueued() > 0 )
   {
      ::SWA::Process::getInstance().getActivityMonitor().pollActivity();
   }

   // Check that the timer has expired. 
   BOOST_CHECK_EQUAL(timer.isFired(timer1),true);

   timer.deleteTimer(timer1);

}

// *************************************************************************************
// *************************************************************************************
BOOST_AUTO_TEST_SUITE_END();
