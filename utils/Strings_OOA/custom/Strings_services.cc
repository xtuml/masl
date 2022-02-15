//
// UK Crown Copyright (c) 2009. All Rights Reserved
//
#include <stdint.h>
#include "Strings_OOA/__Strings_services.hh"
#include "swa/Sequence.hh"
#include "swa/Set.hh"
#include "swa/String.hh"

#include <boost/algorithm/string.hpp>
#include "boost/tokenizer.hpp"

namespace
{

  inline char ToUpperCase(char c)
  {
    return std::toupper(c);
  }

  inline char ToLowerCase(char c)
  {
    return std::tolower(c);
  }

  inline bool IsSpace(char c)
  {
    return std::isspace(c);
  }

  inline bool BothSpace(char c1, char c2)
  {
    return std::isspace(c1) && std::isspace(c2);
  }

  int32_t limitPos (int32_t pos, int32_t min, int32_t max )
  {
    return std::max<int32_t>(std::min<int32_t>(pos,max),min);
  }

  int32_t convertNth ( int32_t oneUpIndexed )
  {
    return oneUpIndexed < 0 ? oneUpIndexed : oneUpIndexed - 1;
  }

  int32_t convertPosition ( const ::SWA::String& input, int32_t position )
  {
    if ( position <= 0 )
    {
      position = input.last()+position+1;
    }
    return limitPos(position,input.first(),input.last()+1);
  }

  std::pair<int32_t,int32_t> convertPosition (  const ::SWA::String& input, int32_t startPos, int32_t length )
  {
    if ( startPos <= 0 )
    {
      startPos = input.last()+startPos+1;
    }
    int32_t endPos = limitPos(startPos + length,input.first(),input.last()+1);
    startPos = limitPos(startPos,input.first(),input.last()+1);

    if ( startPos <= endPos )
    {
      return std::pair<int32_t,int32_t>(startPos,endPos);
    }
    else
    {
      return std::pair<int32_t,int32_t>(endPos,startPos);
    }
  }
  
  ::SWA::String::const_iterator getConstIterator ( const ::SWA::String& input, int32_t pos )
  {
    return input.begin() + convertPosition(input,pos) - 1;
  }

  ::SWA::String::iterator getIterator ( ::SWA::String& input, int32_t pos )
  {
    return input.begin() + convertPosition(input,pos) - 1;
  }

  boost::iterator_range< ::SWA::String::const_iterator> getConstRange ( const ::SWA::String& input, int32_t startPos, int32_t length )
  {
    std::pair<int32_t,int32_t> range = convertPosition(input,startPos,length);
    return boost::iterator_range< ::SWA::String::const_iterator>(input.begin() + range.first - 1,input.begin() + range.second - 1);
  }

  boost::iterator_range< ::SWA::String::iterator> getRange ( ::SWA::String& input, int32_t startPos, int32_t length )
  {
    std::pair<int32_t,int32_t> range = convertPosition(input,startPos,length);
    return boost::iterator_range< ::SWA::String::iterator>(input.begin() + range.first - 1,input.begin() + range.second - 1);
  }

  boost::iterator_range< ::SWA::String::const_iterator> getConstRange ( const ::SWA::String& input, int32_t startPos )
  {
    return boost::iterator_range< ::SWA::String::const_iterator>(getConstIterator(input,startPos),input.end());
  }

  boost::iterator_range< ::SWA::String::iterator> getRange ( ::SWA::String& input, int32_t startPos )
  {
    return boost::iterator_range< ::SWA::String::iterator>(getIterator(input,startPos),input.end());
  }

  typedef boost::iterator_range< ::SWA::String::const_iterator> SubString;
  typedef boost::iterator_range< ::SWA::String::iterator> WriteableSubString;

  int32_t searchResultToPosition ( const ::SWA::String& str, const SubString& result )
  {
    if ( !result ) return 0;
    else return std::distance(str.begin(),result.begin())+1;
  }

  typedef boost::char_separator<char> CharSeparator;
  typedef boost::escaped_list_separator<char> QuotedSeparator;

}

namespace boost {
  namespace algorithm {

    template<>
    class has_native_replace<SWA::String>
    {
      public:
        typedef mpl::bool_<true> type;
        static const bool value = true;
    };

  }
}



