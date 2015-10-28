package net.kimleo.mq.impl;

import net.kimleo.mq.Destination;
import net.kimleo.mq.Message;
import net.kimleo.mq.Producer;

public class ProducerImpl implements Producer {
    @Override
    public <T> void send(Message<T> message, Destination destination) {
        destination.put(message);
    }
}
