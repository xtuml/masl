//
// File: Schedule_services.cc
//
#include "Schedule_OOA/__Schedule_services.hh"
#include "Schedule_OOA/__Schedule_types.hh"
#include <stdint.h>
#include "swa/String.hh"

#include "swa/Process.hh"
#include "swa/ProgramError.hh"
#include <format>

namespace masld_Schedule
{

  const bool localServiceRegistration_masls_run_service = interceptor_masls_run_service::instance().registerLocal( &masls_run_service );
  const bool localServiceRegistration_masls_overload1_run_service = interceptor_masls_overload1_run_service::instance().registerLocal( &masls_overload1_run_service );
  const bool localServiceRegistration_masls_idle = interceptor_masls_idle::instance().registerLocal( &masls_idle );
  const bool localServiceRegistration_masls_pause = interceptor_masls_pause::instance().registerLocal( &masls_pause );
  const bool localServiceRegistration_masls_terminate = interceptor_masls_terminate::instance().registerLocal( &masls_terminate );

  void masls_run_service ( const maslt_ServiceType& maslp_service_type,
                           const ::SWA::String&     maslp_domain_name,
                           int32_t                  maslp_number )
  {
    masls_overload1_run_service(maslp_service_type, maslp_domain_name, maslp_number, ::SWA::String(""));
  }


  void masls_overload1_run_service ( const maslt_ServiceType& maslp_service_type,
                                     const ::SWA::String&     maslp_domain_name,
                                     int32_t                  maslp_number,
                                     const ::SWA::String&     maslp_input )
  {
    const ::SWA::Domain& domain = ::SWA::Process::getInstance().getDomain(maslp_domain_name);
    try
    {
      if ( maslp_service_type == maslt_ServiceType::masle_SCENARIO )
      {
        ::SWA::Process::getInstance().runService(domain.getScenario(maslp_number), maslp_input);
      }
      else
      {
        ::SWA::Process::getInstance().runService(domain.getExternal(maslp_number), maslp_input);
      }
    }
    catch ( const ::SWA::Exception& )
    {
      throw;
    }
    catch ( const std::exception& e )
    {
      throw ::SWA::ProgramError( std::format("Failed to run service: {}", e.what()) );
    }
  }

  void masls_idle ( int32_t maslp_timeout )
  {
    try
    {
      ::SWA::Process::getInstance().idle(maslp_timeout);
    }
    catch ( const ::SWA::Exception& )
    {
      throw;
    }
    catch ( const std::exception& e )
    {
        throw ::SWA::ProgramError(std::format( "Exception during idle: {}", e.what()) );
      }
    }

  void masls_pause ( )
  {
    try
    {
      ::SWA::Process::getInstance().pause();
    }
    catch ( const ::SWA::Exception& )
    {
      throw;
    }
    catch ( const std::exception& e )
    {
      throw ::SWA::ProgramError( std::format("Exception during timeout: {}",e.what()) );
    }
  }

  void masls_terminate ( )
  {
    try
    {
      ::SWA::Process::getInstance().forceTerminate();
    }
    catch ( const ::SWA::Exception& )
    {
      throw;
    }
    catch ( const std::exception& e )
    {
      throw ::SWA::ProgramError( std::format("Failed to terminate: {}",e.what()) );
    }
  }

}
