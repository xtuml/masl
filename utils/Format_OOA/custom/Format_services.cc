//
// UK Crown Copyright (c) 2009. All Rights Reserved
//
#include <stdint.h>
#include <sstream>
#include <iomanip>
#include "Format_OOA/__Format_services.hh"
#include "Format_OOA/__Format_types.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"

namespace masld_Format
{
  uint8_t masls_to_ascii ( char maslp_input )
  {
    return static_cast<uint8_t>(maslp_input);
  }

  ::SWA::Sequence<uint8_t> masls_overload1_to_ascii ( const ::SWA::String& maslp_input )
  {
    return ::SWA::Sequence<uint8_t>(maslp_input.begin(),maslp_input.end());
  }

  char masls_from_ascii ( uint8_t maslp_input )
  {
    return maslp_input;
  }

  ::SWA::String masls_overload1_from_ascii ( const ::SWA::Sequence<uint8_t>& maslp_input )
  {
    return SWA::String(maslp_input.begin(),maslp_input.end());
  }


  class justify
  {
    public:
      justify ( const maslt_justify& justification ) : justification(justification) {}
      
      friend std::ostream& operator<< ( std::ostream& stream, const justify& j ); 

    private:
      maslt_justify justification;
  };

  std::ostream& operator<< ( std::ostream& stream, const justify& j )
  {
    switch ( j.justification.getIndex() )
    {
      case maslt_justify::index_masle_left:     return stream << std::left;
      case maslt_justify::index_masle_right:    return stream << std::right;
      case maslt_justify::index_masle_internal: return stream << std::internal;
    }
    return stream;
  }

