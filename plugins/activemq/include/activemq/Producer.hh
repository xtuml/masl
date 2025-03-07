#ifndef ActiveMQ_Producer_HH
#define ActiveMQ_Producer_HH

#include <cstdint>
#include <string>
#include <vector>

namespace ActiveMQ {

class Producer {

public:
  void publish(int domainId, int serviceId, std::vector<std::uint8_t> data, std::vector<std::uint8_t> partKey);
  void publish(int domainId, int serviceId, std::string data, std::string partKey);
  void publish(int domainId, int serviceId, std::vector<std::uint8_t> data);
  void publish(int domainId, int serviceId, std::string data);
  static Producer &getInstance();

private:
  typedef std::pair<int, int> ServiceKey;
};

} // namespace ActiveMQ

#endif
