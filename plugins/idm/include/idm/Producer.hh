#ifndef InterDomainMessaging_Producer_HH
#define InterDomainMessaging_Producer_HH

#include <nlohmann/json.hpp>

namespace InterDomainMessaging {

    class Producer {

      public:
        Producer(std::string topic);
        void produce(std::string topic, nlohmann::json data);
        void produce(std::string topic, nlohmann::json data, nlohmann::json partKey);
    };

} // namespace InterDomainMessaging

#endif