namespace masld_Strings
{
  const bool localServiceRegistration_masls_overload1_to_lower_case = interceptor_masls_overload1_to_lower_case::instance().registerLocal( &masls_overload1_to_lower_case );
  const bool localServiceRegistration_masls_overload1_to_upper_case = interceptor_masls_overload1_to_upper_case::instance().registerLocal( &masls_overload1_to_upper_case );
  const bool localServiceRegistration_masls_overload1_trim = interceptor_masls_overload1_trim::instance().registerLocal( &masls_overload1_trim );
  const bool localServiceRegistration_masls_overload1_trim_leading = interceptor_masls_overload1_trim_leading::instance().registerLocal( &masls_overload1_trim_leading );
  const bool localServiceRegistration_masls_overload1_trim_trailing = interceptor_masls_overload1_trim_trailing::instance().registerLocal( &masls_overload1_trim_trailing );
  const bool localServiceRegistration_masls_overload1_squeeze = interceptor_masls_overload1_squeeze::instance().registerLocal( &masls_overload1_squeeze );
  const bool localServiceRegistration_masls_overload1_replace_first = interceptor_masls_overload1_replace_first::instance().registerLocal( &masls_overload1_replace_first );
  const bool localServiceRegistration_masls_overload1_replace_next = interceptor_masls_overload1_replace_next::instance().registerLocal( &masls_overload1_replace_next );
  const bool localServiceRegistration_masls_overload1_replace_nth = interceptor_masls_overload1_replace_nth::instance().registerLocal( &masls_overload1_replace_nth );
  const bool localServiceRegistration_masls_overload1_replace_last = interceptor_masls_overload1_replace_last::instance().registerLocal( &masls_overload1_replace_last );
  const bool localServiceRegistration_masls_overload1_replace_all = interceptor_masls_overload1_replace_all::instance().registerLocal( &masls_overload1_replace_all );
  const bool localServiceRegistration_masls_overload1_replace_head = interceptor_masls_overload1_replace_head::instance().registerLocal( &masls_overload1_replace_head );
  const bool localServiceRegistration_masls_overload1_replace_tail = interceptor_masls_overload1_replace_tail::instance().registerLocal( &masls_overload1_replace_tail );
  const bool localServiceRegistration_masls_overload2_replace_substring = interceptor_masls_overload2_replace_substring::instance().registerLocal( &masls_overload2_replace_substring );
  const bool localServiceRegistration_masls_overload3_replace_substring = interceptor_masls_overload3_replace_substring::instance().registerLocal( &masls_overload3_replace_substring );
  const bool localServiceRegistration_masls_overload1_insert = interceptor_masls_overload1_insert::instance().registerLocal( &masls_overload1_insert );
  const bool localServiceRegistration_masls_overload1_erase_first = interceptor_masls_overload1_erase_first::instance().registerLocal( &masls_overload1_erase_first );
  const bool localServiceRegistration_masls_overload1_erase_next = interceptor_masls_overload1_erase_next::instance().registerLocal( &masls_overload1_erase_next );
  const bool localServiceRegistration_masls_overload1_erase_nth = interceptor_masls_overload1_erase_nth::instance().registerLocal( &masls_overload1_erase_nth );
  const bool localServiceRegistration_masls_overload1_erase_last = interceptor_masls_overload1_erase_last::instance().registerLocal( &masls_overload1_erase_last );
  const bool localServiceRegistration_masls_overload1_erase_all = interceptor_masls_overload1_erase_all::instance().registerLocal( &masls_overload1_erase_all );
  const bool localServiceRegistration_masls_overload1_erase_head = interceptor_masls_overload1_erase_head::instance().registerLocal( &masls_overload1_erase_head );
  const bool localServiceRegistration_masls_overload1_erase_tail = interceptor_masls_overload1_erase_tail::instance().registerLocal( &masls_overload1_erase_tail );
  const bool localServiceRegistration_masls_overload2_erase_substring = interceptor_masls_overload2_erase_substring::instance().registerLocal( &masls_overload2_erase_substring );
  const bool localServiceRegistration_masls_overload3_erase_substring = interceptor_masls_overload3_erase_substring::instance().registerLocal( &masls_overload3_erase_substring );

  ::SWA::String masls_to_lower_case ( const ::SWA::String& maslp_input )
  {
    ::SWA::String result;
    std::transform(maslp_input.begin(),maslp_input.end(),std::back_inserter(result),ToLowerCase);
    return result;
  }

  ::SWA::String masls_to_upper_case ( const ::SWA::String& maslp_input )
  {
    ::SWA::String result;
    std::transform(maslp_input.begin(),maslp_input.end(),std::back_inserter(result),ToUpperCase);
    return result;
  }

  ::SWA::String masls_trim ( const ::SWA::String& maslp_input )
  {
    return boost::algorithm::trim_copy(maslp_input);
  }

