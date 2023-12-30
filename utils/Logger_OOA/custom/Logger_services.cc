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
  const bool localServiceRegistration_masls_overload1_log = interceptor_masls_overload1_log::instance().registerLocal( &masls_overload1_log );
  const bool localServiceRegistration_masls_trace = interceptor_masls_trace::instance().registerLocal( &masls_trace );
  const bool localServiceRegistration_masls_overload1_trace = interceptor_masls_overload1_trace::instance().registerLocal( &masls_overload1_trace );
  const bool localServiceRegistration_masls_debug = interceptor_masls_debug::instance().registerLocal( &masls_debug );
  const bool localServiceRegistration_masls_overload1_debug = interceptor_masls_overload1_debug::instance().registerLocal( &masls_overload1_debug );
  const bool localServiceRegistration_masls_information = interceptor_masls_information::instance().registerLocal( &masls_information );
  const bool localServiceRegistration_masls_overload1_information = interceptor_masls_overload1_information::instance().registerLocal( &masls_overload1_information );
  const bool localServiceRegistration_masls_notice = interceptor_masls_notice::instance().registerLocal( &masls_notice );
  const bool localServiceRegistration_masls_overload1_notice = interceptor_masls_overload1_notice::instance().registerLocal( &masls_overload1_notice );
  const bool localServiceRegistration_masls_warning = interceptor_masls_warning::instance().registerLocal( &masls_warning );
  const bool localServiceRegistration_masls_overload1_warning = interceptor_masls_overload1_warning::instance().registerLocal( &masls_overload1_warning );
  const bool localServiceRegistration_masls_error = interceptor_masls_error::instance().registerLocal( &masls_error );
  const bool localServiceRegistration_masls_overload1_error = interceptor_masls_overload1_error::instance().registerLocal( &masls_overload1_error );
  const bool localServiceRegistration_masls_critical = interceptor_masls_critical::instance().registerLocal( &masls_critical );
  const bool localServiceRegistration_masls_overload1_critical = interceptor_masls_overload1_critical::instance().registerLocal( &masls_overload1_critical );
  const bool localServiceRegistration_masls_fatal = interceptor_masls_fatal::instance().registerLocal( &masls_fatal );
  const bool localServiceRegistration_masls_overload1_fatal = interceptor_masls_overload1_fatal::instance().registerLocal( &masls_overload1_fatal );
  const bool localServiceRegistration_masls_setLogLevel = interceptor_masls_setLogLevel::instance().registerLocal( &masls_setLogLevel );
  const bool localServiceRegistration_masls_overload1_setLogLevel = interceptor_masls_overload1_setLogLevel::instance().registerLocal( &masls_overload1_setLogLevel );

  void masls_log ( const maslt_Priority& maslp_priority,
                   const ::SWA::String&  maslp_message )
  {
    switch ( maslp_priority.getIndex() )
    {
      case maslt_Priority::index_masle_Trace      : masls_trace      (maslp_message); break;
      case maslt_Priority::index_masle_Debug      : masls_debug      (maslp_message); break;
      case maslt_Priority::index_masle_Information: masls_information(maslp_message); break;
      case maslt_Priority::index_masle_Notice     : masls_notice     (maslp_message); break;
      case maslt_Priority::index_masle_Warning    : masls_warning    (maslp_message); break;
      case maslt_Priority::index_masle_Error      : masls_error      (maslp_message); break;
      case maslt_Priority::index_masle_Critical   : masls_critical   (maslp_message); break;
      case maslt_Priority::index_masle_Fatal      : masls_fatal      (maslp_message); break;
    }
  }

  void masls_overload1_log ( const maslt_Priority& maslp_priority,
                             const ::SWA::String&  maslp_logger,
                             const ::SWA::String&  maslp_message )
  {
    switch ( maslp_priority.getIndex() )
    {
      case maslt_Priority::index_masle_Trace      : masls_overload1_trace      (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Debug      : masls_overload1_debug      (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Information: masls_overload1_information(maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Notice     : masls_overload1_notice     (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Warning    : masls_overload1_warning    (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Error      : masls_overload1_error      (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Critical   : masls_overload1_critical   (maslp_logger,maslp_message); break;
      case maslt_Priority::index_masle_Fatal      : masls_overload1_fatal      (maslp_logger,maslp_message); break;
    }
  }


  void masls_trace ( const ::SWA::String& maslp_message )
  {
      Logger{""}.trace(fmt::runtime(maslp_message.s_str()));
  }


  void masls_overload1_trace ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.trace(fmt::runtime(maslp_message.s_str()));
  }

  void masls_debug ( const ::SWA::String& maslp_message )
  {
      Logger{""}.debug( fmt::runtime(maslp_message.s_str()) );
  }


  void masls_overload1_debug ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.debug (fmt::runtime(maslp_message.s_str()) );
  }


  void masls_information ( const ::SWA::String& maslp_message )
  {
      Logger{""}.info( fmt::runtime(maslp_message.s_str()) );
  }

  void masls_overload1_information ( const ::SWA::String& maslp_logger,
                                     const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.info (fmt::runtime(maslp_message.s_str()) );
  }

  void masls_notice ( const ::SWA::String& maslp_message )
  {
      Logger{""}.info( fmt::runtime(maslp_message.s_str()) );
  }

  void masls_overload1_notice ( const ::SWA::String& maslp_logger,
                                const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.info (fmt::runtime(maslp_message.s_str()) );
  }

  void masls_warning ( const ::SWA::String& maslp_message )
  {
      Logger{""}.warn( fmt::runtime(maslp_message.s_str()) );
  }

  void masls_overload1_warning ( const ::SWA::String& maslp_logger,
                                 const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.warn (fmt::runtime(maslp_message.s_str()) );
  }

  void masls_error ( const ::SWA::String& maslp_message )
  {
      Logger{""}.error( fmt::runtime(maslp_message.s_str()) );
  }

  void masls_overload1_error ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.error (fmt::runtime(maslp_message.s_str()) );
  }


  void masls_critical ( const ::SWA::String& maslp_message )
  {
      Logger{""}.error( fmt::runtime(maslp_message.s_str()) );
  }


  void masls_overload1_critical ( const ::SWA::String& maslp_logger,
                                  const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.error (fmt::runtime(maslp_message.s_str()) );
  }


  void masls_fatal ( const ::SWA::String& maslp_message )
  {
      Logger{""}.fatal( fmt::runtime(maslp_message.s_str()) );
  }

  void masls_overload1_fatal ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.fatal (fmt::runtime(maslp_message.s_str()) );

  }

  void masls_setLogLevel ( const maslt_Priority& maslp_priority )
  {
    switch ( maslp_priority.getIndex() )
    {
      case maslt_Priority::index_masle_Trace      : Logger{""}.set_level(Logger::Level::TRACE ); break;
      case maslt_Priority::index_masle_Debug      : Logger{""}.set_level(Logger::Level::DEBUG ); break;
      case maslt_Priority::index_masle_Information: Logger{""}.set_level(Logger::Level::INFO  ); break;
      case maslt_Priority::index_masle_Notice     : Logger{""}.set_level(Logger::Level::INFO  ); break;
      case maslt_Priority::index_masle_Warning    : Logger{""}.set_level(Logger::Level::WARN  ); break;
      case maslt_Priority::index_masle_Error      : Logger{""}.set_level(Logger::Level::ERROR ); break;
      case maslt_Priority::index_masle_Critical   : Logger{""}.set_level(Logger::Level::ERROR ); break;
      case maslt_Priority::index_masle_Fatal      : Logger{""}.set_level(Logger::Level::FATAL ); break;
    }
  }


  void masls_overload1_setLogLevel ( const ::SWA::String&  maslp_logger,
                                     const maslt_Priority& maslp_priority )
  {
    switch ( maslp_priority.getIndex() )
    {
      case maslt_Priority::index_masle_Trace      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::TRACE ); break;
      case maslt_Priority::index_masle_Debug      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::DEBUG ); break;
      case maslt_Priority::index_masle_Information: Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::INFO  ); break;
      case maslt_Priority::index_masle_Notice     : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::INFO  ); break;
      case maslt_Priority::index_masle_Warning    : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::WARN  ); break;
      case maslt_Priority::index_masle_Error      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::ERROR ); break;
      case maslt_Priority::index_masle_Critical   : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::ERROR ); break;
      case maslt_Priority::index_masle_Fatal      : Logger{fmt::runtime(maslp_logger.s_str())}.set_level(Logger::Level::FATAL ); break;
    }
  }

  bool masls_enabled ( const maslt_Priority& maslp_priority ) {
      switch ( maslp_priority.getIndex() )
      {
          case maslt_Priority::index_masle_Trace      : return Logger{""}.trace_enabled(); break;
          case maslt_Priority::index_masle_Debug      : return Logger{""}.debug_enabled(); break;
          case maslt_Priority::index_masle_Information: return Logger{""}.info_enabled();  break;
          case maslt_Priority::index_masle_Notice     : return Logger{""}.info_enabled();  break;
          case maslt_Priority::index_masle_Warning    : return Logger{""}.warn_enabled();  break;
          case maslt_Priority::index_masle_Error      : return Logger{""}.error_enabled(); break;
          case maslt_Priority::index_masle_Critical   : return Logger{""}.error_enabled(); break;
          case maslt_Priority::index_masle_Fatal      : return Logger{""}.fatal_enabled(); break;
      }

  }
  bool masls_traceEnabled      () { return Logger{""}.trace_enabled();  }
  bool masls_debugEnabled      () { return Logger{""}.debug_enabled();  }
  bool masls_informationEnabled() { return Logger{""}.info_enabled();   }
  bool masls_noticeEnabled     () { return Logger{""}.info_enabled();   }
  bool masls_warningEnabled    () { return Logger{""}.warn_enabled();   }
  bool masls_errorEnabled      () { return Logger{""}.error_enabled();  }
  bool masls_criticalEnabled   () { return Logger{""}.error_enabled();  }
  bool masls_fatalEnabled      () { return Logger{""}.fatal_enabled();  }

    bool masls_overload1_enabled ( const maslt_Priority& maslp_priority, const ::SWA::String&  maslp_logger ) {
        switch ( maslp_priority.getIndex() )
        {
            case maslt_Priority::index_masle_Trace      : return Logger{fmt::runtime(maslp_logger.s_str())}.trace_enabled(); break;
            case maslt_Priority::index_masle_Debug      : return Logger{fmt::runtime(maslp_logger.s_str())}.debug_enabled(); break;
            case maslt_Priority::index_masle_Information: return Logger{fmt::runtime(maslp_logger.s_str())}.info_enabled();  break;
            case maslt_Priority::index_masle_Notice     : return Logger{fmt::runtime(maslp_logger.s_str())}.info_enabled();  break;
            case maslt_Priority::index_masle_Warning    : return Logger{fmt::runtime(maslp_logger.s_str())}.warn_enabled();  break;
            case maslt_Priority::index_masle_Error      : return Logger{fmt::runtime(maslp_logger.s_str())}.error_enabled(); break;
            case maslt_Priority::index_masle_Critical   : return Logger{fmt::runtime(maslp_logger.s_str())}.error_enabled(); break;
            case maslt_Priority::index_masle_Fatal      : return Logger{fmt::runtime(maslp_logger.s_str())}.fatal_enabled(); break;
        }

    }

  bool masls_overload1_traceEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.trace_enabled(); }
  bool masls_overload1_debugEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.debug_enabled(); }
  bool masls_overload1_informationEnabled(const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.info_enabled();  }
  bool masls_overload1_noticeEnabled     (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.info_enabled();  }
  bool masls_overload1_warningEnabled    (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.warn_enabled();  }
  bool masls_overload1_errorEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.error_enabled(); }
  bool masls_overload1_criticalEnabled   (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.error_enabled(); }
  bool masls_overload1_fatalEnabled      (const ::SWA::String&  maslp_logger) { return Logger{fmt::runtime(maslp_logger.s_str())}.fatal_enabled(); }

}
