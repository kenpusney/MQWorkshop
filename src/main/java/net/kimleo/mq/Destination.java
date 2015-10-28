package net.kimleo.mq;

public interface Destination extends Observable<Consumer> {
    <T> void put(Message<T> message);
    void bind(Consumer consumer);
    void detach(Consumer consumer);
    void destroy();
}