  ::SWA::String masls_trim_leading ( const ::SWA::String& maslp_input )
  {
    return boost::algorithm::trim_left_copy(maslp_input);
  }

  ::SWA::String masls_trim_trailing ( const ::SWA::String& maslp_input )
  {
    return boost::algorithm::trim_right_copy(maslp_input);
  }

  ::SWA::String masls_squeeze ( const ::SWA::String& maslp_input )
  {
    ::SWA::String result;
    std::unique_copy(maslp_input.begin(), maslp_input.end(), std::back_inserter(result), BothSpace);
    std::replace_if(result.begin(), result.end(),IsSpace,' ');
    return result;
  }

  ::SWA::String masls_head ( const ::SWA::String& maslp_input,
                             int32_t              maslp_length )
  {
    return SWA::String(boost::algorithm::find_head(maslp_input,maslp_length));
  }

  ::SWA::String masls_tail ( const ::SWA::String& maslp_input,
                             int32_t              maslp_length )
  {
    return SWA::String(boost::algorithm::find_tail(maslp_input,maslp_length));
  }

  ::SWA::String masls_substring ( const ::SWA::String& maslp_input,
                                  int32_t              maslp_start_pos )
  {
    return ::SWA::String(getConstIterator(maslp_input,maslp_start_pos),maslp_input.end());
  }

  ::SWA::String masls_overload1_substring ( const ::SWA::String& maslp_input,
                                            int32_t              maslp_start_pos,
                                            int32_t              maslp_length )
  {
    return ::SWA::String(getConstRange(maslp_input,maslp_start_pos,maslp_length));
  }

  bool masls_starts_with ( const ::SWA::String& maslp_input,
                           const ::SWA::String& maslp_prefix )
  {
    return boost::algorithm::starts_with(maslp_input,maslp_prefix);
  }

  bool masls_ends_with ( const ::SWA::String& maslp_input,
                         const ::SWA::String& maslp_suffix )
  {
    return boost::algorithm::ends_with(maslp_input,maslp_suffix);
  }

  bool masls_contains ( const ::SWA::String& maslp_input,
                        const ::SWA::String& maslp_substring )
  {
    return boost::algorithm::contains(maslp_input,maslp_substring);
  }

  int32_t masls_search_first ( const ::SWA::String& maslp_input,
                               const ::SWA::String& maslp_search )
  {
    return searchResultToPosition(maslp_input,boost::algorithm::find_first(maslp_input,maslp_search));
  }

  int32_t masls_search_next ( const ::SWA::String& maslp_input,
                              const ::SWA::String& maslp_search,
                              int32_t              maslp_start_pos )
  {
    SubString toSearch = getConstRange(maslp_input,maslp_start_pos);
    return searchResultToPosition(maslp_input,boost::algorithm::find_first(toSearch,maslp_search));
  }

  int32_t masls_search_nth ( const ::SWA::String& maslp_input,
                             const ::SWA::String& maslp_search,
                             int32_t              maslp_n )
  {
    if ( maslp_n == 0 ) return 0;
    return searchResultToPosition(maslp_input,boost::algorithm::find_nth(maslp_input,maslp_search, convertNth(maslp_n)));
  }

  int32_t masls_search_last ( const ::SWA::String& maslp_input,
                              const ::SWA::String& maslp_search )
  {
    return searchResultToPosition(maslp_input,boost::algorithm::find_last(maslp_input,maslp_search));
  }

  ::SWA::Sequence<int32_t> masls_search_all ( const ::SWA::String& maslp_input,
                                              const ::SWA::String& maslp_search )
  {
    ::SWA::Sequence<int32_t> result;
    std::vector<SubString> found;
    boost::algorithm::find_all(found,maslp_input, maslp_search);
    for ( std::vector<SubString>::const_iterator it = found.begin(); it != found.end(); ++it )
    {
      result.push_back ( searchResultToPosition(maslp_input, *it) );
    }
    return result;
  }

  ::SWA::String masls_replace_first ( const ::SWA::String& maslp_input,
                                      const ::SWA::String& maslp_search,
                                      const ::SWA::String& maslp_replacement )
  {
    return boost::algorithm::replace_first_copy(maslp_input,maslp_search,maslp_replacement );
  }

  ::SWA::String masls_replace_next ( const ::SWA::String& maslp_input,
                                     const ::SWA::String& maslp_search,
                                     int32_t              maslp_start_pos,
                                     const ::SWA::String& maslp_replacement )
  {
    SubString toSearch = getConstRange(maslp_input,maslp_start_pos);
    SubString foundRange = boost::algorithm::find_first(toSearch,maslp_search);
    return boost::algorithm::replace_range_copy(maslp_input,foundRange,maslp_replacement );
  }

