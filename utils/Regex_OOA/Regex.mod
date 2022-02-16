//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

//! Domain Regex supplies various functions for manipulating 
//! and searching strings using regular expressions. The 
//! expressions are supplied in Perl format, which provides 
//! such capabilities as lookahead, cub-expression captures, 
//! non-greedy matching and conditional expressions. 
//!
//! Creation of a new regular expression is relatively 
//! expensive, so regular expressions are not created until 
//! they are actually used in a match, and previously used 
//! expressions are cached. Once the maximum cache size has 
//! been reached, the least recently used expression will age 
//! off. The default max cache size is 100 regexes, but this 
//! can be changed at process startup by means of the 
//! -regex-cache-size command line option. 
domain Regex is
  
  //! Type to hold a regular expression. Regular expressions 
  //! should be specified in Perl regex syntax, which is superset of 
  //! the POSIX Extended format. 
  type regex is string;

  //! Type to hold a format string. Formats are specified in Perl format.
  //!
  //! Perl-style format strings treat all characters as literals except '$' and '\' which start placeholder and escape sequences respectively.
  //!
  //!    Placeholder sequences specify that some part of what matched the regular expression should be sent to output as follows:                                         
  //!
  //!    Placeholder Meaning                                                                                                                                          
  //!    $&          Outputs what matched the whole expression.                                                                                                               
  //!    $`          Outputs the text between the end of the last match found (or the start of the text if no previous match was found), and the start of the current match.  
  //!    $'          Outputs all the text following the end of the current match.                                                                                             
  //!    $$          Outputs a literal '$'                                                                                                                                    
  //!    $n          Outputs what matched the n'th sub-expression.                                                                                                            
  //!
  //!    Any $-placeholder sequence not listed above, results in '$' being treated as a literal.                                                                          
  //!
  //!    An escape character followed by any character x, outputs that character unless x is one of the escape sequences shown below.                                     
  //!
  //!    Escape    Meaning                                                                                                                                                  
  //!    \a        Outputs the bell character: '\a'.
  //!    \e        Outputs the ANSI escape character (code point 27).
  //!    \f        Outputs a form feed character: '\f'
  //!    \n        Outputs a newline character: '\n'.
  //!    \r        Outputs a carriage return character: '\r'.
  //!    \t        Outputs a tab character: '\t'.
  //!    \v        Outputs a vertical tab character: '\v'.
  //!    \xDD      Outputs the character whose hexadecimal code point is 0xDD
  //!    \x{DDDD}  Outputs the character whose hexadecimal code point is 0xDDDDD
  //!    \cX       Outputs the ANSI escape sequence "escape-X".
  //!    \D        If D is a decimal digit in the range 1-9, then outputs the text that matched sub-expression D.
  //!    \l        Causes the next character to be outputted, to be output in lower case.
  //!    \u        Causes the next character to be outputted, to be output in upper case.
  //!    \L        Causes all subsequent characters to be output in lower case, until a \E is found.
  //!    \U        Causes all subsequent characters to be output in upper case, until a \E is found.
  //!    \E        Terminates a \L or \U sequence.
  type format is string;
  
  //! Holds the validity of a particular sub-expression match, 
  //! along with the text relating to that match. 
  //! eg "AAABCCC" matched with "AAA(B?)CCC" will give valid=true, text="B".
  //! eg "AAACCC"  matched with "AAA(B?)CCC" will give valid=true, text="".
  //! eg "AAACCC"  matched with "AAA(B)?CCC" will give valid=false, text="".
  type sub_match is structure
    valid         : anonymous boolean;
    text          : anonymous string;
  end structure;

  //! Holds the result of a regular expression match. The whole 
  //! of the matching text is stored, along with the text of 
  //! any sub-expression captures. 
  type match_result is structure
    whole       : sub_match;
    captured    : anonymous sequence of sub_match;
  end structure;

  //! Holds the position of a particular sub-expression match. 
  //! If the match is invalid, then first and last will both be 
  //! zero. If the match is valid but empty, then first will 
  //! denote the position immediately after the matched 
  //! position, and last will denote the position immediately 
  //! before ( =first-1). 
  //! eg "AAABCCC" matched with "AAA(B?)CCC" will give first=4, last=4. 
  //! eg "AAACCC"  matched with "AAA(B?)CCC" will give first=4, last=3. 
  //! eg "AAACCC"  matched with "AAA(B)?CCC" will give first=0, last=0. 
  type sub_position is structure
    first         : anonymous integer;
    last          : anonymous integer;
  end structure;

  //! Holds the result of a regular expression match. The 
  //! position of the whole of the matching text is stored, 
  //! along with the positions of any sub-expression captures. 
  type match_position is structure
    whole       : sub_position;
    captured    : anonymous sequence of sub_position;
  end structure;


  //! Returns whether the whole source string matches the 
  //! supplies regular expression 
  function is_match_whole ( source : in anonymous string, regex : in regex ) return anonymous boolean;
  
  //! Returns whether the start of the source string matches 
  //! the supplies regular expression 
  function is_match_start ( source : in anonymous string, regex : in regex ) return anonymous boolean;

  //! Returns whether any part of the source string matches the 
  //! supplies regular expression 
  function is_match_anywhere ( source : in anonymous string, regex : in regex ) return anonymous boolean;



  //! Returns the positions of the match and sub-expression 
  //! captures if the regular expression matches the whole 
  //! source string. 
  function search_whole ( source : in anonymous string, regex : in regex ) return match_position;

  //! Returns the positions of the match and sub-expression 
  //! captures if the regular expression matches the start of 
  //! the source string. 
  function search_start ( source : in anonymous string, regex : in regex ) return match_position;

  //! Returns the positions of the first match and sub 
  //! expression captures of the regular expression matches in 
  //! the source string. 
  function search_first ( source : in anonymous string, regex : in regex ) return match_position;

  //! Returns the positions of all matches and sub-expression 
  //! captures of the regular expression matches in the source 
  //! string. 
  function search_repeat ( source : in anonymous string, regex : in regex ) return anonymous sequence of match_position;



  //! Returns the text of the match and sub-expression 
  //! captures if the regular expression matches the whole 
  //! source string. 
  function match_whole ( source : in anonymous string, regex : in regex ) return match_result; 

  //! Returns the text of the match and sub-expression 
  //! captures if the regular expression matches the start of 
  //! the source string. 
  function match_start ( source : in anonymous string, regex : in regex ) return match_result; 

  //! Returns the text of the match and sub 
  //! expression captures of the first regular expression match in 
  //! the source string. 
  function match_first ( source : in anonymous string, regex : in regex ) return match_result; 
  
  //! Returns the text of the match and sub-expression 
  //! captures of all regular expression matches in the source 
  //! string. 
  function match_repeat ( source : in anonymous string, regex : in regex ) return anonymous sequence of match_result; 


  //! Returns the text of sub-expression captures if the 
  //! regular expression matches the whole source string. An 
  //! empty string means either an empty match or no match. An 
  //! empty result means either no matches were found or no 
  //! capturing sub-expressions were specified in the regular 
  //! expression. 
  function get_captures_whole ( source : in anonymous string, regex : in regex ) return anonymous sequence of anonymous string;

  //! Returns the text of sub-expression captures if the 
  //! regular expression matches the start of the source 
  //! string. An empty string means either an empty match or no 
  //! match. An empty result means either no matches were found 
  //! or no capturing sub-expressions were specified in the 
  //! regular expression. 
  function get_captures_start ( source : in anonymous string, regex : in regex ) return anonymous sequence of anonymous string;

  //! Returns the text of the match and sub 
  //! expression captures of the first regular expression match in 
  //! the source string. An empty string means either an empty match or no 
  //! match. An empty result means either no matches were found 
  //! or no capturing sub-expressions were specified in the 
  //! regular expression. 
  function get_captures_first ( source : in anonymous string, regex : in regex ) return anonymous sequence of anonymous string;

  //! Returns the text of all sub-expression captures of the 
  //! regular expression matches in the source string. An empty 
  //! string means either an empty match or no match. An empty 
  //! result means no matches were found. 
  function get_captures_repeat ( source : in anonymous string, regex : in regex ) return anonymous sequence of anonymous sequence of anonymous string;

  //! Replaces the first match of the supplied regular expression with the specified format string.
  function replace_first ( source : in anonymous string, regex : in regex, format : in format ) return anonymous string; 

  //! Replaces all match of the supplied regular expression with the specified format string.
  function replace_all ( source : in anonymous string, regex : in regex, format : in format ) return anonymous string; 

  //! Tokenizes the source string into a sequence of strings using the specified separator to denote boundaries between tokens.
  function tokenize ( source : in anonymous string, separator : in regex ) return anonymous sequence of anonymous string;

end domain; pragma build_set("UtilityDomains");
