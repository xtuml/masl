//
// UK Crown Copyright (c) 2010. All Rights Reserved
//
#include "CommandLine_OOA/__CommandLine_services.hh"
#include "CommandLine_OOA/__CommandLine_types.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include "swa/CommandLine.hh"

namespace masld_CommandLine
{
  const bool localServiceRegistration_masls_register_flag = interceptor_masls_register_flag::instance().registerLocal( &masls_register_flag );
  const bool localServiceRegistration_masls_register_value = interceptor_masls_register_value::instance().registerLocal( &masls_register_value );

  void masls_register_flag ( const ::SWA::String& maslp_option,
                             const ::SWA::String& maslp_usage_text )
  {
    SWA::CommandLine::getInstance().registerOption ( SWA::NamedOption ( maslp_option.s_str(), maslp_usage_text.s_str() ) );
  }

  void masls_register_value ( const ::SWA::String&        maslp_option,
                              const ::SWA::String&        maslp_usage_text,
                              const maslt_Conditionality& maslp_option_type,
                              const ::SWA::String&        maslp_value_name,
                              const maslt_Conditionality& maslp_value_type,
                              const maslt_Multiplicity&   maslp_multiplicity )
  {
    SWA::CommandLine::getInstance().registerOption ( 
        SWA::NamedOption ( 
          maslp_option.s_str(), 
          maslp_usage_text.s_str(), 
          maslp_option_type == maslt_Conditionality::masle_Required,
          maslp_value_name.s_str(),
          maslp_value_type == maslt_Conditionality::masle_Required,
          maslp_multiplicity == maslt_Multiplicity::masle_Multiple ) );
  }

  bool masls_option_present ( const ::SWA::String& maslp_option )
  {
    return SWA::CommandLine::getInstance().optionPresent( maslp_option.s_str() );
  }

  ::SWA::String masls_get_option_value ( const ::SWA::String& maslp_option )
  {
    return SWA::CommandLine::getInstance().getOption( maslp_option.s_str() );
  }

  ::SWA::String masls_overload1_get_option_value ( const ::SWA::String& maslp_option,
                                                   const ::SWA::String& maslp_default )
  {
    return SWA::CommandLine::getInstance().getOption( maslp_option.s_str(), maslp_default.s_str()  );
  }

  ::SWA::Sequence< ::SWA::String> masls_get_option_values ( const ::SWA::String& maslp_option )
  {
    return ::SWA::Sequence< ::SWA::String>(SWA::CommandLine::getInstance().getMultiOption(maslp_option.s_str()));
  }

  ::SWA::String masls_get_command ( )
  {
    return SWA::CommandLine::getInstance().getCommand();
  }

}
