//
// UK Crown Copyright (c) 2009. All Rights Reserved
//
#include "Regex_OOA/__Regex_services.hh"
#include "Regex_OOA/__Regex_types.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include "swa/ProgramError.hh"
#include "swa/CommandLine.hh"
#include "swa/Process.hh"

#include <boost/regex.hpp>
#include <boost/multi_index_container.hpp>
#include <boost/multi_index/hashed_index.hpp>
#include <boost/multi_index/sequenced_index.hpp>
#include <boost/multi_index/mem_fun.hpp>

namespace
{
  const char* const cacheOption = "-regex-cache-size";

  struct Init
  {
    Init()
    {
      SWA::Process::getInstance().getCommandLine().registerOption ( SWA::NamedOption(cacheOption,"Regular Expression Cache Size",false,"size",true) );
    }
  } init;


}

namespace masld_Regex
{
  struct by_regex {};
  struct by_index {};

  // Boost Multi-index container. Default index is
  // sequenced, so that most recent entry can be kept at the 
  // head. Secondary index is a hash of the regex string so 
  // that cached values can be retrieved. 
  typedef boost::multi_index_container
            <
              boost::regex,
              boost::multi_index::indexed_by
              < 
                // Primary sequence index
                boost::multi_index::sequenced
                <
                  boost::multi_index::tag<by_index>
                >,
                // Secondary hash index
                boost::multi_index::hashed_unique
                < 
                  boost::multi_index::tag<by_regex>,
                  // Use value.str() as the lookup key
                  boost::multi_index::const_mem_fun
                  <
                    boost::regex,
                    std::string,
                    &boost::regex::str
                  >
                >
              >
            >
        CacheType;


  size_t getCacheSize()
  {
    static const size_t DEFAULT_SIZE = 100;
    try
    {
      return SWA::Process::getInstance().getCommandLine().getIntOption(cacheOption,DEFAULT_SIZE);
    }
    catch ( const std::exception& e )
    {
      return DEFAULT_SIZE;
    }    
  }

  // Return a regex created from the provided string. Creation 
  // of a new regex is relatively expensive, so maintain a 
  // cache of the most frequenty used current regexes. If the 
  // requested regex exists in the cache, use that one, 
  // otherwise create a new one and age off the regex that was 
  // used least recently. 
  boost::regex getRegex ( const maslt_regex& regexStr )
  {
    static CacheType cache;
    static const size_t MAX_CACHE_SIZE =  getCacheSize();
    try
    {
      if ( MAX_CACHE_SIZE == 0 ) return boost::regex(regexStr.c_str());

      // Try to find a cached version. Use the hash lookup key to find it, but convert to the sequence index.
      CacheType::const_iterator cachedRegex = cache.project<by_index>(cache.get<by_regex>().find(regexStr));
      if ( cachedRegex == cache.end() )
      {
        // Not found so create
        cachedRegex = cache.push_front(boost::regex(regexStr.c_str())).first;

        // trim the cache to the required size
        if ( cache.size() > MAX_CACHE_SIZE )
        {
          cache.pop_back();
        }
      }
      else
      {
        // Found in cache, so move it to the front of the list 
        // to mark it as the most recently used. 
        cache.relocate(cache.begin(),cachedRegex);
      }
      // return the cached regex
      return *cachedRegex;
    }
    catch ( const boost::regex_error& e )
    {
      throw ::SWA::ProgramError ( std::string("Invalid Regex: ") + e.what() );
    }

  }

  bool masls_is_match_whole ( const ::SWA::String& maslp_source,
                              const maslt_regex& maslp_regex )
  {
    return boost::regex_match(maslp_source.s_str(),getRegex(maslp_regex));
  }

  bool masls_is_match_start ( const ::SWA::String& maslp_source,
                              const maslt_regex& maslp_regex )
  {
    return boost::regex_search(maslp_source.s_str(),getRegex(maslp_regex),boost::regex_constants::match_continuous);
  }

  bool masls_is_match_anywhere ( const ::SWA::String& maslp_source,
                                 const maslt_regex& maslp_regex )
  {
    return boost::regex_search(maslp_source.s_str(),getRegex(maslp_regex));
  }


