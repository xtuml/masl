#ifndef ActiveMQ_DataConsumer_HH
#define ActiveMQ_DataConsumer_HH

#include <cstdint>
#include <vector>

namespace ActiveMQ {

class DataConsumer {
public:
  virtual void accept(std::vector<std::uint8_t> data) const {
  }
  virtual ~DataConsumer();
};

} // namespace ActiveMQ

#endif
