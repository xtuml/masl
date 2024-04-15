//
// File: NativeStubs.cc
//
// UK Crown Copyright (c) 2010. All Rights Reserved
//
#include "Logger_OOA/__Logger_services.hh"
#include "Logger_OOA/__Logger_types.hh"
#include "Format_OOA/__Format_services.hh"
#include "Format_OOA/__Format_types.hh"
#include "swa/String.hh"
#include "swa/Stack.hh"
#include "swa/NameFormatter.hh"
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

  Logger::Level get_level( const maslt_Priority& maslp_priority) {
      const static std::array<Logger::Level, 6> level_lookup = {
              Logger::Level::FATAL,
              Logger::Level::ERROR,
              Logger::Level::WARN,
              Logger::Level::INFO,
              Logger::Level::DEBUG,
              Logger::Level::TRACE,
      };

      return level_lookup[static_cast<std::size_t>(maslp_priority.getIndex())];
  }

  void masls_log ( const maslt_Priority& maslp_priority,
                             const ::SWA::String&  maslp_logger,
                             const ::SWA::String&  maslp_message )
  {
      auto logger =   Logger{fmt::runtime(maslp_logger.s_str())};
      const auto& stack_frame = ::SWA::Stack::getInstance().top();
      auto file_name = ::SWA::NameFormatter::formatFileName(stack_frame);
      auto name = ::SWA::NameFormatter::formatStackFrame(stack_frame,false);
      logger.log_raw(get_level(maslp_priority),maslp_message.s_str(),file_name,stack_frame.getLine(),name);
  }
    void masls_overload1_log ( const maslt_Priority& maslp_priority,
                               const ::SWA::String&  maslp_logger,
                               const ::SWA::String&  maslp_message,
                                const masld_Format::maslt_Arguments& maslp_args )
  {
      auto logger =  Logger{fmt::runtime(maslp_logger.s_str())};
      if ( logger.enabled(get_level(maslp_priority)) ) {
        masls_log(maslp_priority,maslp_logger, masld_Format::masls_format(maslp_message,maslp_args));
      }
  }

  void masls_trace ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      masls_log(maslt_Priority::masle_Trace,maslp_logger, maslp_message);
  }

    void masls_overload1_trace ( const ::SWA::String& maslp_logger,
                                const ::SWA::String& maslp_message,
                               const masld_Format::maslt_Arguments& maslp_args )
  {
      masls_overload1_log(maslt_Priority::masle_Trace,maslp_logger, maslp_message, maslp_args );
  }

    void masls_debug ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      masls_log(maslt_Priority::masle_Debug,maslp_logger, maslp_message);
  }

    void masls_overload1_debug ( const ::SWA::String& maslp_logger,
                                const ::SWA::String& maslp_message,
                                const masld_Format::maslt_Arguments& maslp_args )
  {
      masls_overload1_log(maslt_Priority::masle_Debug,maslp_logger, maslp_message, maslp_args);
  }


  void masls_information ( const ::SWA::String& maslp_logger,
                                     const ::SWA::String& maslp_message )
  {
      masls_log(maslt_Priority::masle_Information,maslp_logger, maslp_message);
  }

    void masls_overload1_information ( const ::SWA::String& maslp_logger,
                                       const ::SWA::String& maslp_message,
                                       const masld_Format::maslt_Arguments& maslp_args )
  {
      masls_overload1_log(maslt_Priority::masle_Information,maslp_logger, maslp_message, maslp_args);
  }

    void masls_warning ( const ::SWA::String& maslp_logger,
                                 const ::SWA::String& maslp_message )
  {
      masls_log(maslt_Priority::masle_Warning,maslp_logger, maslp_message);
  }

    void masls_overload1_warning ( const ::SWA::String& maslp_logger,
                                       const ::SWA::String& maslp_message,
                                       const masld_Format::maslt_Arguments& maslp_args )
  {
      masls_overload1_log(maslt_Priority::masle_Warning,maslp_logger, maslp_message, maslp_args);
  }

  void masls_error ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      masls_log(maslt_Priority::masle_Error,maslp_logger, maslp_message);
  }

    void masls_overload1_error ( const ::SWA::String& maslp_logger,
                                       const ::SWA::String& maslp_message,
                                       const masld_Format::maslt_Arguments& maslp_args )
  {
      masls_overload1_log(maslt_Priority::masle_Error,maslp_logger, maslp_message, maslp_args);
  }

    void masls_fatal ( const ::SWA::String& maslp_logger,
                               const ::SWA::String& maslp_message )
  {
      masls_log(maslt_Priority::masle_Fatal,maslp_logger, maslp_message);

  }
    void masls_overload1_fatal ( const ::SWA::String& maslp_logger,
                                       const ::SWA::String& maslp_message,
                                       const masld_Format::maslt_Arguments& maslp_args )
  {
      masls_overload1_log(maslt_Priority::masle_Fatal,maslp_logger, maslp_message, maslp_args );

  }
  void masls_setLogLevel ( const ::SWA::String&  maslp_logger,
                                     const maslt_Priority& maslp_priority )
  {
      Logger{fmt::runtime(maslp_logger.s_str())}.set_level(get_level(maslp_priority));
  }

  bool masls_enabled ( const maslt_Priority& maslp_priority, const ::SWA::String&  maslp_logger ) {
    return Logger{fmt::runtime(maslp_logger.s_str())}.enabled(get_level(maslp_priority));
  }

  bool masls_traceEnabled      (const ::SWA::String&  maslp_logger) { return masls_enabled(maslt_Priority::masle_Trace, maslp_logger); }
  bool masls_debugEnabled      (const ::SWA::String&  maslp_logger) { return masls_enabled(maslt_Priority::masle_Debug, maslp_logger); }
  bool masls_informationEnabled(const ::SWA::String&  maslp_logger) { return masls_enabled(maslt_Priority::masle_Information, maslp_logger);  }
  bool masls_warningEnabled    (const ::SWA::String&  maslp_logger) { return masls_enabled(maslt_Priority::masle_Warning, maslp_logger);  }
  bool masls_errorEnabled      (const ::SWA::String&  maslp_logger) { return masls_enabled(maslt_Priority::masle_Error, maslp_logger); }
  bool masls_fatalEnabled      (const ::SWA::String&  maslp_logger) { return masls_enabled(maslt_Priority::masle_Fatal, maslp_logger); }

}
