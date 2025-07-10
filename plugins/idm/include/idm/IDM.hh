#ifndef InterDomainMessaging_IDM_HH
#define InterDomainMessaging_IDM_HH

#include "swa/Process.hh"

namespace InterDomainMessaging {

    // Load up the domain handlers once the process has initialised and domains are
    // known.
    void loadLibs() {
        SWA::Process::getInstance().loadDynamicLibraries("idm", "_if", false);
    }

} // namespace InterDomainMessaging

#endif
