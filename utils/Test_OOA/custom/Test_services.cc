// (C) 2022 - UK CROWN OWNED COPYRIGHT. All rights reserved.
// The copyright of this Software is vested in the Crown and the Software is the property of the Crown.

#include <stdint.h>
#include "Test_OOA/__Test_services.hh"
#include "swa/Process.hh"
#include "swa/Duration.hh"
#include "swa/Timestamp.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include "swa/EventTimers.hh"
#include "swa/ProgramError.hh"

namespace masld_Test
{
  const bool localServiceRegistration_masls_service_event_queue = interceptor_masls_service_event_queue::instance().registerLocal( &masls_service_event_queue );
  const bool localServiceRegistration_masls_fire_timer = interceptor_masls_fire_timer::instance().registerLocal( &masls_fire_timer );

  void masls_service_event_queue ( )
  {
    try
    {
      ::SWA::Process::getInstance().getEventQueue().processEvents();
    }
    catch ( const ::SWA::Exception& )
    {
      throw;
    }
    catch ( const std::exception& e )
    {
      throw ::SWA::ProgramError( "Failed to service event queue:\nCaused by:\n" + std::string( e.what() ) );
    }
  }

  ::SWA::Sequence< ::SWA::EventTimers::TimerIdType> masls_get_scheduled_timers ( )
  {
    ::SWA::Sequence< ::SWA::EventTimers::TimerIdType> result;
    ::SWA::EventTimers::QueuedEvents timers = ::SWA::EventTimers::getInstance().getQueuedEvents();
    for ( ::SWA::EventTimers::QueuedEvents::iterator it = timers.begin(), end = timers.end(); it != end; ++it )
    {
      result.push_back((*it)->getId());
    }
    return result;
  }

  void masls_fire_timer ( const ::SWA::EventTimers::TimerIdType& maslp_t )
  {
    ::SWA::EventTimers::getInstance().fireTimer(maslp_t, 0);
  }

}