  maslt_match_result getResult ( const boost::smatch& match )
  {
    maslt_match_result result;

    result.set_masla_whole() = maslt_sub_match(match[0].matched,match[0].str());

    for ( size_t i = 1; i < match.size(); ++i )
    {
      result.set_masla_captured().push_back(maslt_sub_match(match[i].matched,match[i].str()));
    }      
    return result;
  }

  ::SWA::Sequence< ::SWA::String> getCaptures ( const boost::smatch& match )
  {
    ::SWA::Sequence< ::SWA::String> result;

    for ( size_t i = 1; i < match.size(); ++i )
    {
      result.push_back(match[i].str());
    }      
    return result;
  }

  maslt_match_position getPosition ( const boost::smatch::value_type::iterator& start, const boost::smatch& match )
  {
    maslt_match_position result;

    result.set_masla_whole() = maslt_sub_position(std::distance(start,match[0].first)+1,std::distance(start,match[0].second));

    for ( size_t i = 1; i < match.size(); ++i )
    {
      if ( match[i].matched )
      {
        result.set_masla_captured().push_back(maslt_sub_position(std::distance(start,match[i].first)+1,std::distance(start,match[i].second)));
      }
      else
      {
        result.set_masla_captured().push_back(maslt_sub_position());
      }
    }      
    return result;
  }

  maslt_match_position masls_search_whole ( const ::SWA::String& maslp_source,
                                            const maslt_regex&   maslp_regex )
  {
    boost::smatch result;
    if ( boost::regex_match(maslp_source.s_str(),result,getRegex(maslp_regex)) )
    {
      return getPosition(maslp_source.begin(),result);
    }
    else
    {
      return maslt_match_position();
    }
  }

  maslt_match_position masls_search_start ( const ::SWA::String& maslp_source,
                                            const maslt_regex&   maslp_regex )
  {
    boost::smatch result;
    if ( boost::regex_search(maslp_source.s_str(),result,getRegex(maslp_regex),boost::regex_constants::match_continuous) )
    {
      return getPosition(maslp_source.begin(),result);
    }
    else
    {
      return maslt_match_position();
    }
  }

  maslt_match_position masls_search_first ( const ::SWA::String& maslp_source,
                                               const maslt_regex&   maslp_regex )
  {
    boost::smatch result;
    if ( boost::regex_search(maslp_source.s_str(),result,getRegex(maslp_regex)) )
    {
      return getPosition(maslp_source.begin(),result);
    }
    else
    {
      return maslt_match_position();
    }
  }

  ::SWA::Sequence<maslt_match_position> masls_search_repeat ( const ::SWA::String& maslp_source,
                                                              const maslt_regex&   maslp_regex )
  {
    ::SWA::Sequence<maslt_match_position> result;

    boost::sregex_iterator end;
    boost::sregex_iterator pos = boost::make_regex_iterator ( maslp_source.s_str(),getRegex(maslp_regex) );

    while ( pos != end )
    {
      result.push_back(getPosition(maslp_source.begin(),*pos++));
    }
    return result;

  }



  maslt_match_result masls_match_whole ( const ::SWA::String& maslp_source,
                                         const maslt_regex& maslp_regex )
  {
    boost::smatch result;
    if ( boost::regex_match(maslp_source.s_str(),result,getRegex(maslp_regex)) )
    {
      return getResult(result);
    }
    else
    {
      return maslt_match_result();
    }
  }

  maslt_match_result masls_match_start ( const ::SWA::String& maslp_source,
                                         const maslt_regex& maslp_regex )
  {
    boost::smatch result;
    if ( boost::regex_search(maslp_source.s_str(),result,getRegex(maslp_regex),boost::regex_constants::match_continuous) )
    {
      return getResult(result);
    }
    else
    {
      return maslt_match_result();
    }
  }

  maslt_match_result masls_match_first ( const ::SWA::String& maslp_source,
                                            const maslt_regex& maslp_regex )
  {
    boost::smatch result;
    if ( boost::regex_search(maslp_source.s_str(),result,getRegex(maslp_regex)) )
    {
      return getResult(result);
    }
    else
    {
      return maslt_match_result();
    }
  }

