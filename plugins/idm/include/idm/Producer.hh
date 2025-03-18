#ifndef InterDomainMessaging_Producer_HH
#define InterDomainMessaging_Producer_HH

#include <nlohmann/json.hpp>

namespace InterDomainMessaging {

    class Producer {

      public:
        virtual void produce(nlohmann::json data);
        virtual void produce(nlohmann::json data, nlohmann::json partKey);
    };

} // namespace InterDomainMessaging

#endif
