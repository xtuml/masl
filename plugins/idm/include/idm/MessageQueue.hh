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

    typedef std::pair<std::string, Value> TaggedMessage;

    class MessageQueue {
      public:
        void enqueue(TaggedMessage &msg);
        TaggedMessage dequeue();
        std::vector<TaggedMessage> dequeue_all();
        bool empty() {
            return queue.empty();
        }

      private:
        std::queue<TaggedMessage> queue;
        mutable std::mutex mutex;
        std::condition_variable cond;
    };

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

} // namespace InterDomainMessaging

#endif
