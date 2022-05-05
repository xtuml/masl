// (C) 2022 - UK CROWN OWNED COPYRIGHT. All rights reserved.
// The copyright of this Software is vested in the Crown and the Software is the property of the Crown.

#include <stdint.h>
#include "Test_OOA/__Test_services.hh"
#include "swa/Process.hh"
#include "swa/Duration.hh"
#include "swa/Timestamp.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"

namespace masld_Test
{
  const bool localServiceRegistration_masls_service_event_queue = interceptor_masls_service_event_queue::instance().registerLocal( &masls_service_event_queue );

  void masls_service_event_queue ( )
  {
    ::SWA::Process::getInstance().getEventQueue().processEvents();
  }

}
