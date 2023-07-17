#ifndef Kafka_ServiceHandler_HH
#define Kafka_ServiceHandler_HH

#include "BufferedIO.hh"

#include <functional>

namespace Kafka {

typedef std::function<void()> Callable;

class ServiceHandler {
public:
  virtual Callable getInvoker(BufferedInputStream &buf) const {
    return Callable();
  }
  virtual ~ServiceHandler();
};

} // namespace Kafka

#endif
