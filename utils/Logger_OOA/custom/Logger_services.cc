//
// File: NativeStubs.cc
//
// UK Crown Copyright (c) 2010. All Rights Reserved
//
#include "Logger_OOA/__Logger_services.hh"
#include "Logger_OOA/__Logger_types.hh"
#include "swa/String.hh"
#include "logging/log.hh"

using namespace xtuml::logging;

namespace masld_Logger
{
  const bool localServiceRegistration_masls_log = interceptor_masls_log::instance().registerLocal( &masls_log );
  const bool localServiceRegistration_masls_trace = interceptor_masls_trace::instance().registerLocal( &masls_trace );
  const bool localServiceRegistration_masls_debug = interceptor_masls_debug::instance().registerLocal( &masls_debug );
  const bool localServiceRegistration_masls_information = interceptor_masls_information::instance().registerLocal( &masls_information );
  const bool localServiceRegistration_masls_warning = interceptor_masls_warning::instance().registerLocal( &masls_warning );
  const bool localServiceRegistration_masls_error = interceptor_masls_error::instance().registerLocal( &masls_error );
  const bool localServiceRegistration_masls_fatal = interceptor_masls_fatal::instance().registerLocal( &masls_fatal );
  const bool localServiceRegistration_masls_setLogLevel = interceptor_masls_setLogLevel::instance().registerLocal( &masls_setLogLevel );

  void masls_log ( const maslt_Priority& maslp_priority,
                             const ::SWA::String&  maslp_logger,
                             const ::SWA::String&  maslp_message )
  {
    switch ( maslp_priority.getIndex() )
    {
      case maslt_Priority::index_masle_Trace      : masls_trace      (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Debug      : masls_debug      (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Information: masls_information(maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Warning    : masls_warning    (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Error      : masls_error      (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Fatal      : masls_fatal      (maslp_logger,maslp_message); break;
    }
  }


  void masls_trace ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.trace(fmt::runtime(maslp_message.s_str()));
  }

  void masls_debug ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.debug (fmt::runtime(maslp_message.s_str()) );
  }


  void masls_information ( const ::SWA::String& maslp_logger,
                                     const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.info (fmt::runtime(maslp_message.s_str()) );
  }

  void masls_warning ( const ::SWA::String& maslp_logger,
                                 const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.warn (fmt::runtime(maslp_message.s_str()) );
  }

  void masls_error ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.error (fmt::runtime(maslp_message.s_str()) );
  }

  void masls_fatal ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.fatal (fmt::runtime(maslp_message.s_str()) );

  }
  
  void masls_setLogLevel ( const ::SWA::String&  maslp_logger,
                                     const maslt_Priority& maslp_priority )
  {
    switch ( maslp_priority.getIndex() )
    {
      case maslt_Priority::index_masle_Trace      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::TRACE ); break;
      case maslt_Priority::index_masle_Debug      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::DEBUG ); break;
      case maslt_Priority::index_masle_Information: Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::INFO  ); break;
      case maslt_Priority::index_masle_Warning    : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::WARN  ); break;
      case maslt_Priority::index_masle_Error      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::ERROR ); break;
      case maslt_Priority::index_masle_Fatal      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::FATAL ); break;
    }
  }

bool masls_enabled ( const maslt_Priority& maslp_priority, const ::SWA::String&  maslp_logger ) {
    switch ( maslp_priority.getIndex() )
    {
        case maslt_Priority::index_masle_Trace      : return Logger{fmt::runtime(maslp_logger.s_str())}.trace_enabled(); break;
        case maslt_Priority::index_masle_Debug      : return Logger{fmt::runtime(maslp_logger.s_str())}.debug_enabled(); break;
        case maslt_Priority::index_masle_Information: return Logger{fmt::runtime(maslp_logger.s_str())}.info_enabled();  break;
        case maslt_Priority::index_masle_Warning    : return Logger{fmt::runtime(maslp_logger.s_str())}.warn_enabled();  break;
        case maslt_Priority::index_masle_Error      : return Logger{fmt::runtime(maslp_logger.s_str())}.error_enabled(); break;
        case maslt_Priority::index_masle_Fatal      : return Logger{fmt::runtime(maslp_logger.s_str())}.fatal_enabled(); break;
    }

}

  bool masls_traceEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.trace_enabled(); }
  bool masls_debugEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.debug_enabled(); }
  bool masls_informationEnabled(const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.info_enabled();  }
  bool masls_warningEnabled    (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.warn_enabled();  }
  bool masls_errorEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.error_enabled(); }
  bool masls_fatalEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.fatal_enabled(); }

}
