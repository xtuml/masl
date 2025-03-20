#ifndef InterDomainMessaging_Producer_HH
#define InterDomainMessaging_Producer_HH

#include <string>

namespace InterDomainMessaging {

    class Producer {

      public:
        virtual void produce(std::string data) = 0;
        virtual void produce(std::string, std::string partKey) = 0;
    };

} // namespace InterDomainMessaging

#endif
