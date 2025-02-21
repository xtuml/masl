//
// File: NativeStubs.cc
//
// UK Crown Copyright (c) 2012. All Rights Reserved
//
#include <stdint.h>
#include <sstream>
#include "BinaryIO_OOA/__BinaryIO_services.hh"
#include "BinaryIO_OOA/__BinaryIO_types.hh"
#include "swa/Device.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include "swa/IOError.hh"
#include "boost/dynamic_bitset.hpp"
#include <memory>
#include "boost/make_shared.hpp"

#include "endian.hh"

namespace masld_BinaryIO
{

  void masls_open_byte_reader ( const ::SWA::Sequence<uint8_t>& maslp_bytes,
                                SWA::Device&                    maslp_reader )
  {
    maslp_reader.setInputStream(std::make_shared<std::istringstream>(std::string(maslp_bytes.begin(),maslp_bytes.end())));
  }

  const bool localServiceRegistration_masls_open_byte_reader = interceptor_masls_open_byte_reader::instance().registerLocal( &masls_open_byte_reader );

  void masls_close_byte_reader ( SWA::Device& maslp_reader )
  {
    maslp_reader.clearInputStream();
  }

  const bool localServiceRegistration_masls_close_byte_reader = interceptor_masls_close_byte_reader::instance().registerLocal( &masls_close_byte_reader );

  void masls_open_byte_writer ( SWA::Device& maslp_writer )
  {
    maslp_writer.setOutputStream(std::make_shared<std::ostringstream>());
  }

  const bool localServiceRegistration_masls_open_byte_writer = interceptor_masls_open_byte_writer::instance().registerLocal( &masls_open_byte_writer );

  ::SWA::Sequence<uint8_t> masls_get_bytes ( const SWA::Device& maslp_writer )
  {
    std::shared_ptr<std::ostringstream> input = maslp_writer.getOutputStream<std::ostringstream>();
    if ( !input ) throw SWA::ProgramError("Not a byte_writer device");
    const std::string& str = input->str();
    return SWA::Sequence<uint8_t>(str.begin(),str.end());
  }

  void masls_close_byte_writer ( SWA::Device& maslp_writer )
  {
    maslp_writer.clearOutputStream();
  }

  const bool localServiceRegistration_masls_close_byte_writer = interceptor_masls_close_byte_writer::instance().registerLocal( &masls_close_byte_writer );

  ::SWA::Sequence<bool> masls_create_bitmap ( int32_t maslp_bits,
                                              bool    maslp_value )
  {
    return ::SWA::Sequence<bool>(std::vector<bool>(maslp_bits,maslp_value));
  }

  ::SWA::Sequence<bool> masls_overload1_create_bitmap ( int32_t                         maslp_bits,
                                                        const ::SWA::Sequence<uint8_t>& maslp_value )
  {
    boost::dynamic_bitset<uint8_t> value(maslp_value.rbegin(),maslp_value.rend());

    std::vector<bool> result;
    result.reserve(maslp_bits);

    for ( int32_t bit = 0; bit != maslp_bits; ++bit )
    {
      result.push_back(value[bit]);
    }

    return result;
  }

  void masls_write_bitmap ( const ::SWA::Device&         maslp_file,
                            const ::SWA::Sequence<bool>& maslp_value )
  {
    boost::dynamic_bitset<uint8_t> value;
    for ( ::SWA::Sequence<bool>::const_iterator it = maslp_value.begin(), end = maslp_value.end(); it != end; ++it )
    {
      value.push_back(*it);
    }
    std::string buffer(value.num_blocks(),0x00);
    boost::to_block_range(value,buffer.rbegin());
    maslp_file.write_raw(buffer);    
  }

  const bool localServiceRegistration_masls_write_bitmap = interceptor_masls_write_bitmap::instance().registerLocal( &masls_write_bitmap );

