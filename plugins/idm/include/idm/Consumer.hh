#ifndef InterDomainMessaging_Consumer_HH
#define InterDomainMessaging_Consumer_HH

#include "DataConsumer.hh"

#include <nlohmann/json.hpp>

namespace InterDomainMessaging {

    class Consumer {

      public:
        Consumer(std::string topic);
        bool consumeOne(DataConsumer &dataConsumer);
        void receive();
    };

} // namespace InterDomainMessaging

#endif
