#ifndef ActiveMQ_ServiceHandler_HH
#define ActiveMQ_ServiceHandler_HH

#include <cstdint>
#include <functional>
#include <vector>

namespace ActiveMQ {

typedef std::function<void()> Callable;

class ServiceHandler {
public:
  virtual Callable getInvoker(std::vector<std::uint8_t> data) const {
    return Callable();
  }
  virtual ~ServiceHandler();
};

} // namespace ActiveMQ

#endif
