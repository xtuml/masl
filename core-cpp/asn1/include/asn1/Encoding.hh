//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef ASN1_Encoding_HH
#define ASN1_Encoding_HH

#include <boost/type_traits.hpp>

namespace ASN1
{
  namespace Encoding
  {
    // Can't put these into the class (as per C++ Templates 
    // The Complete Guide, Vandervoorde/Josuttis, as it won't 
    // build on gcc 3.2. Hide them in an anonymous namespace instead.
    namespace 
    { 
      namespace is_container
      {
        typedef char One;
        typedef struct { char a[2]; } Two;
        template<class C> static One test ( typename C::value_type*, typename C::const_iterator* ); 
        template<class C> static Two test (...); 
      }
    }
  
    template<class T>
    struct IsContainer
    {
      public:
        // Use enum rather than static const bool for backwards compatibility
        enum { value = (sizeof(is_container::test<T>(0,0)) == 1) };
    };

    template<class T> struct IsBoolean { enum { value = false }; };
    template<> struct IsBoolean<bool> { enum { value = true }; };

    template<class T> struct IsOctet { enum { value = sizeof(T)==1 && ! IsBoolean<T>::value }; };

    template<class T, bool>
    struct IsOctetContainerHelper
    {
      enum { value = false };
    };

    template<class T>
    struct IsOctetContainerHelper<T,true>
    {
      enum { value = IsOctet<typename T::value_type>::value };
    };

    template<class T>
    struct IsOctetContainer
    {
      enum { value = IsOctetContainerHelper<T,IsContainer<T>::value>::value };
    };


    template<class T, bool>
    struct IsBitContainerHelper
    {
      enum { value = false };
    };

    template<class T>
    struct IsBitContainerHelper<T,true>
    {
      enum { value = IsBoolean<typename T::value_type>::value };
    };

    template<class T>
    struct IsBitContainer
    {
      enum { value = IsBitContainerHelper<T,IsContainer<T>::value>::value };
    };


    template<int V> struct Int2Type { };

    enum { Null_, Boolean_, Integer_, Enumerated_, Real_, BitString_, OctetString_, SequenceOf_, Unknown_ };

    typedef Int2Type<Null_>        Null;
    typedef Int2Type<Boolean_>     Boolean;
    typedef Int2Type<Integer_>     Integer;
    typedef Int2Type<Enumerated_>  Enumerated;
    typedef Int2Type<Real_>        Real;
    typedef Int2Type<BitString_>   BitString;
    typedef Int2Type<OctetString_> OctetString;
    typedef Int2Type<SequenceOf_>  SequenceOf;
    typedef Int2Type<Unknown_>     Unknown;

    template<class T> struct Traits
    { 
      enum
      {
        value = boost::is_void<T>::value           ? Null_ :
                IsBoolean<T>::value                ? Boolean_ :
                boost::is_enum<T>::value           ? Enumerated_ :
                boost::is_integral<T>::value       ? Integer_ :
                boost::is_floating_point<T>::value ? Real_ :
                IsBitContainer<T>::value           ? BitString_ :
                IsOctetContainer<T>::value         ? OctetString_ :
                (boost::is_pointer<T>::value && 
                IsOctet<typename boost::remove_pointer<T>::type>::value)
                                                   ? OctetString_ :
                (boost::is_array<T>::value && 
                IsOctet<typename boost::remove_extent<T>::type>::value)
                                                   ? OctetString_ :
                IsContainer<T>::value              ? SequenceOf_ :
                                                     Unknown_
      };
      typedef Int2Type<value> type;
    };
  }
}

#endif
