#ifndef ActiveMQ_Consumer_HH
#define ActiveMQ_Consumer_HH

#include "idm/Consumer.hh"

#include "ProcessHandler.hh"

#include "amqp_asio/delivery.hh"
#include "idm/DataConsumer.hh"
#include "idm/MessageQueue.hh"
#include "logging/log.hh"
#include "swa/RealTimeSignalListener.hh"

namespace InterDomainMessaging {

    namespace ActiveMQ {

        class Consumer : public InterDomainMessaging::Consumer {

          public:
            Consumer(std::string topic, ProcessHandler &proc)
                : topic(topic), log("idm.activemq.consumer.{}", topic), proc(proc) {}
            void receive(std::shared_ptr<ServiceHandler> handler) override;
            bool consumeOne(DataConsumer &dataConsumer) override {
                throw std::logic_error("Not implemented");
            }

          private:
            std::string topic;
            xtuml::logging::Logger log;
            std::unique_ptr<SWA::RealTimeSignalListener> listener;
            MessageQueue<amqp_asio::Delivery> messageQueue;
            ProcessHandler &proc;
        };

    } // namespace ActiveMQ

} // namespace InterDomainMessaging

#endif