  ::SWA::Sequence<maslt_match_result> masls_match_repeat ( const ::SWA::String& maslp_source,
                                                           const maslt_regex& maslp_regex )
  {
    ::SWA::Sequence<maslt_match_result> result;

    boost::sregex_iterator end;
    boost::sregex_iterator pos = boost::make_regex_iterator ( maslp_source.s_str(),getRegex(maslp_regex) );

    while ( pos != end )
    {
      result.push_back(getResult(*pos++));
    }
    return result;
  }


  ::SWA::Sequence< ::SWA::String> masls_get_captures_whole ( const ::SWA::String& maslp_source,
                                                             const maslt_regex& maslp_regex )
  {
    boost::smatch match;
    if ( boost::regex_match(maslp_source.s_str(),match,getRegex(maslp_regex)) )
    {
      return getCaptures(match);
    }
    else
    {
      return ::SWA::Sequence< ::SWA::String>();
    }
  } 

  ::SWA::Sequence< ::SWA::String> masls_get_captures_start ( const ::SWA::String& maslp_source,
                                                             const maslt_regex& maslp_regex )
  {
    boost::smatch match;
    if ( boost::regex_search(maslp_source.s_str(),match,getRegex(maslp_regex),boost::regex_constants::match_continuous) )
    {
      return getCaptures(match);
    }
    else
    {
      return ::SWA::Sequence< ::SWA::String>();
    }
  } 


  ::SWA::Sequence< ::SWA::String> masls_get_captures_first ( const ::SWA::String& maslp_source,
                                                                const maslt_regex& maslp_regex )
  {
    boost::smatch match;
    if ( boost::regex_search(maslp_source.s_str(),match,getRegex(maslp_regex)) )
    {
      return getCaptures(match);
    }
    else
    {
      return ::SWA::Sequence< ::SWA::String>();
    }
  } 

  ::SWA::Sequence< ::SWA::Sequence< ::SWA::String> > masls_get_captures_repeat ( const ::SWA::String& maslp_source,
                                                              const maslt_regex& maslp_regex )
  {
    boost::smatch match;
    ::SWA::Sequence< ::SWA::Sequence< ::SWA::String> > result;

    boost::sregex_iterator end;
    boost::sregex_iterator pos = boost::make_regex_iterator ( maslp_source.s_str(),getRegex(maslp_regex) );

    while ( pos != end )
    {
      result.push_back(getCaptures(*pos++));
    }

    return result;
  } 


  ::SWA::String masls_replace_first ( const ::SWA::String& maslp_source,
                                      const maslt_regex& maslp_regex,
                                      const maslt_format& maslp_format )
  {
    return boost::regex_replace ( maslp_source.s_str(), getRegex(maslp_regex), maslp_format.s_str(),boost::format_first_only);
  }

  ::SWA::String masls_replace_all ( const ::SWA::String& maslp_source,
                                    const maslt_regex& maslp_regex,
                                    const maslt_format& maslp_format )
  {
    return boost::regex_replace ( maslp_source.s_str(), getRegex(maslp_regex), maslp_format.s_str());
  }

  ::SWA::Sequence< ::SWA::String> masls_tokenize ( const ::SWA::String& maslp_source,
                                                   const maslt_regex&   maslp_separator )
  {
    ::SWA::Sequence< ::SWA::String> result;
    std::string::const_iterator tokenStart = maslp_source.begin();
    for ( boost::sregex_iterator it = boost::sregex_iterator(maslp_source.s_str().begin(), maslp_source.s_str().end(), getRegex(maslp_separator)), end = boost::sregex_iterator();
          it != end;
          ++it )
    {
      // Only add the token if it doesn't start at the end of 
      // the following separator - in other words if the token 
      // is empty and so is the separator - in yet more words, 
      // if the end of the previous separator was in the same 
      // place as the end of this one. The iterator should 
      // really cope with this, but it doesn't. It happens 
      // when there is a lookahead in the separator which can 
      // match optional characters before the lookahead. The 
      // the first time though it matches the optionals plus 
      // the lookahead, and the second time just the 
      // lookahead. There is code in the iterator to cope with 
      // the start and end being in the same place (or it 
      // would just get into an infinite loop matching the 
      // same place over and over again), but not just the end. 
      if ( tokenStart != (*it)[0].second )
      {
        result.push_back(std::string(tokenStart,(*it)[0].first));
        tokenStart = (*it)[0].second;
      }
    }
    result.push_back(std::string(tokenStart,maslp_source.end()));
    return result;
  }

}
