package net.kimleo.mq.impl;

import net.kimleo.mq.Consumer;
import net.kimleo.mq.Message;
import net.kimleo.mq.Queue;

import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

public class QueueImpl implements Queue {
    private final PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<>(100, Comparable::compareTo);
    private final ConcurrentLinkedQueue<Consumer> consumers = new ConcurrentLinkedQueue<Consumer>();

    @Override
    public <T> Message<T> get() {
        return queue.poll();
    }

    @Override
    public <T> void put(Message<T> message) {
        queue.add(message);
        notifyChanges();
    }

    private synchronized void notifyChanges() {
        if(!consumers.isEmpty() && !queue.isEmpty()) {
            Consumer consumer = consumers.poll();
            consumers.add(consumer);
            consumer.update(this, get());
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