  ::SWA::String masls_replace_nth ( const ::SWA::String& maslp_input,
                                    const ::SWA::String& maslp_search,
                                    int32_t              maslp_n,
                                    const ::SWA::String& maslp_replacement )
  {
    if ( maslp_n == 0 ) return maslp_input;
    return boost::algorithm::replace_nth_copy(maslp_input,maslp_search,convertNth(maslp_n),maslp_replacement );
  }

  ::SWA::String masls_replace_last ( const ::SWA::String& maslp_input,
                                     const ::SWA::String& maslp_search,
                                     const ::SWA::String& maslp_replacement )
  {
    return boost::algorithm::replace_last_copy(maslp_input,maslp_search,maslp_replacement );
  }

  ::SWA::String masls_replace_all ( const ::SWA::String& maslp_input,
                                    const ::SWA::String& maslp_search,
                                    const ::SWA::String& maslp_replacement )
  {
    return boost::algorithm::replace_all_copy(maslp_input,maslp_search,maslp_replacement );
  }

  ::SWA::String masls_replace_head ( const ::SWA::String& maslp_input,
                                     int32_t              maslp_length,
                                     const ::SWA::String& maslp_replacement )
  {
    if ( maslp_length == 0 || -maslp_length >= static_cast<int32_t>(maslp_input.size()) )
    {
      return maslp_replacement + maslp_input;
    }
    else
    {
      return boost::algorithm::replace_head_copy(maslp_input,maslp_length,maslp_replacement );
    }
  }

  ::SWA::String masls_replace_tail ( const ::SWA::String& maslp_input,
                                     int32_t              maslp_length,
                                     const ::SWA::String& maslp_replacement )
  {
    if ( maslp_length == 0 || -maslp_length >= static_cast<int32_t>(maslp_input.size()) )
    {
      return maslp_input + maslp_replacement;
    }
    return boost::algorithm::replace_tail_copy(maslp_input,maslp_length,maslp_replacement );
  }

  ::SWA::String masls_replace_substring ( const ::SWA::String& maslp_input,
                                          int32_t              maslp_start_pos,
                                                    const ::SWA::String& maslp_replacement )
  {
    SubString toReplace = getConstRange(maslp_input,maslp_start_pos);

    ::SWA::String result;
    result.reserve(maslp_input.size()-result.size()+maslp_replacement.size());
    return result.append(maslp_input.begin(),toReplace.begin()).append(maslp_replacement).append(toReplace.end(),maslp_input.end());
  }

  ::SWA::String masls_overload1_replace_substring ( const ::SWA::String& maslp_input,
                                                    int32_t              maslp_start_pos,
                                                    int32_t              maslp_length,
                                                    const ::SWA::String& maslp_replacement )
  {
    SubString toReplace = getConstRange(maslp_input,maslp_start_pos,maslp_length);

    ::SWA::String result;
    result.reserve(maslp_input.size()-result.size()+maslp_replacement.size());
    return result.append(maslp_input.begin(),toReplace.begin()).append(maslp_replacement).append(toReplace.end(),maslp_input.end());
  }

  ::SWA::String masls_insert ( const ::SWA::String& maslp_input,
                               int32_t              maslp_position,
                               const ::SWA::String& maslp_insertion )
  {
    ::SWA::String::const_iterator pos = getConstIterator(maslp_input,maslp_position);

    ::SWA::String result;
    result.reserve(maslp_input.size()+maslp_insertion.size());
    return result.append(maslp_input.begin(),pos).append(maslp_insertion).append(pos,maslp_input.end());
  }

  ::SWA::String masls_erase_first ( const ::SWA::String& maslp_input,
                                    const ::SWA::String& maslp_search )
  {
    return boost::algorithm::erase_first_copy(maslp_input,maslp_search );
  }

  ::SWA::String masls_erase_next ( const ::SWA::String& maslp_input,
                                   const ::SWA::String& maslp_search,
                                   int32_t              maslp_start_pos )
  {
    SubString toSearch = getConstRange(maslp_input,maslp_start_pos);
    SubString foundRange = boost::algorithm::find_first(toSearch,maslp_search);
    return boost::algorithm::erase_range_copy(maslp_input,foundRange);
  }

  ::SWA::String masls_erase_nth ( const ::SWA::String& maslp_input,
                                  const ::SWA::String& maslp_search,
                                  int32_t              maslp_n )
  {
    if ( maslp_n == 0 ) return maslp_input;
    return boost::algorithm::erase_nth_copy(maslp_input,maslp_search,convertNth(maslp_n) );
  }

