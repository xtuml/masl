#include "kafka/Consumer.hh"

#include "kafka/Kafka.hh"
#include "kafka/ProcessHandler.hh"

#include "swa/CommandLine.hh"
#include "swa/Duration.hh"
#include "swa/Process.hh"
#include "swa/ProgramError.hh"
#include "swa/RealTimeSignalListener.hh"

#include <asio/co_spawn.hpp>
#include <asio/spawn.hpp>
#include <asio/detached.hpp>
#include <uuid/uuid.h>

namespace Kafka {

void Consumer::initialize(std::vector<std::string> topics) {

  auto log = ProcessHandler::getInstance().getLog();

  // create a signal listener
  SWA::RealTimeSignalListener listener(
      [this](int pid, int uid) { this->handleMessages(); },
      SWA::Process::getInstance().getActivityMonitor());

  // for each topic, create a receiver and spawn a listen loop
  for (auto it = topics.begin(); it != topics.end(); it++) {
    auto topic = *it;
    auto executor = ProcessHandler::getInstance().getContext().get_executor();

    asio::co_spawn(executor, [&]() -> asio::awaitable<void> {

      log.info("LEVI91");
      auto receiver = co_await ProcessHandler::getInstance().getSession().open_receiver(topic, amqp_asio::ReceiverOptions().name("TODO: receiver"));
      log.info("LEVI92");
      amqp_asio::spawn_cancellable_loop(
        executor,
        [&]() -> asio::awaitable<void> {
          log.info("LEVI93");
          // Queue the message to be handled on the main thread
          auto delivery = co_await receiver.receive();
          log.info("LEVI94");
          log.info("Received message {}", delivery.message().as_string());
          TaggedMessage msg(topic, delivery);
          log.info("LEVI95");
          messageQueue.enqueue(msg);
          log.info("LEVI96");
          listener.queueSignal();
          log.info("LEVI97");
        },
        log
      );



    }, asio::detached);

  }

}

void Consumer::handleMessages() {
  // drain the message queue
  if (!messageQueue.empty()) {
    std::vector<TaggedMessage> msgs = messageQueue.dequeue_all();
    for (auto it = msgs.begin(); it != msgs.end(); it++) {
      TaggedMessage msg = std::move(*it);

      // get the service invoker
      std::vector<std::byte> payload = msg.second.message().as_bytes();
      std::vector<uint8_t> msg_bytes(payload.size());
      for (size_t i = 0; i < payload.size(); ++i) {
        msg_bytes[i] = static_cast<uint8_t>(payload[i]);
      }
      Callable service = ProcessHandler::getInstance().getServiceHandler(msg.first).getInvoker(msg_bytes);

      // run the service
      service();

      // accept delivery
      auto executor = ProcessHandler::getInstance().getContext().get_executor();
      asio::co_spawn(executor, [&]() -> asio::awaitable<void> { co_await msg.second.accept(); }, asio::detached);
    }
  }
}

bool Consumer::consumeOne(DataConsumer& dataConsumer) {
  // TODO rewrite this for the amqp mechanism
  return false;
}

void MessageQueue::enqueue(TaggedMessage &msg) {
  std::lock_guard<std::mutex> lock(mutex);
  queue.push(std::move(msg));
  cond.notify_one();
}

TaggedMessage MessageQueue::dequeue() {
  std::lock_guard<std::mutex> lock(mutex);
  if (queue.empty()) {
    throw std::out_of_range("Queue is empty");
  }
  TaggedMessage msg = std::move(queue.front());
  queue.pop();
  return msg;
}

std::vector<TaggedMessage> MessageQueue::dequeue_all() {
  std::lock_guard<std::mutex> lock(mutex);
  std::vector<TaggedMessage> result;
  while (!queue.empty()) {
    result.push_back(std::move(queue.front()));
    queue.pop();
  }
  return result;
}

} // namespace Kafka
