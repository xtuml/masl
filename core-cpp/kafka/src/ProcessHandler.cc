#include "kafka/ProcessHandler.hh"

#include "kafka/Kafka.hh"

#include "swa/CommandLine.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"

namespace Kafka {

bool ProcessHandler::registerServiceHandler(
    int domainId, int serviceId, std::shared_ptr<ServiceHandler> handler) {
  const std::string topicName = getTopicName(domainId, serviceId);
  serviceLookup.insert(ServiceLookup::value_type(topicName, handler));
  return true;
}

ServiceHandler &ProcessHandler::getServiceHandler(int domainId, int serviceId) {
  const std::string topicName = getTopicName(domainId, serviceId);
  return getServiceHandler(topicName);
}
ServiceHandler &ProcessHandler::getServiceHandler(std::string topicName) {
  ServiceLookup::iterator it = serviceLookup.find(topicName);

  if (it == serviceLookup.end()) {
    throw SWA::ProgramError("No Kafka topic registered for '" + topicName + "'");
  }

  return *(it->second);
}

std::vector<std::string> ProcessHandler::getTopicNames() {
  std::vector<std::string> topicNames;
  for (auto it = serviceLookup.begin(); it != serviceLookup.end(); it++) {
    topicNames.push_back(it->first);
  }
  return topicNames;
}

std::string ProcessHandler::getTopicName(int domainId, int serviceId) {
  static const std::string ns = SWA::CommandLine::getInstance().getOption(NamespaceOption);
  if (ns.empty()) {
    return SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
  } else {
    return ns + "." + SWA::Process::getInstance().getDomain(domainId).getName() + "_service" + std::to_string(serviceId);
  }

}

ProcessHandler &ProcessHandler::getInstance() {
  static ProcessHandler instance;
  return instance;
}

namespace {

// Load up the domain handlers once the process has initialised and domains are
// known.
void loadLibs() {
  SWA::Process::getInstance().loadDynamicLibraries("kafka", "_if", false);
}

bool initialise() {
  SWA::Process::getInstance().registerInitialisedListener(&loadLibs);
  return true;
}

bool initialised = initialise();

} // namespace

} // namespace Kafka
