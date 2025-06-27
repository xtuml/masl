#include "idm/ProcessHandler.hh"

namespace InterDomainMessaging {


    ProcessHandler &ProcessHandler::getInstance() {
        return getSingleton();
    }

    bool ProcessHandler::hasImplementation() {
        return singletonRegistered();
    }

} // namespace InterDomainMessaging
