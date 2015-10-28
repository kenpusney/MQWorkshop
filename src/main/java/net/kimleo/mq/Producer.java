package net.kimleo.mq;

public interface Producer {
    <T> void send(Message<T> message, Destination destination);
}
