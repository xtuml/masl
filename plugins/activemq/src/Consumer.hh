#ifndef ActiveMQ_Consumer_HH
#define ActiveMQ_Consumer_HH

#include "idm/Consumer.hh"

#include "amqp_asio/delivery.hh"
#include "amqp_asio/session.hh"
#include "idm/DataConsumer.hh"
#include "idm/MessageQueue.hh"
#include "logging/log.hh"
#include "swa/RealTimeSignalListener.hh"

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Consumer : public InterDomainMessaging::Consumer {

          public:
            Consumer(std::string topic, amqp_asio::Session session)
                : topic(topic), log("idm.activemq.consumer.{}", topic), session(session) {}
            void receive(std::shared_ptr<ServiceHandler> handler);
            bool consumeOne(DataConsumer &dataConsumer) {
                throw std::logic_error("Not implemented");
            }

          private:
            std::string topic;
            xtuml::logging::Logger log;
            std::unique_ptr<SWA::RealTimeSignalListener> listener;
            MessageQueue<amqp_asio::Delivery> messageQueue;
            amqp_asio::Session session;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
