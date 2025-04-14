//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef Binary_IO_Endian_HH
#define Binary_IO_Endian_HH

#include <boost/integer.hpp>
#include <boost/type_traits/is_signed.hpp>
#include <boost/type_traits/is_unsigned.hpp>
#include <boost/type_traits/is_floating_point.hpp>
#include <stdint.h>
#include <vector>
#include <bit>
namespace masld_BinaryIO
{
  namespace Endian
  {
    namespace
    {

        template<int bits> struct int_t  { typedef typename boost::int_t<bits>::exact type; };
        template<int bits> struct uint_t { typedef typename boost::uint_t<bits>::exact type; };
        template<int bits> struct float_t;

        template<> struct float_t<sizeof(float)*8>       { typedef float type; };
        template<> struct float_t<sizeof(double)*8>      { typedef double type; };

        template<int bits, std::endian ByteOrder> struct Bswap;

        template<> struct Bswap<64,std::endian::native> { static uint64_t bswap ( uint64_t v ) { return v; } };
        template<> struct Bswap<32,std::endian::native> { static uint32_t bswap ( uint32_t v ) { return v; } };
        template<> struct Bswap<16,std::endian::native> { static uint16_t bswap ( uint16_t v ) { return v; } };
        template<> struct Bswap<8 ,std::endian::native> { static uint8_t  bswap ( uint8_t  v ) { return v; } };

        template<std::endian ByteOrder> struct Bswap<64,ByteOrder> { static uint64_t bswap ( uint64_t v ) { return __builtin_bswap64(v); } };
        template<std::endian ByteOrder> struct Bswap<32,ByteOrder> { static uint32_t bswap ( uint32_t v ) { return __builtin_bswap32(v); } };
        template<std::endian ByteOrder> struct Bswap<16,ByteOrder> { static uint16_t bswap ( uint16_t v ) { return __builtin_bswap16(v); } };
        template<std::endian ByteOrder> struct Bswap<8 ,ByteOrder> { static uint8_t  bswap ( uint8_t  v ) { return v; } };

        template<int V> struct Int2Type { };

        enum { Signed_, Unsigned_, Floating_, Unknown_ };

        typedef Int2Type<Signed_>     Signed;
        typedef Int2Type<Unsigned_>   Unsigned;
        typedef Int2Type<Floating_>   Floating;
        typedef Int2Type<Unknown_>    Unknown;

        template<class T> struct Traits
        { 
          enum
          {
            value = boost::is_signed<T>::value         ? Signed_ :
                    boost::is_unsigned<T>::value       ? Unsigned_ :
                    boost::is_floating_point<T>::value ? Floating_ :
                                                         Unknown_
          };
          typedef Int2Type<value> type;
        };

    }


    template<std::endian ByteOrder>
    struct Convert
    {
      private:
      public:
        template<int bits, class Native>
        static typename uint_t<bits>::type toRaw ( Native value )
        {
          return toRaw<bits>(value,typename Traits<Native>::type());
        }

        template<int bits,template <class> class C, class Native>
        static std::vector<typename uint_t<bits>::type> toRaws ( const C<Native>& value )
        {
          std::vector<typename uint_t<bits>::type> result;
          result.reserve(value.size());
          for ( typename C<Native>::const_iterator it = value.begin(), end = value.end(); it != end; ++it )
          {
            result.push_back(toRaw<bits,Native>(*it));
          }
          return result;
        }  

        template<class Native,class Raw>
        static Native toNative ( Raw value )
        {
          return toNative<sizeof(Raw)*8,Native>(value,typename Traits<Native>::type(),typename Traits<Raw>::type());
        }

        template<class NativeC, class Intermediate, template <class> class RawC, class Raw>
        static NativeC toNatives ( const RawC<Raw>& value )
        {
          NativeC result;
          try_reserve(result,value.size());
          std::insert_iterator<NativeC> res(result,result.end());  
          for ( typename RawC<Raw>::const_iterator it = value.begin(), end = value.end(); it != end; ++it )
          {
            *res++= toNative<Intermediate>(*it);
          }
          return result;
        }  

        template<class NativeC, template <class> class RawC, class Raw>
        static NativeC toNatives ( const RawC<Raw>& value )
        {
          return toNatives<NativeC,typename NativeC::value_type,RawC,Raw>(value);
        }  

#if __GNUC__ > 4 || (__GNUC__ == 4 && __GNUC_MINOR__ > 1 )

        template<class NativeC, class Intermediate, template <class,class> class RawC, class Raw, template<class> class Alloc>
        static NativeC toNatives ( const RawC<Raw,Alloc<Raw> >& value )
        {
          NativeC result;
          try_reserve(result,value.size());
          std::insert_iterator<NativeC> res(result,result.end());  
          for ( typename RawC<Raw,Alloc<Raw> >::const_iterator it = value.begin(), end = value.end(); it != end; ++it )
          {
            *res++= toNative<Intermediate>(*it);
          }
          return result;
        }  

        template<class NativeC, template <class,class> class RawC, class Raw, template<class> class Alloc>
        static NativeC toNatives ( const RawC<Raw,Alloc<Raw> >& value )
        {
          return toNatives<NativeC,typename NativeC::value_type,RawC,Raw,Alloc>(value);
        }  

#endif

      private:

        template<int bits, class Native>
        static typename uint_t<bits>::type toRaw ( Native value, Signed )
        {
            union
            {
              typename int_t<bits>::type native;
              typename uint_t<bits>::type raw;
            };
            native = value;
            return Bswap<bits,ByteOrder>::bswap(raw);
        }

        template<int bits, class Native>
        static typename uint_t<bits>::type toRaw ( Native value, Unsigned )
        {
            union
            {
              typename uint_t<bits>::type native;
              typename uint_t<bits>::type raw;
            };
            native = value;
            return Bswap<bits,ByteOrder>::bswap(raw);
        }

        template<int bits, class Native>
        static typename uint_t<bits>::type toRaw ( Native value, Floating )
        {
            union
            {
              typename float_t<bits>::type native;
              typename uint_t<bits>::type raw;
            };
            native = value;
            return Bswap<bits,ByteOrder>::bswap(raw);
        }

        template<int bits, class T>
        static T toNative ( typename uint_t<bits>::type value, Signed, Unsigned )
        {
          union
          {
            typename int_t<bits>::type native;
            typename uint_t<bits>::type raw;
          };
          raw = Bswap<bits,ByteOrder>::bswap(value);
          return native;
        }

        template<int bits, class T>
        static T toNative ( typename uint_t<bits>::type value, Unsigned, Unsigned )
        {
          union
          {
            typename uint_t<bits>::type native;
            typename uint_t<bits>::type raw;
          };
          raw = Bswap<bits,ByteOrder>::bswap(value);
          return native;
        }

        template<int bits,class T>
        static T toNative ( typename uint_t<bits>::type value, Floating, Unsigned )
        {
          union
          {
            typename float_t<bits>::type native;
            typename uint_t<bits>::type raw;
          };
          raw = Bswap<bits,ByteOrder>::bswap(value);
          return native;
        }  

        template<class MFT, MFT mf> struct mem_fun_test;

        template<class C> static void try_reserve ( C& container, size_t bits, mem_fun_test<void(C::*)(typename C::size_type), &C::reserve>* = 0 )
        {
          container.reserve(bits);
        }

        template<class C, class S> static void try_reserve ( C& container, S size ) {}
    };

    typedef Convert<std::endian::big>    BigEndian;
    typedef Convert<std::endian::little> LittleEndian;

  }

}


#endif
