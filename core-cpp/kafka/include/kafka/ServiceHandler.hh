#ifndef Kafka_ServiceHandler_HH
#define Kafka_ServiceHandler_HH

#include "BufferedIO.hh"

#include <functional>

namespace Kafka {

class ServiceHandler {
public:
  virtual std::function<void()> getInvoker(BufferedInputStream &buf) const {
    return std::function<void()>();
  }
  virtual ~ServiceHandler();
};

} // namespace Kafka

#endif
