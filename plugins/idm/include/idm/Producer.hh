#ifndef InterDomainMessaging_Producer_HH
#define InterDomainMessaging_Producer_HH

#include <string>
#include <memory>

namespace InterDomainMessaging {

    class Producer : public std::enable_shared_from_this<Producer> {

      public:
        virtual void produce(std::string data) = 0;
        virtual void produce(std::string, std::string partKey) = 0;

        virtual void setProperty(const std::string& name, int value) {}
        virtual void setProperty(const std::string& name, std::string value) {}
        virtual void setProperty(const std::string& name, bool value) {}

    };

} // namespace InterDomainMessaging

#endif
