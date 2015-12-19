package net.kimleo.mq;

public interface Consumer extends Observer<Destination> {
    <T> void acceptOnce(Destination destination, Handler<Message<T>> handler);
    <T> void listen(Destination destination, Handler<Message<T>> handler);
    void update(Destination destination, Message message);
}
