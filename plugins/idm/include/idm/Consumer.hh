#ifndef InterDomainMessaging_Consumer_HH
#define InterDomainMessaging_Consumer_HH

#include "DataConsumer.hh"
#include "ServiceHandler.hh"

#include <nlohmann/json.hpp>

namespace InterDomainMessaging {

    class Consumer {

      public:
        Consumer(std::string topic);
        bool consumeOne(DataConsumer &dataConsumer);
        virtual void receive(std::shared_ptr<ServiceHandler> handler);
    };

} // namespace InterDomainMessaging

#endif