  ::SWA::String masls_erase_last ( const ::SWA::String& maslp_input,
                                   const ::SWA::String& maslp_search )
  {
    return boost::algorithm::erase_last_copy(maslp_input,maslp_search );
  }

  ::SWA::String masls_erase_all ( const ::SWA::String& maslp_input,
                                  const ::SWA::String& maslp_search )
  {
    return boost::algorithm::erase_all_copy(maslp_input,maslp_search );
  }

  ::SWA::String masls_erase_head ( const ::SWA::String& maslp_input,
                                   int32_t              maslp_length )
  {
    return boost::algorithm::erase_head_copy(maslp_input,maslp_length );
  }

  ::SWA::String masls_erase_tail ( const ::SWA::String& maslp_input,
                                   int32_t              maslp_length )
  {
    return boost::algorithm::erase_tail_copy(maslp_input,maslp_length );
  }



  ::SWA::String masls_erase_substring ( const ::SWA::String& maslp_input,
                                        int32_t              maslp_start_pos )
  {
    return boost::algorithm::erase_range_copy(maslp_input,getConstRange(maslp_input,maslp_start_pos) );
  }

  ::SWA::String masls_overload1_erase_substring ( const ::SWA::String& maslp_input,
                                                  int32_t              maslp_start_pos,
                                                  int32_t              maslp_length )
  {
    return boost::algorithm::erase_range_copy(maslp_input,getConstRange(maslp_input,maslp_start_pos,maslp_length) );
  }

  ::SWA::String masls_join ( const ::SWA::Sequence< ::SWA::String>& maslp_strings,
                             const ::SWA::String&                   maslp_separator )
  {
    return boost::algorithm::join(maslp_strings, maslp_separator);
  }

