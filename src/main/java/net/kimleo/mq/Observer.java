package net.kimleo.mq;

public interface Observer<T extends Observable> {
    void update(T observable, Message<?> message);
}
