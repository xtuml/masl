#ifndef Kafka_DataConsumer_HH
#define Kafka_DataConsumer_HH

#include <cstdint>
#include <vector>

namespace Kafka {

class DataConsumer {
public:
  virtual void accept(std::vector<std::uint8_t> data) const {
  }
  virtual ~DataConsumer();
};

} // namespace Kafka

#endif
