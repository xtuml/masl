#ifndef InterDomainMessaging_DataConsumer_HH
#define InterDomainMessaging_DataConsumer_HH

#include <string>

namespace InterDomainMessaging {

    class DataConsumer {
      public:
        virtual void accept(std::string data) const {}
        virtual ~DataConsumer();
    };

} // namespace InterDomainMessaging

#endif
