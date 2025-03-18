#ifndef InterDomainMessaging_ProcessHandler_HH
#define InterDomainMessaging_ProcessHandler_HH

#include "Consumer.hh"
#include "Producer.hh"
#include "ServiceHandler.hh"

#include "swa/DynamicSingleton.hh"
#include "swa/Process.hh"

namespace InterDomainMessaging {

    class ProcessHandler : public SWA::DynamicSingleton<ProcessHandler> {
      public:
        std::unique_ptr<Consumer> createConsumer(std::string topic) {
            return std::make_unique<Consumer>(topic);
        }

        std::unique_ptr<Producer> createProducer(std::string topic) {
            return std::make_unique<Producer>(topic);
        }

        bool registerServiceHandler(std::string topic, std::shared_ptr<ServiceHandler> handler);

        ServiceHandler &getServiceHandler(std::string topic);

        bool hasRegisteredServices() {
            return serviceLookup.size() > 0;
        }

        std::string getTopicName(int domainId, int serviceId) {
            return SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
        }

        static ProcessHandler &getInstance();

      private:
        typedef std::map<std::string, std::shared_ptr<ServiceHandler>> ServiceLookup;
        ServiceLookup serviceLookup;
    };

} // namespace InterDomainMessaging

#endif
