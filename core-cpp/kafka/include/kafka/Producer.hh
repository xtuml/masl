#ifndef Kafka_Producer_HH
#define Kafka_Producer_HH

#include "amqp_asio/sender.hh"
#include "amqp_asio/session.hh"

namespace Kafka {

class Producer {

public:
  Producer(amqp_asio::Session &session);
  void publish(int domainId, int serviceId, std::vector<std::uint8_t> data, std::vector<std::uint8_t> partKey);
  void publish(int domainId, int serviceId, std::string data, std::string partKey);
  void publish(int domainId, int serviceId, std::vector<std::uint8_t> data);
  void publish(int domainId, int serviceId, std::string data);
  static Producer &getInstance();

private:
  typedef std::pair<int, int> ServiceKey;
  std::shared_ptr<amqp_asio::Sender> prod;
};

} // namespace Kafka

#endif