  ::SWA::String masls_format_integer ( int64_t maslp_input,
                                       bool    maslp_show_positive )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos ) << maslp_input;
    return result.str();
  }

  ::SWA::String masls_overload1_format_integer ( int64_t              maslp_input,
                                                 bool                 maslp_show_positive,
                                                 const maslt_justify& maslp_justification,
                                                 int32_t              maslp_width,
                                                 char                 maslp_pad )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << justify(maslp_justification)
           << std::setw(maslp_width)
           << std::setfill(maslp_pad)
           << maslp_input;
    return result.str();
  }

  std::string getBasedInteger ( int64_t                value,
                                int32_t                maslp_base,
                                const maslt_base_case& maslp_base_case )
  {

    static const std::string upperCaseChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static const std::string lowerCaseChars = "0123456789abcdefghijklmnopqrstuvwxyz";

    if ( maslp_base < 2 || maslp_base > 36 ) throw SWA::ProgramError("radix out of range" );

    const std::string& baseChars = maslp_base_case == maslt_base_case::masle_upper?upperCaseChars:lowerCaseChars;

    std::string backwards; 

    do
    {
      int32_t placeValue = value % maslp_base;
      value /= maslp_base;
      backwards.push_back(baseChars[placeValue]);
    }
    while(value);
  
    return std::string(backwards.rbegin(),backwards.rend());
  }

  ::SWA::String masls_format_based_integer ( int64_t                maslp_input,
                                             bool                   maslp_show_positive,
                                             int32_t                maslp_base,
                                             const maslt_base_case& maslp_base_case )
  {
    return (maslp_input < 0 ? "-" : (maslp_show_positive ? "+" : "" )) +
           getBasedInteger(std::abs(maslp_input),maslp_base, maslp_base_case);
  }

  ::SWA::String masls_overload1_format_based_integer ( int64_t                maslp_input,
                                                       bool                   maslp_show_positive,
                                                       int32_t                maslp_base,
                                                       const maslt_base_case& maslp_base_case,
                                                       const maslt_justify&   maslp_justification,
                                                       int32_t                maslp_width,
                                                       char                   maslp_pad )
  {
    ::SWA::String value = getBasedInteger(std::abs(maslp_input),maslp_base, maslp_base_case);
    ::SWA::String sign =  (maslp_input < 0 ? "-" : (maslp_show_positive ? "+" : "" ));
    if ( maslp_justification == maslt_justify::masle_internal && ( maslp_input < 0 || maslp_show_positive ) )
    {
      return sign + masls_format_string ( value, maslp_justification, maslp_width-1, maslp_pad );
    }
    else
    {
      return masls_format_string ( sign + value, maslp_justification, maslp_width, maslp_pad );
    }
  }

  ::SWA::String masls_format_number ( double  maslp_input,
                                      bool    maslp_show_positive,
                                      int32_t maslp_sigfigs )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::setprecision(maslp_sigfigs)
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_overload1_format_number ( double               maslp_input,
                                                bool                 maslp_show_positive,
                                                int32_t              maslp_sigfigs,
                                                const maslt_justify& maslp_justification,
                                                int32_t              maslp_width,
                                                char                 maslp_pad )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::setprecision(maslp_sigfigs)
           << justify(maslp_justification)
           << std::setw(maslp_width)
           << std::setfill(maslp_pad)
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_format_decimal ( double  maslp_input,
                                       bool    maslp_show_positive,
                                       int32_t maslp_sigfigs )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::setprecision(maslp_sigfigs)
           << std::showpoint
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_overload1_format_decimal ( double               maslp_input,
                                                 bool                 maslp_show_positive,
                                                 int32_t              maslp_sigfigs,
                                                 const maslt_justify& maslp_justification,
                                                 int32_t              maslp_width,
                                                 char                 maslp_pad )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::setprecision(maslp_sigfigs)
           << std::showpoint
           << justify(maslp_justification)
           << std::setw(maslp_width)
           << std::setfill(maslp_pad)
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_format_scientific ( double  maslp_input,
                                          bool    maslp_show_positive,
                                          int32_t maslp_sigfigs )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::scientific
           << std::setprecision(maslp_sigfigs-1)
           << std::showpoint
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_overload1_format_scientific ( double               maslp_input,
                                                    bool                 maslp_show_positive,
                                                    int32_t              maslp_sigfigs,
                                                    const maslt_justify& maslp_justification,
                                                    int32_t              maslp_width,
                                                    char                 maslp_pad )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::setprecision(maslp_sigfigs-1)
           << std::scientific
           << std::showpoint
           << justify(maslp_justification)
           << std::setw(maslp_width)
           << std::setfill(maslp_pad)
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_format_fixed ( double  maslp_input,
                                     bool    maslp_show_positive,
                                     int32_t maslp_places )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::fixed
           << std::setprecision(maslp_places)
           << std::showpoint
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_overload1_format_fixed ( double               maslp_input,
                                               bool                 maslp_show_positive,
                                               int32_t              maslp_places,
                                               const maslt_justify& maslp_justification,
                                               int32_t              maslp_width,
                                               char                 maslp_pad )
  {
    std::ostringstream result;
    result << ( maslp_show_positive ? std::showpos : std::noshowpos )
           << std::setprecision(maslp_places)
           << std::fixed
           << std::showpoint
           << justify(maslp_justification)
           << std::setw(maslp_width)
           << std::setfill(maslp_pad)
           << maslp_input;
    return result.str();
  }

  ::SWA::String masls_format_boolean ( bool maslp_input )
  {
    return maslp_input?"true":"false";
  }

  ::SWA::String masls_overload1_format_boolean ( bool                 maslp_input,
                                                 const ::SWA::String& maslp_true_text,
                                                 const ::SWA::String& maslp_false_text )
  {
    return maslp_input?maslp_true_text:maslp_false_text;
  }

  ::SWA::String masls_format_string ( const ::SWA::String& maslp_input,
                                      const maslt_justify& maslp_justification,
                                      int32_t              maslp_width,
                                      char                 maslp_pad )
  {
    std::ostringstream result;
    result << justify(maslp_justification)
           << std::setw(maslp_width)
           << std::setfill(maslp_pad)
           << maslp_input;
    return result.str();
  }


  ::SWA::String masls_format_duration_iso ( const ::SWA::Duration&      maslp_input,
                                            const maslt_duration_field& maslp_max_field,
                                            const maslt_duration_field& maslp_min_field,
                                            bool                        maslp_hide_zero,
                                            int32_t                     maslp_places,
                                            bool                        maslp_truncate )
  {
    return maslp_input.format_iso(::SWA::Duration::FormatField(maslp_max_field.getIndex()), ::SWA::Duration::FormatField(maslp_min_field.getIndex()),maslp_hide_zero, maslp_places, maslp_truncate );
  }

  ::SWA::String masls_format_duration_hms ( const ::SWA::Duration& maslp_input,
                                            const maslt_duration_field& maslp_min_field,
                                            int32_t                maslp_places,
                                            bool                   maslp_truncate )
  {
    return maslp_input.format_hms(::SWA::Duration::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate );
  }

  ::SWA::String masls_format_duration ( const ::SWA::Duration&                 maslp_input,
                                        const maslt_duration_field&            maslp_max_field,
                                        const maslt_duration_field&            maslp_min_field,
                                        const maslt_rounding&                  maslp_rounding_mode,
                                        bool                                   maslp_hide_zero,
                                        int32_t                                maslp_places,
                                        bool                                   maslp_truncate,
                                        int32_t                                maslp_field_width,
                                        const ::SWA::String&                   maslp_prefix,
                                        const ::SWA::String&                   maslp_time_prefix,
                                        const ::SWA::Sequence< ::SWA::String>& maslp_suffixes )
  {
    return maslp_input.format(::SWA::Duration::FormatField(maslp_max_field.getIndex()), ::SWA::Duration::FormatField(maslp_min_field.getIndex()), SWA::Duration::Rounding(maslp_rounding_mode.getIndex()), maslp_hide_zero, maslp_places, maslp_truncate, maslp_field_width, maslp_prefix, maslp_time_prefix, maslp_suffixes );
  }

  ::SWA::String masls_format_timestamp_iso_ymdhms ( const ::SWA::Timestamp&      maslp_input,
                                                    const maslt_timestamp_field& maslp_min_field,
                                                    int32_t                      maslp_places,
                                                    bool                         maslp_truncate )
  {
    return maslp_input.format_iso_ymdhms ( ::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, false );
  }

  ::SWA::String masls_format_timestamp_iso_ydhms ( const ::SWA::Timestamp&      maslp_input,
                                                   const maslt_timestamp_field& maslp_min_field,
                                                   int32_t                      maslp_places,
                                                   bool                         maslp_truncate )
  {
    return maslp_input.format_iso_ydhms ( ::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, false );
  }

  ::SWA::String masls_format_timestamp_iso_ywdhms ( const ::SWA::Timestamp&      maslp_input,
                                                    const maslt_timestamp_field& maslp_min_field,
                                                    int32_t                      maslp_places,
                                                    bool                         maslp_truncate )
  {
    return maslp_input.format_iso_ywdhms ( ::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, false );
  }

  ::SWA::String masls_format_timestamp_compact_iso_ymdhms ( const ::SWA::Timestamp&      maslp_input,
                                                            const maslt_timestamp_field& maslp_min_field,
                                                            int32_t                      maslp_places,
                                                            bool                         maslp_truncate )
  {
    return maslp_input.format_iso_ymdhms ( ::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, true );
  }

  ::SWA::String masls_format_timestamp_compact_iso_ydhms ( const ::SWA::Timestamp&      maslp_input,
                                                           const maslt_timestamp_field& maslp_min_field,
                                                           int32_t                      maslp_places,
                                                           bool                         maslp_truncate )
  {
    return maslp_input.format_iso_ydhms ( ::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, true );
  }

  ::SWA::String masls_format_timestamp_compact_iso_ywdhms ( const ::SWA::Timestamp&      maslp_input,
                                                            const maslt_timestamp_field& maslp_min_field,
                                                            int32_t                      maslp_places,
                                                            bool                         maslp_truncate )
  {
    return maslp_input.format_iso_ywdhms ( ::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, true );
  }

  ::SWA::String masls_format_timestamp_dmy ( const ::SWA::Timestamp&      maslp_input )
  {
    return maslp_input.format_dmy();
  }

  ::SWA::String masls_format_timestamp_mdy ( const ::SWA::Timestamp&      maslp_input )
  {
    return maslp_input.format_mdy();
  }

  ::SWA::String masls_format_timestamp_dtg ( const ::SWA::Timestamp&      maslp_input )
  {
    return maslp_input.format_dtg();
  }

  ::SWA::String masls_format_timestamp_time ( const ::SWA::Timestamp&      maslp_input,
                                              const maslt_timestamp_field& maslp_min_field,
                                              int32_t                      maslp_places,
                                              bool                         maslp_truncate )
  {
    return maslp_input.format_time(::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, false );
  }

  ::SWA::String masls_format_timestamp_compact_time ( const ::SWA::Timestamp&      maslp_input,
                                                      const maslt_timestamp_field& maslp_min_field,
                                                      int32_t                      maslp_places,
                                                      bool                         maslp_truncate )
  {
    return maslp_input.format_time(::SWA::Timestamp::FormatField(maslp_min_field.getIndex()), maslp_places, maslp_truncate, true );
  }


}
