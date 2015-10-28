package net.kimleo.mq.impl;

import net.kimleo.mq.Consumer;
import net.kimleo.mq.Message;
import net.kimleo.mq.Topic;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class TopicImpl implements Topic {
    private final PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<>(100, Comparable::compareTo);
    private final ConcurrentLinkedQueue<Consumer> consumers = new ConcurrentLinkedQueue<>();


    @Override
    public <T> void put(Message<T> message) {
        queue.add(message);
        notifyChanges();
    }

    private synchronized void notifyChanges() {
        if(!queue.isEmpty()) {
            Message message = queue.poll();
            for(Consumer consumer: consumers) {
                consumer.update(this, message);
            }
        }
    }

    @Override
    public void bind(Consumer consumer) {
        consumers.add(consumer);
        notifyChanges();
    }

    @Override
    public void detach(Consumer consumer) {
        consumers.remove(consumer);
    }

    @Override
    public void destroy() {
    }
}
