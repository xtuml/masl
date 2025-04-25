#ifndef InterDomainMessaging_ProcessHandler_HH
#define InterDomainMessaging_ProcessHandler_HH

#include "Consumer.hh"
#include "Producer.hh"
#include "ServiceHandler.hh"

#include "swa/DynamicSingleton.hh"
#include "swa/Process.hh"

#include <string>

namespace InterDomainMessaging {

    class ProcessHandler : public SWA::DynamicSingleton<ProcessHandler> {
      public:
        virtual std::shared_ptr<Consumer> createConsumer(std::string topic) = 0;

        virtual std::shared_ptr<Producer> createProducer(std::string topic) = 0;

        virtual void setConsumerConfig(std::string consumerId, std::string paramName, std::string paramValue) = 0;
        virtual void setConsumerConfig(std::string consumerId, std::string paramName, int paramValue) = 0;
        virtual void setConsumerConfig(std::string consumerId, std::string paramName, bool paramValue) = 0;

        bool registerServiceHandler(std::string topic, std::shared_ptr<ServiceHandler> handler);

        static std::string getTopicName(int domainId, int serviceId) {
            return SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
        }

        static ProcessHandler &getInstance();

      private:
        std::vector<std::shared_ptr<Consumer>> consumers;
    };

} // namespace InterDomainMessaging

#endif
