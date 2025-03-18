#ifndef InterDomainMessaging_ProcessHandler_HH
#define InterDomainMessaging_ProcessHandler_HH

#include "Consumer.hh"
#include "Producer.hh"
#include "ServiceHandler.hh"

#include "swa/DynamicSingleton.hh"
#include "swa/Process.hh"

#include <asio/io_context.hpp>

namespace InterDomainMessaging {

    class ProcessHandler : public SWA::DynamicSingleton<ProcessHandler> {
      public:
        virtual std::unique_ptr<Consumer> createConsumer(std::string topic);

        virtual std::unique_ptr<Producer> createProducer(std::string topic);

        bool registerServiceHandler(std::string topic, std::shared_ptr<ServiceHandler> handler);

        std::string getTopicName(int domainId, int serviceId) {
            return SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
        }

        asio::io_context &getContext() {
            return ctx;
        }

        static ProcessHandler &getInstance();

      private:
        asio::io_context ctx;
    };

} // namespace InterDomainMessaging

#endif
