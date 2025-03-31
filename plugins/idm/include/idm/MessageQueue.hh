#ifndef InterDomainMessaging_MessageQueue_HH
#define InterDomainMessaging_MessageQueue_HH

#include <condition_variable>
#include <mutex>
#include <queue>
#include <string>
#include <utility>
#include <vector>

namespace InterDomainMessaging {

    template <class Value>
    class MessageQueue {
      public:
        void enqueue(Value &msg);

        Value dequeue();

        std::vector<Value> dequeue_all();

        bool empty() {
            return queue.empty();
        }

      private:
        std::queue<Value> queue;
        mutable std::mutex mutex;
        std::condition_variable cond;
    };

    template <class Value>
    void MessageQueue<Value>::enqueue(Value &msg) {
        std::lock_guard<std::mutex> lock(mutex);
        queue.push(std::move(msg));
        cond.notify_one();
    }

    template <class Value>
    Value MessageQueue<Value>::dequeue() {
        std::lock_guard<std::mutex> lock(mutex);
        if (queue.empty()) {
            throw std::out_of_range("Queue is empty");
        }
        Value msg = std::move(queue.front());
        queue.pop();
        return msg;
    }

    template <class Value>
    std::vector<Value> MessageQueue<Value>::dequeue_all() {
        std::lock_guard<std::mutex> lock(mutex);
        std::vector<Value> result;
        while (!queue.empty()) {
            result.push_back(std::move(queue.front()));
            queue.pop();
        }
        return result;
    }

} // namespace InterDomainMessaging

#endif
