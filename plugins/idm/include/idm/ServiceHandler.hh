#ifndef InterDomainMessaging_ServiceHandler_HH
#define InterDomainMessaging_ServiceHandler_HH

#include <functional>
#include <string>

namespace InterDomainMessaging {

    typedef std::function<void()> Callable;

    class ServiceHandler {
      public:
        virtual Callable getInvoker(std::string data) const {
            return Callable();
        }
    };

} // namespace InterDomainMessaging

#endif
