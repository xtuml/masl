//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_DynamicSingleton_HH
#define SWA_DynamicSingleton_HH

#include "boost/function.hpp"

#include <iostream>

namespace SWA
{

template<class Singleton>
class DynamicSingleton
{
  public:
    typedef boost::function<Singleton&()> GetterFunction;

    static Singleton& getSingleton();
    static bool registerSingleton(GetterFunction getter);

  private:
    static GetterFunction& getSingletonGetter();
    GetterFunction getter;
};


template<class Singleton>
bool DynamicSingleton<Singleton>::registerSingleton(GetterFunction getter) 
{
  getSingletonGetter() = getter;
  return true;
}

template<class Singleton>
typename DynamicSingleton<Singleton>::GetterFunction& DynamicSingleton<Singleton>::getSingletonGetter() 
{
  static GetterFunction getter;
  return getter;
}

template<class Singleton>
Singleton& DynamicSingleton<Singleton>::getSingleton()
{
  static Singleton& singleton = getSingletonGetter()();
  return singleton;
}


}
#endif

