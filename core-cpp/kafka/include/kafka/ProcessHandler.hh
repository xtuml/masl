#ifndef Kafka_ProcessHandler_HH
#define Kafka_ProcessHandler_HH

#include "ServiceHandler.hh"

#include <map>
#include <memory>
#include <string>
#include <vector>

namespace Kafka {

class ProcessHandler {
public:
  bool registerServiceHandler(int domainId, int serviceId,
                              std::shared_ptr<ServiceHandler> handler);

  ServiceHandler &getServiceHandler(int domainId, int serviceId);

  ServiceHandler &getServiceHandler(std::string topicName);

  std::vector<std::string> getTopicNames();

  bool hasRegisteredServices() { return serviceLookup.size() > 0; }

  bool setCustomTopicName(int domainId, int serviceId, std::string topicName);

  std::string getTopicName(int domainId, int serviceId);

  static ProcessHandler &getInstance();



private:
  typedef std::map<std::string, std::shared_ptr<ServiceHandler>> ServiceLookup;
  typedef std::map<std::pair<int, int>, std::string> TopicMap;

  ServiceLookup serviceLookup;
  TopicMap customTopicNames;
};

} // namespace Kafka

#endif
