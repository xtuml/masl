//
// UK Crown Copyright (c) 2009. All Rights Reserved
//

domain BinaryIO is

  type Justification is enum ( left, right );

  //! Opens a device to read binary data from a byte sequence
  service open_byte_reader ( bytes : in anonymous sequence of anonymous byte, reader : out anonymous device );

  //! Closes a device previously opened as a byte reader
  service close_byte_reader ( reader : out anonymous device );

  //! Opens a device to write binary data to a byte sequence 
  service open_byte_writer( writer : out anonymous device );

  //! Returns the byte sequence of all data previously written to the supplied byte writer.
  function get_bytes ( writer : in anonymous device ) return anonymous sequence of anonymous byte;

  //! Closes a device previously opened as a byte writer
  service close_byte_writer ( writer : out anonymous device );


  //! Creates a bitmap of the requested number of bits, with 
  //! every bit initialised to the supplied value.
  function create_bitmap ( bits   : in anonymous integer,
                           value  : in boolean ) 
                           return      anonymous sequence of anonymous boolean;

  //! Creates a bitmap of the requested number of bits, the 
  //! least significant bits initialised to the binary 
  //! representation of the supplied values, with 
  //! value[value'first] being the most significant byte. Any 
  //! bits which cannot be initialised from the supplied value 
  //! will be initialised to zero. Any unneeded bits from the 
  //! supplied value will be ignored. The resultant sequence of 
  //! boolean should be interpreted as result[result'first] 
  //! being the LSB. In other words, the LSB of 
  //! value[value'last] = result[result'first]. 
  //!
  //! eg create_bitmap (  5, 16#f4 ) will generate the bitmap                        '1 0100'
  //!    create_bitmap ( 10, 16#f4 ) will generate the bitmap                  '00 1111 0100'
  //!    create_bitmap ( 17, 16#80 & 16#f4 ) will generate the bitmap '0 1000 0000 1111 0100'

  function create_bitmap ( bits   : in anonymous integer,
                           value  : in anonymous sequence of anonymous byte )
                           return      anonymous sequence of anonymous boolean;



  //! Writes a bitmap to the file. value[value'first] is taken 
  //! to be the least significant bit (LSB), and bits are 
  //! written to file in order of MSB to LSB. If the total 
  //! number of bits does not completely fill a whole number of 
  //! bytes, then the value written will be padded with an 
  //! appropriate number of zero MSBs.
  //! 
  //! Note it is recommended that the create_bitmap functions above
  //! are used to initialise a bitmap value, as constructing a 
  //! sequence of boolean using the concatenation operator, the 
  //! LSB, being the first element of the sequence, will also 
  //! be the first term in the expression. Consequently, the 
  //! concatenation will appear to be in LSB to MSB order, the 
  //! reverse of what is intuitively expected. 
  //! 
  //! eg. value := true  & true  & false & false & 
  //!              false & true  & false & false & 
  //!              true; 
  //! is equivalent to the bitmap 1 0010 0011, and would be 
  //! written to the file (after padding) as 
  //! 0000 0001 0010 0011 (or hex 01 23).
  service write_bitmap ( file  : in anonymous device, 
                         value : in anonymous sequence of anonymous boolean ); 




  //! Writes an integer to the file. The value is written in 
  //! big-endian order, MSB first, and MSBs are padded with 0 
  //! bits up to the requested size for positive or zero 
  //! values. Negative values are written in 2's-complement 
  //! format, and padded with 1s. If the value does not fit in 
  //! the bytes available, the MSBs are truncated. 
  service write_integer ( file          : in anonymous device, 
                          value         : in anonymous long_integer, 
                          size_in_bytes : in anonymous integer );

  service write_integers ( file          : in anonymous device, 
                           value         : in anonymous sequence of anonymous long_integer, 
                           size_in_bytes : in anonymous integer );

  //! Equivalent to 'write_integer ( file, value, 8 );', 
  //! writing a 64-bit signed integer. 
  service write_integer_64 ( file  : in anonymous device, 
                             value : in anonymous long_integer );

  service write_integers_64 ( file  : in anonymous device, 
                              value : in anonymous sequence of anonymous long_integer );

  //! Equivalent to 'write_integer ( file, value, 4 );', 
  //! writing a 32-bit signed integer. The parameter is a 
  //! long_integer to allow 32 bit unsigned values to fit.
  service write_integer_32  ( file  : in anonymous device, 
                              value : in anonymous long_integer );

  service write_integers_32  ( file  : in anonymous device, 
                               value : in anonymous sequence of anonymous long_integer );

  //! Equivalent to 'write_integer ( file, value, 2 );', 
  //! writing a 16-bit signed integer. 
  service write_integer_16 ( file  : in anonymous device, 
                             value : in anonymous integer );

  service write_integers_16 ( file  : in anonymous device, 
                              value : in anonymous sequence of anonymous integer );

  //! Equivalent to 'write_integer ( file, value, 1 );', 
  //! writing a 16-bit signed integer. 
  service write_integer_8 ( file  : in anonymous device, 
                             value : in anonymous integer );

  service write_integers_8 ( file  : in anonymous device, 
                              value : in anonymous sequence of anonymous integer );

  //! Writes an integer to the file. The value is written in 
  //! little-endian order, LSB first, and MSBs are padded with 0 
  //! bits up to the requested size for positive or zero 
  //! values. Negative values are written in 2's-complement 
  //! format, and padded with 1s. If the value does not fit in 
  //! the bytes available, the MSBs are truncated. 
  service write_integer_le ( file          : in anonymous device, 
                             value         : in anonymous long_integer, 
                             size_in_bytes : in anonymous integer );

  service write_integers_le ( file          : in anonymous device, 
                              value         : in anonymous sequence of anonymous long_integer, 
                              size_in_bytes : in anonymous integer );

  //! Equivalent to 'write_integer_le ( file, value, 8 );', 
  //! writing a 64-bit signed integer. 
  service write_integer_64_le ( file  : in anonymous device, 
                                value : in anonymous long_integer );

  service write_integers_64_le ( file  : in anonymous device, 
                                 value : in anonymous sequence of anonymous long_integer );

  //! Equivalent to 'write_integer_le ( file, value, 4 );', 
  //! writing a 32-bit signed integer. 
  service write_integer_32_le ( file  : in anonymous device, 
                                value : in anonymous integer );

  service write_integers_32_le ( file  : in anonymous device, 
                                 value : in anonymous sequence of anonymous integer );

  //! Equivalent to 'write_integer_le ( file, value, 2 );', 
  //! writing a 16-bit signed integer. 
  service write_integer_16_le ( file  : in anonymous device, 
                                value : in anonymous integer );

  service write_integers_16_le ( file  : in anonymous device, 
                                 value : in anonymous sequence of anonymous integer );


  //! Writes a real to the file in 32 bit IEEE-754 format (Big Endian)
  service write_real_32 ( file  : in anonymous device,
                          value : in anonymous real );  

  service write_reals_32 ( file  : in anonymous device,
                           value : in anonymous sequence of anonymous real );  

  //! Writes a real to the file in 64 it IEEE-754 format (Big Endian)
  service write_real_64 ( file : in anonymous device,
                          value : in anonymous real );  

  service write_reals_64 ( file  : in anonymous device,
                           value : in anonymous sequence of anonymous real );  

  //! Writes a real to the file in 32 bit IEEE-754 format (Little Endian)
  service write_real_32_le ( file  : in anonymous device,
                             value : in anonymous real );  

  service write_reals_32_le ( file  : in anonymous device,
                              value : in anonymous sequence of anonymous real );  

  //! Writes a real to the file in 64 it IEEE-754 format (Little Endian)
  service write_real_64_le ( file  : in anonymous device,
                             value : in anonymous real );  

  service write_reals_64_le ( file  : in anonymous device,
                              value : in anonymous sequence of anonymous real );  

  //! Writes a single character to the file
  service write_character ( file  : in anonymous device,
                            value : in anonymous character );

  //! Writes the supplied string to the file, left or right 
  //! justifying it within a field of the requested length by 
  //! padding with the supplied character. If the supplied 
  //! string is longer than the requested length, it is 
  //! truncated. 
  //! 
  //! eg. write_string(file,"Hello World!,15,left,'.');  would write "Hello World!..." 
  //! eg. write_string(file,"Hello World!,15,right,'.'); would write "...Hello World!" 
  //! eg. write_string(file,"Hello World!,5,right,'.');  would write "Hello" 
  service write_string ( file          : in anonymous device,
                         value         : in anonymous string,
                         length        : in anonymous integer,
                         justification : in Justification, 
                         pad           : in character );

  //! Equivalent to write_string(file,value,length,left,' ') 
  //! so left justified, padded with spaces on the right. 
  service write_string ( file   : in anonymous device,
                         value  : in anonymous string,
                         length : in anonymous integer );

  //! Writes the raw byte value to the file.
  service write_byte ( file  : in anonymous device,
                       value : in anonymous byte ); 

  //! Writes the raw byte values to the file.
  service write_bytes ( file  : in anonymous device,
                        value : in anonymous sequence of anonymous byte );


  //! Reads a bitmap from the file, using the same format as write_bitmap. Any padding bits are ignored.
  function read_bitmap ( file    : in anonymous device,
                         length  : in anonymous integer ) 
                         return       anonymous sequence of anonymous boolean; 

  //! Reads an integer from the file, using the same format as write_integer.
  function read_integer ( file          : in anonymous device,
                          size_in_bytes : in anonymous integer ) 
                          return             anonymous long_integer;

  function read_integers ( file          : in anonymous device,
                           size_in_bytes : in anonymous integer,
                           length        : in anonymous integer ) 
                           return             anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_unsigned_integer.
  function read_unsigned_integer ( file          : in anonymous device,
                          size_in_bytes : in anonymous integer ) 
                          return             anonymous long_integer;

  function read_unsigned_integers ( file          : in anonymous device,
                           size_in_bytes : in anonymous integer,
                           length        : in anonymous integer ) 
                           return             anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_integer_64
  function read_integer_64 ( file : in anonymous device ) 
                             return    anonymous long_integer;

  function read_integers_64 ( file   : in anonymous device,
                              length : in anonymous integer ) 
                              return      anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_unsigned_integer_32
  function read_unsigned_integer_32 ( file : in anonymous device ) 
                             return    anonymous long_integer;

  function read_unsigned_integers_32 ( file   : in anonymous device,
                              length : in anonymous integer ) 
                              return      anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_integer_32
  function read_integer_32 ( file : in anonymous device )
                             return    anonymous integer;

  function read_integers_32 ( file   : in anonymous device,
                              length : in anonymous integer )
                              return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer_16
  function read_unsigned_integer_16 ( file : in anonymous device )
                             return    anonymous integer;

  function read_unsigned_integers_16 ( file   : in anonymous device,
                              length : in anonymous integer )
                              return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer_16
  function read_integer_16 ( file : in anonymous device )
                             return    anonymous integer;

  function read_integers_16 ( file   : in anonymous device,
                              length : in anonymous integer )
                              return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer_8
  function read_integer_8 ( file : in anonymous device )
                             return    anonymous integer;

  function read_integers_8 ( file   : in anonymous device,
                              length : in anonymous integer )
                              return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer_8
  function read_unsigned_integer_8 ( file : in anonymous device )
                             return    anonymous integer;

  function read_unsigned_integers_8 ( file   : in anonymous device,
                              length : in anonymous integer )
                              return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer.
  function read_integer_le ( file          : in anonymous device,
                             size_in_bytes : in anonymous integer ) 
                             return             anonymous long_integer;

  function read_integers_le ( file          : in anonymous device,
                              size_in_bytes : in anonymous integer,
                              length        : in anonymous integer ) 
                              return             anonymous sequence of anonymous long_integer;

  function read_unsigned_integer_le ( file          : in anonymous device,
                                      size_in_bytes : in anonymous integer ) 
                                      return             anonymous long_integer;

  function read_unsigned_integers_le ( file          : in anonymous device,
                                       size_in_bytes : in anonymous integer,
                                       length        : in anonymous integer ) 
                                       return             anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_integer_64_le
  function read_integer_64_le ( file : in anonymous device ) 
                                return    anonymous long_integer;

  function read_integers_64_le ( file   : in anonymous device,
                                 length : in anonymous integer ) 
                                 return      anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_integer_32_le
  function read_unsigned_integer_32_le ( file : in anonymous device )
                                return    anonymous long_integer;

  function read_unsigned_integers_32_le ( file   : in anonymous device,
                                 length : in anonymous integer )
                                 return      anonymous sequence of anonymous long_integer;

  //! Reads an integer from the file, using the same format as write_integer_32_le
  function read_integer_32_le ( file : in anonymous device )
                                return    anonymous integer;

  function read_integers_32_le ( file   : in anonymous device,
                                 length : in anonymous integer )
                                 return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer_16_le
  function read_unsigned_integer_16_le ( file : in anonymous device )
                                return    anonymous integer;

  function read_unsigned_integers_16_le ( file   : in anonymous device,
                                 length : in anonymous integer )
                                 return      anonymous sequence of anonymous integer;

  //! Reads an integer from the file, using the same format as write_integer_16_le
  function read_integer_16_le ( file : in anonymous device )
                                return    anonymous integer;

  function read_integers_16_le ( file   : in anonymous device,
                                 length : in anonymous integer )
                                 return      anonymous sequence of anonymous integer;


  //! Reads an integer from the file, using the same format as write_real_64
  function read_real_64 ( file : in anonymous device ) 
                          return    anonymous real;

  function read_reals_64 ( file   : in anonymous device,
                           length : in anonymous integer ) 
                           return      anonymous sequence of anonymous real;

  //! Reads an integer from the file, using the same format as write_real_32
  function read_real_32 ( file : in anonymous device )
                          return    anonymous real;

  function read_reals_32 ( file   : in anonymous device,
                           length : in anonymous integer )
                           return      anonymous sequence of anonymous real;

  //! Reads an integer from the file, using the same format as write_real_64_le
  function read_real_64_le ( file : in anonymous device ) 
                             return    anonymous real;

  function read_reals_64_le ( file   : in anonymous device,
                              length : in anonymous integer ) 
                              return    anonymous sequence of anonymous real;

  //! Reads an integer from the file, using the same format as write_real_32_le
  function read_real_32_le ( file : in anonymous device )
                             return    anonymous real;

  function read_reals_32_le ( file   : in anonymous device,
                              length : in anonymous integer )
                              return      anonymous sequence of anonymous real;


  //! Reads a single character from the file
  function read_character ( file : in anonymous device ) 
                            return    anonymous character;

  //! Reads a string contained in a field of the requested size 
  //! from the file, in the same format as write_string. Any 
  //! padding characters are discarded in the result. 
  function read_string ( file          : in anonymous device,
                         length        : in anonymous integer,
                         justification : in Justification,
                         pad           : in character )
                         return             anonymous string;

  //! Reads a string contained in a field of the requested size 
  //! from the file, in the same format as write_string. Any 
  //! trailing spaces are discarded in the result. 
  function read_string ( file   : in anonymous device,
                         length : in anonymous integer )
                         return      anonymous string;

  //! Reads a string of the requested size from the file.  
  function read_raw_string ( file   : in anonymous device,
                             length : in anonymous integer )
                             return      anonymous string;

  //! Reads a single byte from the file.
  function read_byte ( file : in anonymous device )
                       return    anonymous byte; 

  //! Reads the requested number of bytes from the file.
  function read_bytes ( file   : in anonymous device,
                        length : in anonymous integer )
                        return      anonymous sequence of anonymous byte;

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
