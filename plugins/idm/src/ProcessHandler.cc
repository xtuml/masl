#include "idm/ProcessHandler.hh"

namespace InterDomainMessaging {

    bool ProcessHandler::registerServiceHandler(std::string topic, std::shared_ptr<ServiceHandler> handler) {
        auto consumer = createConsumer(topic);
        consumer->receive(handler);
        consumers.push_back(std::move(consumer));
        return true;
    }

    ProcessHandler &ProcessHandler::getInstance() {
        return getSingleton();
    }

} // namespace InterDomainMessaging

namespace {

    // Load up the domain handlers once the process has initialised and domains are
    // known.
    void loadLibs() {
        SWA::Process::getInstance().loadDynamicLibraries("idm", "_if", false);
    }

    bool initialise() {
        SWA::Process::getInstance().registerInitialisedListener(&loadLibs);
        return true;
    }

    bool initialised = initialise();

} // namespace
