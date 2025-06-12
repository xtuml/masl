#ifndef InterDomainMessaging_Consumer_HH
#define InterDomainMessaging_Consumer_HH

#include "DataConsumer.hh"
#include "ServiceHandler.hh"

#include <memory>

namespace InterDomainMessaging {

    class Consumer : public std::enable_shared_from_this<Consumer>{

      public:
        virtual void receive(std::shared_ptr<ServiceHandler> handler) = 0;

        virtual void setProperty(const std::string& name, int value) {}
        virtual void setProperty(const std::string& name, std::string value) {}
        virtual void setProperty(const std::string& name, bool value) {}

    };

} // namespace InterDomainMessaging

#endif
