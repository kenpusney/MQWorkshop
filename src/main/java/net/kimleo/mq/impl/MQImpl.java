package net.kimleo.mq.impl;

import net.kimleo.mq.*;

import java.util.concurrent.ConcurrentHashMap;

public class MQImpl implements MQ {

    private final ConcurrentHashMap<String, Queue> queues = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

    @Override
    public Producer createProducer() {
        return new ProducerImpl();
    }

    @Override
    public Consumer createConsumer() {
        return new ConsumerImpl();
    }

    @Override
    public synchronized Queue createQueue(String id) {
        if(!queues.containsKey(id)) {
            QueueImpl queue = new QueueImpl();
            queues.put(id, queue);
        }
        return queues.get(id);
    }

    @Override
    public synchronized Topic createTopic(String id) {
        if(!topics.containsKey(id)) {
            Topic topic = new TopicImpl();
            topics.put(id, topic);
        }
        return topics.get(id);
    }

    @Override
    public void shutdown() {
        for(Queue queue: queues.values()) {
            queue.destroy();
        }
        queues.clear();
        for(Topic topic: topics.values()) {
            topic.destroy();
        }
        topics.clear();
    }
}
