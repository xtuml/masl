//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain Strings is

    //! Convert a string to lower case
    function to_lower_case ( input : in anonymous string ) return anonymous string;

    //! Convert a string to upper case
    function to_upper_case ( input : in anonymous string ) return anonymous string;

    //! Trim leading and trailing whitespace from a string
    function trim      ( input : in anonymous string ) return anonymous string;

    //! Trim leading whitespace from a string
    function trim_leading ( input : in anonymous string ) return anonymous string;

    //! Trim trailing whitespace from a string
    function trim_trailing ( input : in anonymous string ) return anonymous string;

    //! Replace all sequences of whitespace with a single space character
    function squeeze ( input : in anonymous string ) return anonymous string;
    

    //! Convert a string to lower case
    service to_lower_case ( target : out anonymous string );

    //! Convert a string to upper case
    service to_upper_case ( target : out anonymous string );

    //! Trim leading and trailing whitespace from a string
    service trim      ( target : out anonymous string );

    //! Trim leading whitespace from a string
    service trim_leading ( target : out anonymous string );

    //! Trim trailing whitespace from a string
    service trim_trailing ( target : out anonymous string );

    //! Replace all sequences of whitespace with a single space character
    service squeeze ( target : out anonymous string );
    
    //! Return the requested number of characters from the 
    //! start of the string, or the whole string if shorter. If 
    //! a negative number of characters is requested, then 
    //! input'length - abs(length) characters will be returned. 
    //! In other words, abs(length) characters will be left 
    //! behind. 
    //! eg.
    //!    head("12345",2) = "12"
    //!    head("12345",-2) = "123"
    //!    head("12345",10) = "12345"
    //!    head("12345",-10) = ""
    function head ( input : in anonymous string, length : in anonymous integer ) return anonymous string;

    //! Return the requested number of characters from the 
    //! end of the string, or the whole string if shorter. If 
    //! a negative number of characters is requested, then 
    //! input'length - abs(length) characters will be returned. 
    //! In other words, abs(length) characters will be left 
    //! behind. 
    //! eg.
    //!    tail("12345",2) = "45"
    //!    tail("12345",-2) = "345"
    //!    tail("12345",10) = "12345"
    //!    tail("12345",-10) = ""
    function tail ( input : in anonymous string, length : in anonymous integer ) return anonymous string;

    //! Return a substring from the specified start position 
    //! (1-up) to the end of the string. If the start position 
    //! is past the end then the empty string will be returned. 
    //! If the start position is zero or negative, then the 
    //! position is interpreted as relative to the end of the string. 
    //! eg.
    //!    substring("12345",2) = "2345"
    //!    substring("12345",10) = ""
    //!    substring("12345",-2) = "45"
    function substring ( input : in anonymous string, start_pos : in anonymous integer ) return anonymous string;

    //! Return a substring from the specified start position 
    //! (1-up) of the specified length. 
    //! If the start position is zero or negative, then the 
    //! position is interpreted as relative to the end of the 
    //! string. If length is negative, then the result will be 
    //! from positions counting backwards from the start 
    //! position, not including the start position itself. If 
    //! there are insufficient characters available to form a 
    //! string of the requested length, a shorter (or even 
    //! empty) string will be returned. Positions outside the 
    //! valid range for the string are interpreted as valid 
    //! positions but with no character present. 
    //! eg.
    //!    substring("12345",3,2) = "34"
    //!    substring("12345",3,10) = "345"
    //!    substring("12345",10,1) = ""
    //!    substring("12345",-1,4) = "5" 
    //!    substring("12345",-4,4) = "2345" 
    //!    substring("12345",5,-2) = "34"
    //!    substring("12345",0,-2) = "45"
    //!    substring("12345",6,-2) = "45"
    //!    substring("12345",7,-3) = "45"
    function substring ( input : in anonymous string, start_pos : in anonymous integer, length : in anonymous integer ) return anonymous string;


    //! Check whether a string starts with the specified prefix
    function starts_with ( input : in anonymous string, prefix : in anonymous string ) return anonymous boolean;                    

    //! Check whether a string ends with the specified suffix
    function ends_with   ( input : in anonymous string, suffix : in anonymous string ) return anonymous boolean;                    

    //! Check whether a string contains the specified substring
    function contains    ( input : in anonymous string, substring : in anonymous string ) return anonymous boolean;                    

    //! Finds the position of the first occurrence of the 
    //! specified search string. Returns zero if it is not 
    //! present. 
    function search_first ( input : in anonymous string, search : in anonymous string ) return anonymous integer;

    //! Finds the position of the next occurrence of the 
    //! specified search string starting from the specified 
    //! position. Returns zero if it is not present. 
    function search_next  ( input : in anonymous string, search : in anonymous string, start_pos : in anonymous integer ) return anonymous integer;

    //! Finds the position of the nth occurrence of the 
    //! specified search string. Returns zero if fewer than n 
    //! occurrences are present. 
    function search_nth   ( input : in anonymous string, search : in anonymous string, n         : in anonymous integer ) return anonymous integer;

    //! Finds the position of the last occurrence of the 
    //! specified search string. Returns zero if it is not 
    //! present. 
    function search_last  ( input : in anonymous string, search : in anonymous string ) return anonymous integer;

    //! Finds the position of all occurrences of the specified 
    //! search string. Returns an empty sequence if none are 
    //! present. 
    function search_all   ( input : in anonymous string, search : in anonymous string ) return anonymous sequence of anonymous integer;


    //! Replaces the first occurrence of the specified search 
    //! string with the specified replacement. 
    function replace_first ( input : in anonymous string, search : in anonymous string, replacement : in anonymous string ) return anonymous string;

    //! Replaces the next occurrence of the 
    //! specified search string starting from the specified 
    //! position with the specified replacement.  
    function replace_next  ( input : in anonymous string, search : in anonymous string, start_pos : in anonymous integer, replacement : in anonymous string ) return anonymous string;

    //! Replaces the nth occurrence of the 
    //! specified search string with the specified replacement. 
    function replace_nth   ( input : in anonymous string, search : in anonymous string, n         : in anonymous integer, replacement : in anonymous string ) return anonymous string;

    //! Replaces the last occurrence of the 
    //! specified search string with the specified replacement. 
    function replace_last  ( input : in anonymous string, search : in anonymous string, replacement : in anonymous string ) return anonymous string;

    //! Replaces all occurrences of the specified 
    //! search string with the specified replacement. 
    function replace_all   ( input : in anonymous string, search : in anonymous string, replacement : in anonymous string ) return anonymous string;

    //! Replaces the specified number of characters from the start of the string with the supplied replacement.
    function replace_head       ( input : in anonymous string, length   : in anonymous integer, replacement : in anonymous string ) return anonymous string;

    //! Replaces the specified number of characters from the end of the string with the supplied replacement. 
    function replace_tail       ( input : in anonymous string, length   : in anonymous integer, replacement : in anonymous string ) return anonymous string;

    //! Replaces all characters from the specified position in the string to the end with the supplied replacement.
    function replace_substring  ( input : in anonymous string, start_pos  : in anonymous integer, replacement : in anonymous string ) return anonymous string;

    //! Replaces the specified number of characters starting from the specified position in the string with the supplied replacement. 
    function replace_substring  ( input : in anonymous string, start_pos  : in anonymous integer, length   : in anonymous integer, replacement : in anonymous string ) return anonymous string;



    //! Replaces the first occurrence of the specified search 
    //! string with the specified replacement. 
    service replace_first ( target : out anonymous string, search : in anonymous string, replacement : in anonymous string );

    //! Replaces the next occurrence of the 
    //! specified search string starting from the specified 
    //! position with the specified replacement.  
    service replace_next  ( target : out anonymous string, search : in anonymous string, start_pos : in anonymous integer, replacement : in anonymous string );

    //! Replaces the nth occurrence of the 
    //! specified search string with the specified replacement. 
    service replace_nth   ( target : out anonymous string, search : in anonymous string, n         : in anonymous integer, replacement : in anonymous string );

    //! Replaces the last occurrence of the 
    //! specified search string with the specified replacement. 
    service replace_last  ( target : out anonymous string, search : in anonymous string, replacement : in anonymous string );

    //! Replaces all occurrences of the specified 
    //! search string with the specified replacement. 
    service replace_all   ( target : out anonymous string, search : in anonymous string, replacement : in anonymous string );

    //! Replaces the specified number of characters from the start of the string with the supplied replacement.
    service replace_head       ( target : out anonymous string, length   : in anonymous integer, replacement : in anonymous string );

    //! Replaces the specified number of characters from the end of the string with the supplied replacement. 
    service replace_tail       ( target : out anonymous string, length   : in anonymous integer, replacement : in anonymous string );

    //! Replaces all characters from the specified position in the string to the end with the supplied replacement.
    service replace_substring  ( target : out anonymous string, start_pos  : in anonymous integer, replacement : in anonymous string );

    //! Replaces the specified number of characters starting from the specified position in the string with the supplied replacement. 
    service replace_substring  ( target : out anonymous string, start_pos  : in anonymous integer, length   : in anonymous integer, replacement : in anonymous string );


    //! Inserts the supplied string before the specified position. If the position is after the end, then the string will be appended.
    function insert ( input : in anonymous string, position : in anonymous integer, insertion : in anonymous string ) return anonymous string;


    //! Inserts the supplied string before the specified position. If the position is after the end, then the string will be appended.
    service insert ( target : out anonymous string, position : in anonymous integer, insertion : in anonymous string );


    //! Erases the first occurrence of the specified search 
    //! string. 
    function erase_first ( input : in anonymous string, search : in anonymous string ) return anonymous string;

    //! Erases the next occurrence of the 
    //! specified search string starting from the specified 
    //! position.
    function erase_next  ( input : in anonymous string, search : in anonymous string, start_pos : in anonymous integer ) return anonymous string;

    //! Erases the nth occurrence of the 
    //! specified search string.  
    function erase_nth   ( input : in anonymous string, search : in anonymous string, n         : in anonymous integer ) return anonymous string;

    //! Erases the last occurrence of the 
    //! specified search string. 
    function erase_last  ( input : in anonymous string, search : in anonymous string ) return anonymous string;

    //! Erases all occurrences of the specified 
    //! search string. 
    function erase_all   ( input : in anonymous string, search : in anonymous string ) return anonymous string;

    //! Erases the specified number of characters from the start of the string. 
    function erase_head       ( input : in anonymous string, length : in anonymous integer ) return anonymous string;

    //! Erases the specified number of characters from the end of the string. 
    function erase_tail       ( input : in anonymous string, length : in anonymous integer ) return anonymous string;

    //! Erases all characters from the specified position in the string to the end. 
    function erase_substring  ( input : in anonymous string, start_pos  : in anonymous integer ) return anonymous string;

    //! Erases the specified number of characters starting from the specified position in the string. 
    function erase_substring  ( input : in anonymous string, start_pos  : in anonymous integer, length : in anonymous integer ) return anonymous string;


    //! Erases the first occurrence of the specified search 
    //! string. 
    service erase_first ( target : out anonymous string, search : in anonymous string );

    //! Erases the next occurrence of the 
    //! specified search string starting from the specified 
    //! position.
    service erase_next  ( target : out anonymous string, search : in anonymous string, start_pos : in anonymous integer );

    //! Erases the nth occurrence of the 
    //! specified search string.  
    service erase_nth   ( target : out anonymous string, search : in anonymous string, n         : in anonymous integer );

    //! Erases the last occurrence of the 
    //! specified search string. 
    service erase_last  ( target : out anonymous string, search : in anonymous string );

    //! Erases all occurrences of the specified 
    //! search string. 
    service erase_all   ( target : out anonymous string, search : in anonymous string );

    //! Erases the specified number of characters from the start of the string. 
    service erase_head       ( target : out anonymous string, length : in anonymous integer );

    //! Erases the specified number of characters from the end of the string. 
    service erase_tail       ( target : out anonymous string, length : in anonymous integer );

    //! Erases all characters from the specified position in the string to the end. 
    service erase_substring  ( target : out anonymous string, start_pos  : in anonymous integer );

    //! Erases the specified number of characters starting from the specified position in the string. 
    service erase_substring  ( target : out anonymous string, start_pos  : in anonymous integer, length : in anonymous integer );

    //! Join a sequence of strings into one string with the separator between each one
    //! eg join ( { "One", "Two", "Three" }, ", " ) = "One, Two, Three"
    function join ( strings : in anonymous sequence of anonymous string, separator : in anonymous string ) return anonymous string;

    //! Tokenize a string into a sequence of strings using any 
    //! number of whitespace characters as a separator. The 
    //! whitespace is not included in the result. 
    //! eg tokenize ( "The    quick       brown  fox." ) = { "The", "quick", "brown", "fox." }
    function tokenize ( input  : in anonymous string )
                        return      anonymous sequence of anonymous string;


    //! Tokenize a string into a sequence of strings using any 
    //! number of the supplied separator characters as a 
    //! separator. The separators are not included in the 
    //! result. 
    //! eg tokenize ( "one,,three,,,six....twenty", ",." ) = { "one", "three", "six", "twenty" }

    function tokenize ( input              : in anonymous string, 
                        dropped_separators : in anonymous set of anonymous character )
                        return                  anonymous sequence of anonymous string;

    //! Tokenize a string into a sequence of strings using any 
    //! number of the supplied dropped_separator characters or 
    //! a single retained_separator as a separator. The dropped 
    //! separators are not included in the result. Retained 
    //! separators are included as a token. 
    //! eg tokenize ( "1+2 *   3*-4", " ","+-*/" ) = { "1", "+", "2", "*", "3", "*", "-", "4" }

    function tokenize ( input               : in anonymous string, 
                        dropped_separators  : in anonymous set of anonymous character,
                        retained_separators : in anonymous set of anonymous character ) 
                        return                   anonymous sequence of anonymous string;

    //! Tokenize a string into a sequence of strings using a 
    //! single whitespace character as a separator. The 
    //! whitespace is not included in the result. 
    //! eg tokenize_keep_empty ( "The quick brown  fox." ) = { "The", "quick", "brown", "", "fox." }

    function tokenize_keep_empty ( input : in anonymous string )
                                   return     anonymous sequence of anonymous string;

    //! Tokenize a string into a sequence of strings using any 
    //! one of the supplied separator characters as a 
    //! separator. The separators are not included in the 
    //! result. 
    //! eg tokenize_keep_empty ( "one,,three,,,six...nine", ",." ) = { "one", "", "three", "", "", "six", "", "", "nine" }
    function tokenize_keep_empty ( input              : in anonymous string, 
                                   dropped_separators : in anonymous set of anonymous character )
                                   return                  anonymous sequence of anonymous string;

    //! Tokenize a string into a sequence of strings using any 
    //! one of the supplied dropped_separator or 
    //! retained_separator characters as a separator. The 
    //! dropped separators are not included in the result. 
    //! Retained separators are included as a token. 
    //! eg tokenize_keep_empty ( "1+2 *   3*-4", " ","+-*/" ) = { "1", "+", "2", "", "*", "", "", "", "3", "*", "-", "4" }
    function tokenize_keep_empty ( input               : in anonymous string, 
                                   dropped_separators  : in anonymous set of anonymous character,
                                   retained_separators : in anonymous set of anonymous character ) 
                                   return                   anonymous sequence of anonymous string;

    //! Tokenize a string into a sequence of strings using ',' as a separator, but do 
    //! not look for separators within any portion of a string 
    //! enclosed by '"' characters. 
    //!
    //! Three escape sequences are supported to allow quotes to 
    //! be embedded within a quoted string: 
    //!   \"  -> " 
    //!   \n  -> new line 
    //!   \\  -> \ 
    //!
    //! eg. In the following example, 
    //! strings are shown as they would appear in a text file 
    //! or printed to console, between square brackets, rather 
    //! than as a masl string literal to avoid confusion of 
    //! escapes and quotes. 
    //!      tokenize_quoted ( [Alexis,Aline,,"Wells, John Wellington (\"Sorcerer\")"] ) =
    //!               { [Alexis], [Aline], [], [Wells, John Wellington ("Sorcerer")] }
    function tokenize_quoted ( input : in anonymous string )
                               return     anonymous sequence of anonymous string;

    //! Tokenize a string into a sequence of strings using any 
    //! one of the specified characters as a separator, but do 
    //! not look for separators within any portion of a string 
    //! enclosed by '"' characters. 
    //!
    //! Three escape sequences are supported to allow quotes to 
    //! be embedded within a quoted string: 
    //!   \"  -> " 
    //!   \n  -> new line 
    //!   \\  -> \ 
    //!
    //! eg. In the following example, 
    //! strings are shown as they would appear in a text file 
    //! or printed to console, between square brackets, rather 
    //! than as a masl string literal to avoid confusion of 
    //! escapes and quotes. 
    //!      tokenize_quoted ( [Alexis,Aline,,"Wells, John Wellington (\"Sorcerer\")"], "," ) = 
    //!               { [Alexis], [Aline], [], [Wells, John Wellington ("Sorcerer")] }
    function tokenize_quoted ( input      : in anonymous string, 
                               separators : in anonymous set of anonymous character )
                               return          anonymous sequence of anonymous string;
                               
    //! Tokenize a string into a sequence of strings using any 
    //! one of the specified characters as a separator, but do 
    //! not look for separators within any portion of a string 
    //! enclosed by any of the specified quote characters. 
    //!
    //! Three escape sequences are supported to allow quotes to 
    //! be embedded within a quoted string: 
    //!   <escape><quote>  -> <quote> 
    //!   <escape>n        -> new line 
    //!   <escape><escape> -> <escape> 
    //!
    //! eg. In the following example, 
    //! strings are shown as they would appear in a text file 
    //! or printed to console, between square brackets, rather 
    //! than as a masl string literal to avoid confusion of 
    //! escapes and quotes. 
    //!      tokenize_quoted ( [Alexis,Aline,,"Wells, John Wellington (\"Sorcerer\")"], ",", "\"", "\\" ) = 
    //!               { [Alexis], [Aline], [], [Wells, John Wellington ("Sorcerer")] }
    function tokenize_quoted ( input      : in anonymous string, 
                               separators : in anonymous set of anonymous character,
                               quotes     : in anonymous set of anonymous character,
                               escapes    : in anonymous set of anonymous character )
                               return          anonymous sequence of anonymous string;
                               
    //! Tokenize a string as if it is a number of lines, each 
    //! one of which has comma separated fields. Quoting and 
    //! escaping within fields works as for the single argument 
    //! version of tokenise_quoted. The result is a sequence within a sequence, containing lines and fields.
    //! eg.
    //!  Fred Bloggs, "1, Any Street", Sometown
    //!  John Doe, "99, Lost Road", Nowhere
    //! would tokenize to { { "Fred Bloggs", "1, Any Street", "Sometown" }, 
    //!                     { "John Doe", "99, Lost Road", "Nowhere" } }

    function parse_csv ( input : in anonymous string )
                         return     anonymous sequence of anonymous sequence of anonymous string;


                               
    //! Split a string into lines separated by newline 
    //! characters. Blank lines are retained. 
    function split_lines ( input : in anonymous string )
                            return    anonymous sequence of anonymous string;

    //! Tokenize a string based on fixed size tokens. The sizes 
    //! of consecutive tokens are given by the sizes parameter. 
    //! If wrap is set to true, then on reaching the end of the 
    //! size list it will start again, otherwise it will stop 
    //! and any remaining characteres in the input will be 
    //! ignored. If allow_partial is set to true, then the 
    //! final token is allowed to be shorter than the specified 
    //! length, otherwise the remaining characters will be 
    //! ignored. 
    //! eg tokenize_fixed_size ( "aaaabbccccd", { 4, 2 }, true,  true )  = { "aaaa", "bb", "cccc", "d" }
    //!    tokenize_fixed_size ( "aaaabbccccd", { 4, 2 }, true,  false ) = { "aaaa", "bb", "cccc" }
    //!    tokenize_fixed_size ( "aaaabbccccd", { 4, 2 }, false, ?  )    = { "aaaa", "bb" }
    //!    tokenize_fixed_size ( "aaaab",       { 4, 2 }, false, true  ) = { "aaaa", "b" }
    //!    tokenize_fixed_size ( "aaaab",       { 4, 2 }, false, false ) = { "aaaa" }
    function tokenize_fixed_size ( input         : in anonymous string, 
                                   sizes         : in anonymous sequence of anonymous integer,
                                   wrap          : in anonymous boolean,
                                   allow_partial : in anonymous boolean )
                                   return             anonymous sequence of anonymous string;


    function decodeBase64 ( base64Encoded : in anonymous string ) return anonymous sequence of byte;

    function encodeBase64 ( rawData : in anonymous sequence of byte ) return anonymous string;

end domain;
pragma service_domain(true);