  void masls_write_integer ( const ::SWA::Device& maslp_file,
                             int64_t              maslp_value,
                             int32_t              maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: masls_write_integer_8(maslp_file, maslp_value ); break;
      case 2: masls_write_integer_16(maslp_file, maslp_value ); break;
      case 4: masls_write_integer_32(maslp_file, maslp_value ); break;
      case 8: masls_write_integer_64(maslp_file, maslp_value ); break;
      default: 
      {
        std::string buffer;
        buffer.reserve(maslp_size_in_bytes);
        for ( int i = maslp_size_in_bytes-1; i >= 0; --i )
        {
          buffer.push_back(( maslp_value >> i*8 ) & 0xFF);
        }
        maslp_file.write_raw(buffer);   
      } 
    }
  }

  const bool localServiceRegistration_masls_write_integer = interceptor_masls_write_integer::instance().registerLocal( &masls_write_integer );

  void masls_write_integers ( const ::SWA::Device&            maslp_file,
                              const ::SWA::Sequence<int64_t>& maslp_value,
                              int32_t                         maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: masls_write_integers_8(maslp_file, maslp_value ); break;
      case 2: masls_write_integers_16(maslp_file, maslp_value ); break;
      case 4: masls_write_integers_32(maslp_file, maslp_value ); break;
      case 8: masls_write_integers_64(maslp_file, maslp_value ); break;
      default: 
      {
        std::string buffer;

        buffer.reserve(maslp_size_in_bytes * maslp_value.size());
        for ( ::SWA::Sequence<int64_t>::const_iterator it = maslp_value.begin(), end = maslp_value.end(); it != end; ++it )
        {
          for ( int i = maslp_size_in_bytes-1; i >= 0; --i )
          {
            buffer.push_back(( (*it) >> i*8 ) & 0xFF);
          }
        }
        maslp_file.write_raw(buffer); 
      } 
    }  
  }

  const bool localServiceRegistration_masls_write_integers = interceptor_masls_write_integers::instance().registerLocal( &masls_write_integers );

  void masls_write_integer_64 ( const ::SWA::Device& maslp_file,
                                int64_t              maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaw<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_64 = interceptor_masls_write_integer_64::instance().registerLocal( &masls_write_integer_64 );

  void masls_write_integers_64 ( const ::SWA::Device&            maslp_file,
                                 const ::SWA::Sequence<int64_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaws<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_64 = interceptor_masls_write_integers_64::instance().registerLocal( &masls_write_integers_64 );


  void masls_write_integer_32 ( const ::SWA::Device& maslp_file,
                                int64_t              maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaw<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_32 = interceptor_masls_write_integer_32::instance().registerLocal( &masls_write_integer_32 );

  void masls_write_integers_32 ( const ::SWA::Device&            maslp_file,
                                 const ::SWA::Sequence<int64_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaws<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_32 = interceptor_masls_write_integers_32::instance().registerLocal( &masls_write_integers_32 );


  void masls_write_integer_16 ( const ::SWA::Device& maslp_file,
                                int32_t              maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaw<16>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_16 = interceptor_masls_write_integer_16::instance().registerLocal( &masls_write_integer_16 );

  void masls_write_integers_16 ( const ::SWA::Device&            maslp_file,
                                 const ::SWA::Sequence<int32_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaws<16>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_16 = interceptor_masls_write_integers_16::instance().registerLocal( &masls_write_integers_16 );

  void masls_write_integer_8 ( const ::SWA::Device& maslp_file,
                               int32_t              maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaw<8>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_8 = interceptor_masls_write_integer_8::instance().registerLocal( &masls_write_integer_8 );

  void masls_write_integers_8 ( const ::SWA::Device&            maslp_file,
                                const ::SWA::Sequence<int32_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaws<8>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_8 = interceptor_masls_write_integers_8::instance().registerLocal( &masls_write_integers_8 );

  void masls_write_integer_le ( const ::SWA::Device& maslp_file,
                                int64_t              maslp_value,
                                int32_t              maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: masls_write_integer_8    (maslp_file, maslp_value ); break;
      case 2: masls_write_integer_16_le(maslp_file, maslp_value ); break;
      case 4: masls_write_integer_32_le(maslp_file, maslp_value ); break;
      case 8: masls_write_integer_64_le(maslp_file, maslp_value ); break;
      default: 
      {
        std::string buffer;

        buffer.reserve(maslp_size_in_bytes);
        for ( int i = 0; i < maslp_size_in_bytes; ++i )
        {
          buffer.push_back(( maslp_value >> i*8 ) & 0xFF);
        }
        maslp_file.write_raw(buffer);    
      } 
    }
  }

  const bool localServiceRegistration_masls_write_integer_le = interceptor_masls_write_integer_le::instance().registerLocal( &masls_write_integer_le );

  void masls_write_integers_le ( const ::SWA::Device&            maslp_file,
                                 const ::SWA::Sequence<int64_t>& maslp_value,
                                 int32_t                         maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: masls_write_integers_8    (maslp_file, maslp_value ); break;
      case 2: masls_write_integers_16_le(maslp_file, maslp_value ); break;
      case 4: masls_write_integers_32_le(maslp_file, maslp_value ); break;
      case 8: masls_write_integers_64_le(maslp_file, maslp_value ); break;
      default: 
      {
        std::string buffer;

        buffer.reserve(maslp_size_in_bytes * maslp_value.size());
        for ( ::SWA::Sequence<int64_t>::const_iterator it = maslp_value.begin(), end = maslp_value.end(); it != end; ++it )
        {
          for ( int i = 0; i < maslp_size_in_bytes; ++i )
          {
            buffer.push_back(( (*it) >> i*8 ) & 0xFF);
          }
        }
        maslp_file.write_raw(buffer);  
      }
    }  
  }

  const bool localServiceRegistration_masls_write_integers_le = interceptor_masls_write_integers_le::instance().registerLocal( &masls_write_integers_le );

  void masls_write_integer_64_le ( const ::SWA::Device& maslp_file,
                                   int64_t              maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaw<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_64_le = interceptor_masls_write_integer_64_le::instance().registerLocal( &masls_write_integer_64_le );

  void masls_write_integers_64_le ( const ::SWA::Device&            maslp_file,
                                    const ::SWA::Sequence<int64_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaws<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_64_le = interceptor_masls_write_integers_64_le::instance().registerLocal( &masls_write_integers_64_le );


  void masls_write_integer_32_le ( const ::SWA::Device& maslp_file,
                                   int32_t              maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaw<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_32_le = interceptor_masls_write_integer_32_le::instance().registerLocal( &masls_write_integer_32_le );

  void masls_write_integers_32_le ( const ::SWA::Device&            maslp_file,
                                    const ::SWA::Sequence<int32_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaws<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_32_le = interceptor_masls_write_integers_32_le::instance().registerLocal( &masls_write_integers_32_le );

  void masls_write_integer_16_le ( const ::SWA::Device& maslp_file,
                                   int32_t              maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaw<16>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integer_16_le = interceptor_masls_write_integer_16_le::instance().registerLocal( &masls_write_integer_16_le );

  void masls_write_integers_16_le ( const ::SWA::Device&            maslp_file,
                                    const ::SWA::Sequence<int32_t>& maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaws<16>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_integers_16_le = interceptor_masls_write_integers_16_le::instance().registerLocal( &masls_write_integers_16_le );

  void masls_write_real_32 ( const ::SWA::Device& maslp_file,
                             double               maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaw<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_real_32 = interceptor_masls_write_real_32::instance().registerLocal( &masls_write_real_32 );

  void masls_write_reals_32 ( const ::SWA::Device&           maslp_file,
                              const ::SWA::Sequence<double>& maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaws<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_reals_32 = interceptor_masls_write_reals_32::instance().registerLocal( &masls_write_reals_32 );

  void masls_write_real_64 ( const ::SWA::Device& maslp_file,
                             double               maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaw<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_real_64 = interceptor_masls_write_real_64::instance().registerLocal( &masls_write_real_64 );

  void masls_write_reals_64 ( const ::SWA::Device&           maslp_file,
                              const ::SWA::Sequence<double>& maslp_value )
  {
    maslp_file.write_raw(Endian::BigEndian::toRaws<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_reals_64 = interceptor_masls_write_reals_64::instance().registerLocal( &masls_write_reals_64 );

  void masls_write_real_32_le ( const ::SWA::Device& maslp_file,
                                double               maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaw<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_real_32_le = interceptor_masls_write_real_32_le::instance().registerLocal( &masls_write_real_32_le );

  void masls_write_reals_32_le ( const ::SWA::Device&           maslp_file,
                                 const ::SWA::Sequence<double>& maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaws<32>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_reals_32_le = interceptor_masls_write_reals_32_le::instance().registerLocal( &masls_write_reals_32_le );

  void masls_write_real_64_le ( const ::SWA::Device& maslp_file,
                                double               maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaw<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_real_64_le = interceptor_masls_write_real_64_le::instance().registerLocal( &masls_write_real_64_le );

  void masls_write_reals_64_le ( const ::SWA::Device&           maslp_file,
                                 const ::SWA::Sequence<double>& maslp_value )
  {
    maslp_file.write_raw(Endian::LittleEndian::toRaws<64>(maslp_value));
  }

  const bool localServiceRegistration_masls_write_reals_64_le = interceptor_masls_write_reals_64_le::instance().registerLocal( &masls_write_reals_64_le );

  void masls_write_character ( const ::SWA::Device& maslp_file,
                               char                 maslp_value )
  {
    maslp_file.write_raw(maslp_value);
  }

  const bool localServiceRegistration_masls_write_character = interceptor_masls_write_character::instance().registerLocal( &masls_write_character );

  void masls_write_string ( const ::SWA::Device&       maslp_file,
                            const ::SWA::String&       maslp_value,
                            int32_t                    maslp_length,
                            const maslt_Justification& maslp_justification,
                            char                       maslp_pad )
  {
    std::string value = maslp_value.s_str();

    if ( value.size() > ::SWA::String::size_type(maslp_length) ) value = value.substr(0,maslp_length);
    else if ( value.size() < ::SWA::String::size_type(maslp_length) )
    {
      std::string pad = std::string(maslp_length-value.size(),maslp_pad);
      if ( maslp_justification == maslt_Justification::masle_left )
      {
        value = value + pad;
      }
      else
      {
        value = pad + value;
      }
    }
    maslp_file.write_raw(value);    
  }

  const bool localServiceRegistration_masls_write_string = interceptor_masls_write_string::instance().registerLocal( &masls_write_string );

  void masls_overload1_write_string ( const ::SWA::Device& maslp_file,
                                      const ::SWA::String& maslp_value,
                                      int32_t              maslp_length )
  {
    masls_write_string ( maslp_file, maslp_value, maslp_length, maslt_Justification::masle_left, ' ' );
  }

  const bool localServiceRegistration_masls_overload1_write_string = interceptor_masls_overload1_write_string::instance().registerLocal( &masls_overload1_write_string );

  void masls_write_byte ( const ::SWA::Device& maslp_file,
                          uint8_t              maslp_value )
  {
    maslp_file.write_raw(maslp_value);
  }

  const bool localServiceRegistration_masls_write_byte = interceptor_masls_write_byte::instance().registerLocal( &masls_write_byte );

  void masls_write_bytes ( const ::SWA::Device&            maslp_file,
                           const ::SWA::Sequence<uint8_t>& maslp_value )
  {
    maslp_file.write_raw(maslp_value.getData());    
  }

  const bool localServiceRegistration_masls_write_bytes = interceptor_masls_write_bytes::instance().registerLocal( &masls_write_bytes );

  ::SWA::Sequence<bool> masls_read_bitmap ( const ::SWA::Device& maslp_file,
                                            int32_t              maslp_length )
  {
    int byteLength = (maslp_length+7)/8;
    std::vector<char> buffer = maslp_file.read_raw_bytes(byteLength);

    boost::dynamic_bitset<uint8_t> value(buffer.rbegin(),buffer.rend());

    std::vector<bool> result;
    result.reserve(maslp_length);

    for ( int32_t bit = 0; bit != maslp_length; ++bit )
    {
      result.push_back(value[bit]);
    }

    return result;
  }

  int64_t masls_read_integer ( const ::SWA::Device& maslp_file,
                               int32_t              maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_integer_8  ( maslp_file );
      case 2: return masls_read_integer_16 ( maslp_file ); 
      case 4: return masls_read_integer_32 ( maslp_file ); 
      case 8: return masls_read_integer_64 ( maslp_file ); 
      default: 
      {

        std::vector<char> buffer = maslp_file.read_raw_bytes(maslp_size_in_bytes);
        std::vector<char>::const_iterator it = buffer.begin();

        int64_t result = (it != buffer.end() && (*it & 0x80) ) ? -1 : 0;
        while ( maslp_size_in_bytes-- )
        {
          result <<= 8;
          result |= (*it++ & 0xFF);
        }
        return result;
      }
    }
  }

  ::SWA::Sequence<int64_t> masls_read_integers ( const ::SWA::Device& maslp_file,
                                                 int32_t              maslp_size_in_bytes,
                                                 int32_t              maslp_length )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_integers_8  ( maslp_file, maslp_length );
      case 2: return masls_read_integers_16 ( maslp_file, maslp_length ); 
      case 4: return masls_read_integers_32 ( maslp_file, maslp_length ); 
      case 8: return masls_read_integers_64 ( maslp_file, maslp_length ); 
      default: 
      {
        ::SWA::Sequence<int64_t> result;
        result.reserve(maslp_length);
        for ( int i = 0; i < maslp_length; ++i )
        {
          result.push_back(masls_read_integer(maslp_file,maslp_size_in_bytes));
        }
      }
    }
  }

  int64_t masls_read_unsigned_integer ( const ::SWA::Device& maslp_file,
                                int32_t              maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_unsigned_integer_8  ( maslp_file );
      case 2: return masls_read_unsigned_integer_16 ( maslp_file ); 
      case 4: return masls_read_unsigned_integer_32 ( maslp_file ); 
      case 8: return masls_read_integer_64  ( maslp_file ); 
      default: 
      {
        std::vector<char> buffer = maslp_file.read_raw_bytes(maslp_size_in_bytes);
        std::vector<char>::const_iterator it = buffer.begin();

        int64_t result = 0;
        while ( maslp_size_in_bytes-- )
        {
          result <<= 8;
          result |= (*it++ & 0xFF);
        }
        return result;
      }
    }
  }

  ::SWA::Sequence<int64_t> masls_read_unsigned_integers ( const ::SWA::Device& maslp_file,
                                                  int32_t              maslp_size_in_bytes,
                                                  int32_t              maslp_length )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_unsigned_integers_8  ( maslp_file, maslp_length );
      case 2: return masls_read_unsigned_integers_16 ( maslp_file, maslp_length ); 
      case 4: return masls_read_unsigned_integers_32 ( maslp_file, maslp_length ); 
      case 8: return masls_read_integers_64  ( maslp_file, maslp_length ); 
      default: 
      {
        ::SWA::Sequence<int64_t> result;
        result.reserve(maslp_length);
        for ( int i = 0; i < maslp_length; ++i )
        {
          result.push_back(masls_read_unsigned_integer(maslp_file,maslp_size_in_bytes));
        }
      }
    }
  }

  int64_t masls_read_integer_64 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<int64_t>(maslp_file.read_raw<uint64_t>());
  }

  ::SWA::Sequence<int64_t> masls_read_integers_64 ( const ::SWA::Device& maslp_file,
                                                    int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int64_t> >(maslp_file.read_raw<uint64_t>(maslp_length));
  }

  int64_t masls_read_unsigned_integer_32 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<uint32_t>(maslp_file.read_raw<uint32_t>());
  }

  ::SWA::Sequence<int64_t> masls_read_unsigned_integers_32 ( const ::SWA::Device& maslp_file,
                                                     int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int64_t>,uint32_t>(maslp_file.read_raw<uint32_t>(maslp_length));
  }

  int32_t masls_read_integer_32 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<int32_t>(maslp_file.read_raw<uint32_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_integers_32 ( const ::SWA::Device& maslp_file,
                                                    int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int32_t> >(maslp_file.read_raw<uint32_t>(maslp_length));
  }

  int32_t masls_read_unsigned_integer_16 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<uint16_t>(maslp_file.read_raw<uint16_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_unsigned_integers_16 ( const ::SWA::Device& maslp_file,
                                                     int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int32_t>,uint16_t>(maslp_file.read_raw<uint16_t>(maslp_length));
  }

  int32_t masls_read_integer_16 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<int32_t>(maslp_file.read_raw<uint16_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_integers_16 ( const ::SWA::Device& maslp_file,
                                                    int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int32_t> >(maslp_file.read_raw<uint16_t>(maslp_length));
  }

  int32_t masls_read_integer_8 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<int32_t>(maslp_file.read_raw<uint8_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_integers_8 ( const ::SWA::Device& maslp_file,
                                                   int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int32_t> >(maslp_file.read_raw<uint8_t>(maslp_length));
  }

  int32_t masls_read_unsigned_integer_8 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<uint8_t>(maslp_file.read_raw<uint8_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_unsigned_integers_8 ( const ::SWA::Device& maslp_file,
                                                    int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<int32_t>,uint8_t>(maslp_file.read_raw<uint8_t>(maslp_length));
  }

  int64_t masls_read_integer_le ( const ::SWA::Device& maslp_file,
                                  int32_t              maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_integer_8     ( maslp_file );
      case 2: return masls_read_integer_16_le ( maslp_file ); 
      case 4: return masls_read_integer_32_le ( maslp_file ); 
      case 8: return masls_read_integer_64_le ( maslp_file ); 
      default: 
      {
        std::vector<char> buffer = maslp_file.read_raw_bytes(maslp_size_in_bytes);
        std::vector<char>::const_reverse_iterator it = buffer.rbegin();

        int64_t result = (it != buffer.rend() && (*it & 0x80) ) ? -1 : 0;
        while ( maslp_size_in_bytes-- )
        {
          result <<= 8;
          result |= (*it++ & 0xFF);
        }
        return result;
      }
    }
  }

  ::SWA::Sequence<int64_t> masls_read_integers_le ( const ::SWA::Device& maslp_file,
                                                    int32_t              maslp_size_in_bytes,
                                                    int32_t              maslp_length )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_integers_8     ( maslp_file, maslp_length );
      case 2: return masls_read_integers_16_le ( maslp_file, maslp_length ); 
      case 4: return masls_read_integers_32_le ( maslp_file, maslp_length ); 
      case 8: return masls_read_integers_64_le ( maslp_file, maslp_length ); 
      default: 
      {
        ::SWA::Sequence<int64_t> result;
        result.reserve(maslp_length);
        for ( int i = 0; i < maslp_length; ++i )
        {
          result.push_back(masls_read_integer_le(maslp_file,maslp_size_in_bytes));
        }
      }
    }
  }

  int64_t masls_read_unsigned_integer_le ( const ::SWA::Device& maslp_file,
                                           int32_t              maslp_size_in_bytes )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_unsigned_integer_8     ( maslp_file );
      case 2: return masls_read_unsigned_integer_16_le ( maslp_file ); 
      case 4: return masls_read_unsigned_integer_32_le ( maslp_file ); 
      case 8: return masls_read_integer_64_le          ( maslp_file ); 
      default: 
      {
        std::vector<char> buffer = maslp_file.read_raw_bytes(maslp_size_in_bytes);
        std::vector<char>::const_reverse_iterator it = buffer.rbegin();

        int64_t result = (it != buffer.rend() && (*it & 0x80) ) ? -1 : 0;
        while ( maslp_size_in_bytes-- )
        {
          result <<= 8;
          result |= (*it++ & 0xFF);
        }
        return result;
      }
    }
  }

  ::SWA::Sequence<int64_t> masls_read_unsigned_integers_le ( const ::SWA::Device& maslp_file,
                                                             int32_t              maslp_size_in_bytes,
                                                             int32_t              maslp_length )
  {
    switch ( maslp_size_in_bytes )
    {
      case 1: return masls_read_unsigned_integers_8     ( maslp_file, maslp_length );
      case 2: return masls_read_unsigned_integers_16_le ( maslp_file, maslp_length ); 
      case 4: return masls_read_unsigned_integers_32_le ( maslp_file, maslp_length ); 
      case 8: return masls_read_integers_64_le ( maslp_file, maslp_length ); 
      default: 
      {
        ::SWA::Sequence<int64_t> result;
        result.reserve(maslp_length);
        for ( int i = 0; i < maslp_length; ++i )
        {
          result.push_back(masls_read_integer_le(maslp_file,maslp_size_in_bytes));
        }
      }
    }
  }


  int64_t masls_read_integer_64_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<int64_t>(maslp_file.read_raw<uint64_t>());
  }

  ::SWA::Sequence<int64_t> masls_read_integers_64_le ( const ::SWA::Device& maslp_file,
                                                       int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<int64_t> >(maslp_file.read_raw<uint64_t>(maslp_length));
  }

  int64_t masls_read_unsigned_integer_32_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<uint32_t>(maslp_file.read_raw<uint32_t>());
  }

  ::SWA::Sequence<int64_t> masls_read_unsigned_integers_32_le ( const ::SWA::Device& maslp_file,
                                                        int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<int64_t>,uint32_t>(maslp_file.read_raw<uint32_t>(maslp_length));
  }

  int32_t masls_read_integer_32_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<int32_t>(maslp_file.read_raw<uint32_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_integers_32_le ( const ::SWA::Device& maslp_file,
                                                       int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<int32_t> >(maslp_file.read_raw<uint32_t>(maslp_length));
  }

  int32_t masls_read_unsigned_integer_16_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<uint16_t>(maslp_file.read_raw<uint16_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_unsigned_integers_16_le ( const ::SWA::Device& maslp_file,
                                                        int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<int32_t>,uint16_t>(maslp_file.read_raw<uint16_t>(maslp_length));
  }

  int32_t masls_read_integer_16_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<int32_t>(maslp_file.read_raw<uint16_t>());
  }

  ::SWA::Sequence<int32_t> masls_read_integers_16_le ( const ::SWA::Device& maslp_file,
                                                       int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<int32_t> >(maslp_file.read_raw<uint16_t>(maslp_length));
  }

  double masls_read_real_64 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<double>(maslp_file.read_raw<uint64_t>());
  }

  ::SWA::Sequence<double> masls_read_reals_64 ( const ::SWA::Device& maslp_file,
                                                int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<double> >(maslp_file.read_raw<uint64_t>(maslp_length));
  }

  double masls_read_real_32 ( const ::SWA::Device& maslp_file )
  {
    return Endian::BigEndian::toNative<double>(maslp_file.read_raw<uint32_t>());
  }

  ::SWA::Sequence<double> masls_read_reals_32 ( const ::SWA::Device& maslp_file,
                                                int32_t              maslp_length )
  {
    return Endian::BigEndian::toNatives<SWA::Sequence<double> >(maslp_file.read_raw<uint32_t>(maslp_length));
  }

  double masls_read_real_64_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<double>(maslp_file.read_raw<uint64_t>());
  }

  ::SWA::Sequence<double> masls_read_reals_64_le ( const ::SWA::Device& maslp_file,
                                                   int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<double> >(maslp_file.read_raw<uint64_t>(maslp_length));
  }

  double masls_read_real_32_le ( const ::SWA::Device& maslp_file )
  {
    return Endian::LittleEndian::toNative<double>(maslp_file.read_raw<uint32_t>());
  }

  ::SWA::Sequence<double> masls_read_reals_32_le ( const ::SWA::Device& maslp_file,
                                                   int32_t              maslp_length )
  {
    return Endian::LittleEndian::toNatives<SWA::Sequence<double> >(maslp_file.read_raw<uint32_t>(maslp_length));
  }

  char masls_read_character ( const ::SWA::Device& maslp_file )
  {
    return maslp_file.read_raw_char();
  }

  ::SWA::String masls_read_string ( const ::SWA::Device&       maslp_file,
                                    int32_t                    maslp_length,
                                    const maslt_Justification& maslp_justification,
                                    char                       maslp_pad )
  {
    std::string result = maslp_file.read_raw_string(maslp_length);
    if ( maslp_justification == maslt_Justification::masle_left )
    {
      result = result.substr(0,result.find_last_not_of(maslp_pad)+1);
    }
    else
    {
      result = result.substr(result.find_first_not_of(maslp_pad));
    }

    return result;
  }

  ::SWA::String masls_overload1_read_string ( const ::SWA::Device& maslp_file,
                                              int32_t              maslp_length )
  {
    return masls_read_string ( maslp_file, maslp_length, maslt_Justification::masle_left, ' ' ); 
  }

  ::SWA::String masls_read_raw_string ( const ::SWA::Device& maslp_file,
                                        int32_t              maslp_length )
  {
    return ::SWA::String(maslp_file.read_raw_string(maslp_length));
  }

  uint8_t masls_read_byte ( const ::SWA::Device& maslp_file )
  {
    return maslp_file.read_raw_char();
  }

  ::SWA::Sequence<uint8_t> masls_read_bytes ( const ::SWA::Device& maslp_file,
                                              int32_t              maslp_length )
  {
    return ::SWA::Sequence<uint8_t>(maslp_file.read_raw_bytes(maslp_length));
  }

}