  ::SWA::Sequence< ::SWA::String> masls_tokenize ( const ::SWA::String& maslp_input )
  {
    boost::tokenizer<CharSeparator> tokens(maslp_input.s_str(),CharSeparator( " \t\n\f\v\r", "", boost::drop_empty_tokens ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_overload1_tokenize ( const ::SWA::String&    maslp_input,
                                                             const ::SWA::Set<char>& maslp_dropped_separators )
  {
    std::string dropped(maslp_dropped_separators.begin(),maslp_dropped_separators.end());
    boost::tokenizer<CharSeparator> tokens(maslp_input.s_str(),CharSeparator( dropped.c_str(), "", boost::drop_empty_tokens ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_overload2_tokenize ( const ::SWA::String&    maslp_input,
                                                             const ::SWA::Set<char>& maslp_dropped_separators,
                                                             const ::SWA::Set<char>& maslp_retained_separators )
  {
    std::string dropped(maslp_dropped_separators.begin(),maslp_dropped_separators.end());
    std::string retained(maslp_retained_separators.begin(),maslp_retained_separators.end());

    boost::tokenizer<CharSeparator> tokens(maslp_input.s_str(),CharSeparator( dropped.c_str(), retained.c_str(), boost::drop_empty_tokens ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_tokenize_keep_empty ( const ::SWA::String& maslp_input )
  {
    boost::tokenizer<CharSeparator> tokens(maslp_input.s_str(),CharSeparator( " \t\n\f\v\r", "", boost::keep_empty_tokens ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_overload1_tokenize_keep_empty ( const ::SWA::String&    maslp_input,
                                                                        const ::SWA::Set<char>& maslp_dropped_separators )
  {
    std::string dropped(maslp_dropped_separators.begin(),maslp_dropped_separators.end());

    boost::tokenizer<CharSeparator> tokens(maslp_input.s_str(),CharSeparator( dropped.c_str(), "", boost::keep_empty_tokens ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_overload2_tokenize_keep_empty ( const ::SWA::String&    maslp_input,
                                                                        const ::SWA::Set<char>& maslp_dropped_separators,
                                                                        const ::SWA::Set<char>& maslp_retained_separators )
  {
    std::string dropped(maslp_dropped_separators.begin(),maslp_dropped_separators.end());
    std::string retained(maslp_retained_separators.begin(),maslp_retained_separators.end());

    boost::tokenizer<CharSeparator> tokens(maslp_input.s_str(),CharSeparator( dropped.c_str(), retained.c_str(), boost::keep_empty_tokens ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_tokenize_quoted ( const ::SWA::String& maslp_input )
  {
    boost::tokenizer<QuotedSeparator> tokens(maslp_input.s_str(),QuotedSeparator( "\\", ",", "\"" ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_overload1_tokenize_quoted ( const ::SWA::String&    maslp_input,
                                                                    const ::SWA::Set<char>& maslp_separators )
  {
    std::string separators(maslp_separators.begin(),maslp_separators.end());

    boost::tokenizer<QuotedSeparator> tokens(maslp_input.s_str(),QuotedSeparator( "\\", separators.c_str(), "\"" ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::String> masls_overload2_tokenize_quoted ( const ::SWA::String&    maslp_input,
                                                                    const ::SWA::Set<char>& maslp_separators,
                                                                    const ::SWA::Set<char>& maslp_quotes,
                                                                    const ::SWA::Set<char>& maslp_escapes )
  {
    std::string separators(maslp_separators.begin(),maslp_separators.end());
    std::string quotes(maslp_quotes.begin(),maslp_quotes.end());
    std::string escapes(maslp_escapes.begin(),maslp_escapes.end());

    boost::tokenizer<QuotedSeparator> tokens(maslp_input.s_str(),QuotedSeparator( escapes.c_str(), separators.c_str(), quotes.c_str() ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }

  ::SWA::Sequence< ::SWA::Sequence< ::SWA::String> > masls_parse_csv ( const ::SWA::String& maslp_input )
  {
    ::SWA::Sequence< ::SWA::Sequence< ::SWA::String> > result;

    boost::tokenizer<CharSeparator> lines(maslp_input.s_str(),CharSeparator( "\n", "", boost::keep_empty_tokens ));

    for ( boost::tokenizer<CharSeparator>::const_iterator it = lines.begin(), end = lines.end(); it != end; ++it )
    { 
      boost::tokenizer<QuotedSeparator> fields(*it,QuotedSeparator( "\\", ",", "\"" ));
      result.push_back(::SWA::Sequence< ::SWA::String>(fields.begin(),fields.end()));
    }
    return result;
  } 

  ::SWA::Sequence< ::SWA::String> masls_split_lines ( const ::SWA::String& maslp_input )
  {
    boost::tokenizer<CharSeparator> lines(maslp_input.s_str(),CharSeparator( "\n", "", boost::keep_empty_tokens ));
    return ::SWA::Sequence< ::SWA::String> (lines.begin(),lines.end());
  } 

  ::SWA::Sequence< ::SWA::String> masls_tokenize_fixed_size ( const ::SWA::String&            maslp_input,
                                                              const ::SWA::Sequence<int32_t>& maslp_sizes,
                                                              bool                            maslp_wrap,
                                                              bool                            maslp_allow_partial )
  {
    typedef boost::offset_separator Separator;

    boost::tokenizer<Separator> tokens(maslp_input.s_str(),Separator( maslp_sizes.begin(), maslp_sizes.end(), maslp_wrap, maslp_allow_partial ));

    return ::SWA::Sequence< ::SWA::String> (tokens.begin(),tokens.end());
  }
  void masls_overload1_to_lower_case ( ::SWA::String& maslp_target )
  {
    std::transform(maslp_target.begin(),maslp_target.end(),maslp_target.begin(),ToLowerCase);
  }

  void masls_overload1_to_upper_case ( ::SWA::String& maslp_target )
  {
    std::transform(maslp_target.begin(),maslp_target.end(),maslp_target.begin(),ToUpperCase);
  }

  void masls_overload1_trim ( ::SWA::String& maslp_target )
  {
    boost::algorithm::trim (maslp_target);
  }

  void masls_overload1_trim_leading ( ::SWA::String& maslp_target )
  {
    boost::algorithm::trim_left (maslp_target);
  }

  void masls_overload1_trim_trailing ( ::SWA::String& maslp_target )
  {
    boost::algorithm::trim_right (maslp_target);
  }

  void masls_overload1_squeeze ( ::SWA::String& maslp_target )
  {
    maslp_target.erase(std::unique (maslp_target.begin(), maslp_target.end(), BothSpace), maslp_target.end());
    std::replace_if(maslp_target.begin(), maslp_target.end(),IsSpace,' ');
  }

  void masls_overload1_replace_first ( ::SWA::String& maslp_target,
                                      const ::SWA::String& maslp_search,
                                      const ::SWA::String& maslp_replacement )
  {
    boost::algorithm::replace_first (maslp_target,maslp_search,maslp_replacement );
  }

  void masls_overload1_replace_next ( ::SWA::String& maslp_target,
                                     const ::SWA::String& maslp_search,
                                     int32_t              maslp_start_pos,
                                     const ::SWA::String& maslp_replacement )
  {
    WriteableSubString toSearch = getRange(maslp_target,maslp_start_pos);
    WriteableSubString foundRange = boost::algorithm::find_first(toSearch,maslp_search);
    boost::algorithm::replace_range (maslp_target,foundRange,maslp_replacement );
  }

  void masls_overload1_replace_nth ( ::SWA::String& maslp_target,
                                    const ::SWA::String& maslp_search,
                                    int32_t              maslp_n,
                                    const ::SWA::String& maslp_replacement )
  {
    if ( maslp_n == 0 ) return;
    boost::algorithm::replace_nth (maslp_target,maslp_search,convertNth(maslp_n),maslp_replacement );
  }

  void masls_overload1_replace_last ( ::SWA::String& maslp_target,
                                     const ::SWA::String& maslp_search,
                                     const ::SWA::String& maslp_replacement )
  {
    boost::algorithm::replace_last (maslp_target,maslp_search,maslp_replacement );
  }

  void masls_overload1_replace_all ( ::SWA::String& maslp_target,
                                    const ::SWA::String& maslp_search,
                                    const ::SWA::String& maslp_replacement )
  {
    boost::algorithm::replace_all (maslp_target,maslp_search,maslp_replacement );
  }

  void masls_overload1_replace_head ( ::SWA::String& maslp_target,
                                     int32_t              maslp_length,
                                     const ::SWA::String& maslp_replacement )
  {
     if ( maslp_length == 0 || -maslp_length >= static_cast<int32_t>(maslp_target.size()) )
    {
      // for some reason the boost algorithm doesn't do the replace if the head is empty
      maslp_target.insert(0,maslp_replacement);
    }
    else
    {
      boost::algorithm::replace_head (maslp_target,maslp_length,maslp_replacement );
    }
  }

  void masls_overload1_replace_tail ( ::SWA::String& maslp_target,
                                     int32_t              maslp_length,
                                     const ::SWA::String& maslp_replacement )
  {
    if ( maslp_length == 0 || -maslp_length >= static_cast<int32_t>(maslp_target.size()) )
    {
      // for some reason the boost algorithm doesn't do the replace if the tail is empty
      maslp_target.append(maslp_replacement);
    }
    else
    {
      boost::algorithm::replace_tail (maslp_target,maslp_length,maslp_replacement );
    }
  }

  void masls_overload2_replace_substring ( ::SWA::String& maslp_target,
                                          int32_t              maslp_start_pos,
                                                    const ::SWA::String& maslp_replacement )
  {
    WriteableSubString toReplace = getRange(maslp_target,maslp_start_pos);
    maslp_target.replace(toReplace.begin(),toReplace.end(),maslp_replacement.begin(),maslp_replacement.end());
  }

  void masls_overload3_replace_substring ( ::SWA::String& maslp_target,
                                                    int32_t              maslp_start_pos,
                                                    int32_t              maslp_length,
                                                    const ::SWA::String& maslp_replacement )
  {
    WriteableSubString toReplace = getRange(maslp_target,maslp_start_pos,maslp_length);
    maslp_target.replace(toReplace.begin(),toReplace.end(),maslp_replacement.begin(),maslp_replacement.end());
  }

  void masls_overload1_insert ( ::SWA::String& maslp_target,
                               int32_t              maslp_position,
                               const ::SWA::String& maslp_insertion )
  {
    ::SWA::String::iterator pos = getIterator(maslp_target,maslp_position);
    maslp_target.insert(pos,maslp_insertion.begin(),maslp_insertion.end());
  }

  void masls_overload1_erase_first ( ::SWA::String& maslp_target,
                                    const ::SWA::String& maslp_search )
  {
    boost::algorithm::erase_first (maslp_target,maslp_search );
  }

  void masls_overload1_erase_next ( ::SWA::String& maslp_target,
                                   const ::SWA::String& maslp_search,
                                   int32_t              maslp_start_pos )
  {
    WriteableSubString toSearch = getRange(maslp_target,maslp_start_pos);
    WriteableSubString foundRange = boost::algorithm::find_first(toSearch,maslp_search);
    boost::algorithm::erase_range (maslp_target,foundRange);
  }

  void masls_overload1_erase_nth ( ::SWA::String& maslp_target,
                                  const ::SWA::String& maslp_search,
                                  int32_t              maslp_n )
  {
    if ( maslp_n == 0 ) return;
    boost::algorithm::erase_nth (maslp_target,maslp_search,convertNth(maslp_n) );
  }

  void masls_overload1_erase_last ( ::SWA::String& maslp_target,
                                   const ::SWA::String& maslp_search )
  {
    boost::algorithm::erase_last (maslp_target,maslp_search );
  }

  void masls_overload1_erase_all ( ::SWA::String& maslp_target,
                                  const ::SWA::String& maslp_search )
  {
    boost::algorithm::erase_all (maslp_target,maslp_search );
  }

  void masls_overload1_erase_head ( ::SWA::String& maslp_target,
                                   int32_t              maslp_length )
  {
    boost::algorithm::erase_head (maslp_target,maslp_length );
  }

  void masls_overload1_erase_tail ( ::SWA::String& maslp_target,
                                   int32_t              maslp_length )
  {
    boost::algorithm::erase_tail (maslp_target,maslp_length );
  }

  void masls_overload2_erase_substring ( ::SWA::String& maslp_target,
                                        int32_t              maslp_start_pos )
  {
    boost::algorithm::erase_range (maslp_target,getRange(maslp_target,maslp_start_pos) );
  }

  void masls_overload3_erase_substring ( ::SWA::String& maslp_target,
                                                  int32_t              maslp_start_pos,
                                                  int32_t              maslp_length )
  {
    boost::algorithm::erase_range (maslp_target,getRange(maslp_target,maslp_start_pos, maslp_length) );
  }

  namespace
  {

    template<class In,class Out>
    void encodeBase64 ( In pos, In end, Out out )
    {
      static const int wrapBlocks = 76 /4;
      static const char lookup[] = 
                                  "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                  "abcdefghijklmnopqrstuvwxyz"
                                  "0123456789+/";
      int block = 0;
      int i = 0;

      unsigned char prev;

      while (pos != end)
      {
        unsigned char cur = *pos++;
        switch ( i )
        {
          case 0:
            if ( block == wrapBlocks )
            {
              *out++ = '\n';
              block = 0;
            }
            *out++ = lookup[(cur & 0xfc) >> 2];
            ++i;
            break;
          case 1:
            *out++ = lookup[(prev & 0x03) << 4 | (cur & 0xf0) >> 4];
            ++i;
            break;
          case 2:
            *out++ = lookup[(prev & 0x0f) << 2 | (cur & 0xc0) >> 6];
            *out++ = lookup[cur & 0x3f];
            i = 0;
            ++block;
            break;
        }
        prev = cur;
      }

      switch ( i )
      {
        case 0:
          break;
        case 1:
          *out++ = lookup[(prev & 0x03) << 4];
          *out++ = '=';
          *out++ = '=';
          break;
        case 2:
          *out++ = lookup[(prev & 0x0f) << 2];
          *out++ = '=';
      }

    }

    template<class In,class Out>
    void decodeBase64 ( In pos, In end, Out out )
    {

      static const signed char lookup[] =  {
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
                -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,
                52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,
                -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,
                15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,
                -1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,
                41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1
            };

      int i = 0;
      signed char prev;
      while ( pos != end )
      {
        unsigned char ch = *pos++;
        signed char cur;

        // Check for end 
        if ( ch == '=' ) break;

        // Ignore non-base64 characters
        if ( (ch & 0x80) || (cur = lookup[ch]) == -1 ) continue;

        switch ( i ) 
        {
          case 0:
            ++i;
            break;
          case 1:
            *out++ = prev << 2 | (cur & 0x30) >> 4;
            ++i;
            break;
          case 2:
            *out++ = (prev & 0x0f) << 4 | (cur & 0x3c) >> 2;
            ++i;
            break;
          case 3:
            *out++ = (prev & 0x03) << 6 | cur;
            i = 0;
            break;
        }
        prev = cur;
      }
    }

  }

  ::SWA::Sequence<uint8_t> masls_decodeBase64 ( const ::SWA::String& maslp_base64Encoded )
  {
    SWA::Sequence<uint8_t> result;

    // Reserve at least enough space... 4 bytes of input packs down to 3 of output.
    // It may be that there are ignored chars or padding, in which case we will simply
    // end up with extra space reserved.
    result.reserve(maslp_base64Encoded.size()*3/4);
    decodeBase64(maslp_base64Encoded.begin(),maslp_base64Encoded.end(),std::back_inserter(result));
    return result;
  }

  ::SWA::String masls_encodeBase64 ( const ::SWA::Sequence<uint8_t>& maslp_rawData )
  {
    SWA::String result;

    // Reserve enough space... 3 bytes of input expands to 4 byes of output round up 
    // to a multiple of 4 for packing, then newlines every 76 characters.
    int encodedSize = ((((int)maslp_rawData.size()*4/3+3)&~0x3)-1)*77/76+1;

    result.reserve(encodedSize);
    encodeBase64(maslp_rawData.begin(),maslp_rawData.end(),std::back_inserter(result));
    return result;
  }


}


