#ifndef InterDomainMessaging_ProcessHandler_HH
#define InterDomainMessaging_ProcessHandler_HH

#include "Consumer.hh"
#include "Producer.hh"
#include "ServiceHandler.hh"

#include "swa/DynamicSingleton.hh"
#include "swa/Process.hh"

#include <functional>
#include <format>
#include <string>

namespace InterDomainMessaging {

    class ProcessHandler : public SWA::DynamicSingleton<ProcessHandler> {
      public:
        template <typename T, typename V>
        using PropertySetter = std::function<void(T &, V)>;


        virtual std::shared_ptr<Consumer> createConsumer(std::string topic) = 0;

        virtual std::shared_ptr<Producer> createProducer(std::string topic) = 0;

        bool registerServiceHandler(std::string id, std::string topic, std::shared_ptr<ServiceHandler> handler) {
            auto consumer = createConsumer(topic);
            consumer->receive(handler);
            consumers.emplace(id,consumer);
            return true;
        }

        static std::string getTopicName(int domainId, int serviceId) {
            return SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
        }

        static ProcessHandler &getInstance();

        static bool hasImplementation();

        std::shared_ptr<Consumer> getConsumer(const std::string& id ) {
            if ( auto it = consumers.find(id); it == consumers.end() ) {
                throw std::out_of_range(std::format("Consumer {} not found", id));
            } else {
                return it->second;
            }
        }

        std::shared_ptr<Producer> getProducer(const std::string& id ) {
            if ( auto it = producers.find(id); it == producers.end() ) {
                throw std::out_of_range(std::format("Producer {} not found", id));
            } else {
                return it->second;
            }
        }

        std::map<std::string, std::shared_ptr<Consumer>> consumers;
        std::map<std::string, std::shared_ptr<Producer>> producers;
    };

} // namespace InterDomainMessaging

#endif
