#ifndef Kafka_ServiceHandler_HH
#define Kafka_ServiceHandler_HH

#include <cstdint>
#include <functional>
#include <vector>

namespace Kafka {

typedef std::function<void()> Callable;

class ServiceHandler {
public:
  virtual Callable getInvoker(std::vector<std::uint8_t> data) const {
    return Callable();
  }
  virtual ~ServiceHandler();
};

} // namespace Kafka

#endif
