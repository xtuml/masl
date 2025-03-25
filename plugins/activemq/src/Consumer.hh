#ifndef ActiveMQ_Consumer_HH
#define ActiveMQ_Consumer_HH

#include "idm/Consumer.hh"

#include "ProcessHandler.hh"

#include "amqp_asio/delivery.hh"
#include "idm/DataConsumer.hh"
#include "logging/log.hh"

#include <queue>

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Consumer : public InterDomainMessaging::Consumer {

          public:
            Consumer(std::string topic, ProcessHandler &proc)
                : topic(topic), log("idm.activemq.consumer.{}", topic), proc(proc) {}
            void receive(std::shared_ptr<ServiceHandler> handler) override;

          private:
            std::string topic;
            xtuml::logging::Logger log;
            std::queue<amqp_asio::Delivery> messageQueue;
            ProcessHandler &proc;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
