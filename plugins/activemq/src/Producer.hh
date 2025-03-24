#ifndef ActiveMQ_Producer_HH
#define ActiveMQ_Producer_HH

#include "idm/Producer.hh"

#include "ProcessHandler.hh"

#include "amqp_asio/condition_var.hh"
#include "amqp_asio/sender.hh"
#include "amqp_asio/session.hh"
#include "logging/log.hh"

#include <asio/any_io_executor.hpp>
#include <future>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Producer : public InterDomainMessaging::Producer {

          public:
            Producer(const std::string topic, ProcessHandler &proc);

            void produce(std::string data) override;

            void produce(std::string data, std::string partKey) override {
                // partition key is ignored in this implementation
                produce(data);
            };

            asio::awaitable<void> isInitialised() {
                return initialisedCond.wait([this] {
                    return initialised;
                });
            }

          private:
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
