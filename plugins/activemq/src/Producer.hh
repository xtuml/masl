#ifndef ActiveMQ_Producer_HH
#define ActiveMQ_Producer_HH

#include "idm/Producer.hh"

#include "ProcessHandler.hh"
#include "amqp_asio/condition_var.hh"
#include "amqp_asio/sender.hh"
#include "logging/log.hh"

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Producer : public InterDomainMessaging::Producer {

          public:
            Producer(std::string topic, ProcessHandler &proc);

            void produce(std::string data) override;

            void produce(std::string data, std::string partKey) override {
                // partition key is ignored in this implementation
                produce(data);
            };

            asio::awaitable<void> isInitialised() {
                co_await initialisedCond.wait([this] {
                    return initialised;
                });
            }

            std::string getName() {
                return proc.getName() + ".producer." + topic;
            }

          private:
            auto self() const {
                return std::static_pointer_cast<const Producer>(this->shared_from_this());
            }

            auto self() {
                return std::static_pointer_cast<Producer>(this->shared_from_this());
            }


            std::string topic_prefix;
            std::string topic;
            xtuml::logging::Logger log;
            amqp_asio::Sender sender;
            bool initialised;
            amqp_asio::ConditionVar initialisedCond;
            ProcessHandler &proc;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
