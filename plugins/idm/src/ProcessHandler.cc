#include "idm/ProcessHandler.hh"

namespace InterDomainMessaging {


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
