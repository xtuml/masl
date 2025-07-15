#ifndef ActiveMQ_Consumer_HH
#define ActiveMQ_Consumer_HH

#include "idm/Consumer.hh"

#include "ProcessHandler.hh"
#include "activemq/ActiveMQ.hh"
#include "amqp_asio/condition_var.hh"
#include "amqp_asio/delivery.hh"
#include "amqp_asio/receiver.hh"
#include "logging/log.hh"
#include "swa/CommandLine.hh"

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Consumer : public InterDomainMessaging::Consumer {

          public:
            Consumer(std::string topic, ProcessHandler &proc)
                : topic_prefix(SWA::CommandLine::getInstance().getOption(TopicPrefixOption, "topic://")),
                  topic(topic),
                  log("idm.activemq.consumer.{}", topic),
                  initialisedCond(SWA::Process::getInstance().getIOContext().get_executor()),
                  proc(proc) {}
            void receive(std::shared_ptr<ServiceHandler> handler) override;

            asio::awaitable<void> isInitialised() {
                co_await initialisedCond.wait([this] {
                    return initialised;
                });
            }

            std::string getName() {
                return proc.getName() + ".consumer." + topic;
            }

            void setProperty(const std::string &name, int value) override;
            void setProperty(const std::string &name, bool value) override;

          private:
            auto self() const {
                return std::static_pointer_cast<const Consumer>(this->shared_from_this());
            }

            auto self() {
                return std::static_pointer_cast<Consumer>(this->shared_from_this());
            }

          private:
            std::string topic_prefix;
            std::string topic;
            xtuml::logging::Logger log;
            amqp_asio::Receiver receiver;
            bool initialised;
            amqp_asio::ConditionVar initialisedCond;
            ProcessHandler &proc;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
