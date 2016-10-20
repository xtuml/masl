//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef SWA_ListenerPriority_HH
#define SWA_ListenerPriority_HH

namespace SWA
{
  class ListenerPriority
  {
    public:
      static const ListenerPriority& getMinimum();
      static const ListenerPriority& getLow();
      static const ListenerPriority& getNormal();
      static const ListenerPriority& getHigh();
      static const ListenerPriority& getMaximum();

      int getValue() const { return priority; }

    private:
      ListenerPriority(int priority);
      ListenerPriority(const ListenerPriority& low, const ListenerPriority& high );
      int priority;
  
  };

}


#endif
