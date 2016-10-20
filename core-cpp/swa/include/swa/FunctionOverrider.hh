//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef FunctionOverrider_HH
#define FunctionOverrider_HH

namespace SWA
{

  template<class FunctionType>
  class FunctionOverrider
  {
    public:
      typedef FunctionType* FunctionPtr;

      FunctionOverrider ( FunctionPtr defaultFn )
        : defaultFn(defaultFn),
          overrideFn(0)
      {}

      FunctionPtr getFunction() const
      {
        return overrideFn?overrideFn:defaultFn;
      }

      void override ( FunctionPtr fn )
      {
        overrideFn = fn;
      }

      void cancelOverride()
      {
        overrideFn = 0;
      }

      bool isOverridden()
      {
        return overrideFn != 0; 
      }

    private:
      const FunctionPtr defaultFn;
      FunctionPtr overrideFn;

  };
}

#endif
