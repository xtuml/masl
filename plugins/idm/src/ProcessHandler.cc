#include "idm/ProcessHandler.hh"

namespace InterDomainMessaging {

    bool ProcessHandler::registerServiceHandler(std::string topic, std::shared_ptr<ServiceHandler> handler) {
        auto consumer = createConsumer(topic);
        consumer->receive(handler);
        return true;
    }

} // namespace InterDomainMessaging
