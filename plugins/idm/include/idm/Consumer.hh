#ifndef InterDomainMessaging_Consumer_HH
#define InterDomainMessaging_Consumer_HH

#include "DataConsumer.hh"
#include "ServiceHandler.hh"

#include <memory>

namespace InterDomainMessaging {

    class Consumer {

      public:
        virtual void receive(std::shared_ptr<ServiceHandler> handler) = 0;
    };

} // namespace InterDomainMessaging

#endif
