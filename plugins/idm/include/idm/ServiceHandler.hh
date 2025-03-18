#ifndef InterDomainMessaging_ServiceHandler_HH
#define InterDomainMessaging_ServiceHandler_HH

#include <functional>
#include <nlohmann/json.hpp>

namespace InterDomainMessaging {

    typedef std::function<void()> Callable;

    class ServiceHandler {
      public:
        virtual Callable getInvoker(nlohmann::json data) const {
            return Callable();
        }
    };

} // namespace InterDomainMessaging

#endif
