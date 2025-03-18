#ifndef InterDomainMessaging_DataConsumer_HH
#define InterDomainMessaging_DataConsumer_HH

#include <nlohmann/json.hpp>

namespace InterDomainMessaging {

    class DataConsumer {
      public:
        virtual void accept(nlohmann::json data) const {}
        virtual ~DataConsumer();
    };

} // namespace InterDomainMessaging

#endif
